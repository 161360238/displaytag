package cn.wj.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.wj.bean.Customer;
import cn.wj.bean.PageBean;
import cn.wj.service.CustomerService;

public class CustomerServlet extends HttpServlet {
	CustomerService customerService=new CustomerService();
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			String method=request.getParameter("method");
			if("findAll".equals(method)){
				findAll(request,response);
			}else if("query".equals(method)){
				query(request,response);
			}
	}

	/**
	 * 查询所有的员工
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void findAll(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1，获取前台传过来的pagecode
		 * 2，给定ps的值
		 * 3，使用pc,和ps调用server层，得到Pagebean，保存到resuest
		 */
		int pc=getPc(request);
		int ps=10;    //每页显示10条数据
		
		PageBean<Customer> pb=customerService.findAllServer(pc,ps);
		//System.out.print(pb);
		pb.setUrl(getUrl(request));
		request.setAttribute("pb", pb);
		request.getRequestDispatcher("/list.jsp").forward(request, response);
	}

	public void query(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		System.out.println(getUrl(request));
		/*
		 * 0. 把条件封装到Customer对象中
		 * 1. 得到pc
		 * 2. 给定ps
		 * 3. 使用pc和ps，以及条件对象，调用service方法得到PageBean
		 * 4. 把PageBean保存到request域中
		 * 5. 转发到list.jsp
		 */
		// 获取查询条件
		Customer criteria = CommonUtils.toBean(request.getParameterMap(), Customer.class);
		
		/*
		 * 处理GET请求方式编码问题！
		 */
		criteria = encoding(criteria);
		
		int pc = getPc(request);//得到pc
		int ps = 10;//给定ps的值，第页10行记录
		PageBean<Customer> pb = customerService.query(criteria, pc, ps);
		
		// 得到url，保存到pb中
		pb.setUrl(getUrl(request));
		
		request.setAttribute("pb", pb);

		 request.getRequestDispatcher("/list.jsp").forward(request, response);
	}
	
	/**
	 * 处理参数编码问题
	 * @param criteria
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private Customer encoding(Customer criteria) throws UnsupportedEncodingException {
		String cname = criteria.getCname();
		String gender = criteria.getGender();
		String cellphone = criteria.getCellphone();
		String email = criteria.getEmail();
		
		if(cname != null && !cname.trim().isEmpty()) {
		//	cname = new String(cname.getBytes("ISO-8859-1"), "utf-8");
			criteria.setCname(cname);
		}
		
		if(gender != null && !gender.trim().isEmpty()) {
			//gender = new String(gender.getBytes("ISO-8859-1"), "utf-8");
			criteria.setGender(gender);
		}
		
		if(cellphone != null && !cellphone.trim().isEmpty()) {
			//cellphone = new String(cellphone.getBytes("ISO-8859-1"), "utf-8");
			criteria.setCellphone(cellphone);
		}
		
		if(email != null && !email.trim().isEmpty()) {
		//	email = new String(email.getBytes("ISO-8859-1"), "utf-8");
			criteria.setEmail(email);
		}
		return criteria;
	}	
	
	
	/**
	 * 得到前台传过来的页码pc
	 * @param request
	 * @return
	 */
	private int getPc(HttpServletRequest request) {
		String pc=request.getParameter("pc");
		if(pc==null||pc.trim().isEmpty()){
			return 1;
		}	
		return Integer.parseInt(pc);
	}
	
	/**
	 * 设置请求的url，防止查询条件丢失
	 * @param request
	 * @return
	 */
	private String getUrl(HttpServletRequest request){
		String contextPath=request.getContextPath(); //获取项目名
		String servletpath=request.getServletPath(); //获取servletPath，即/CustomerServlet
		String queryString=request.getQueryString();  //获取问号后的参数部分
		
		if(queryString.contains("&pc=")){
			int index=queryString.lastIndexOf("&pc=");
			queryString=queryString.substring(0,index);
		}
		return contextPath+servletpath+"?"+queryString;
	}
}
