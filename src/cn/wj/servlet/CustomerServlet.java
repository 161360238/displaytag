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
	 * ��ѯ���е�Ա��
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void findAll(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1����ȡǰ̨��������pagecode
		 * 2������ps��ֵ
		 * 3��ʹ��pc,��ps����server�㣬�õ�Pagebean�����浽resuest
		 */
		int pc=getPc(request);
		int ps=10;    //ÿҳ��ʾ10������
		
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
		 * 0. ��������װ��Customer������
		 * 1. �õ�pc
		 * 2. ����ps
		 * 3. ʹ��pc��ps���Լ��������󣬵���service�����õ�PageBean
		 * 4. ��PageBean���浽request����
		 * 5. ת����list.jsp
		 */
		// ��ȡ��ѯ����
		Customer criteria = CommonUtils.toBean(request.getParameterMap(), Customer.class);
		
		/*
		 * ����GET����ʽ�������⣡
		 */
		criteria = encoding(criteria);
		
		int pc = getPc(request);//�õ�pc
		int ps = 10;//����ps��ֵ����ҳ10�м�¼
		PageBean<Customer> pb = customerService.query(criteria, pc, ps);
		
		// �õ�url�����浽pb��
		pb.setUrl(getUrl(request));
		
		request.setAttribute("pb", pb);

		 request.getRequestDispatcher("/list.jsp").forward(request, response);
	}
	
	/**
	 * ���������������
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
	 * �õ�ǰ̨��������ҳ��pc
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
	 * ���������url����ֹ��ѯ������ʧ
	 * @param request
	 * @return
	 */
	private String getUrl(HttpServletRequest request){
		String contextPath=request.getContextPath(); //��ȡ��Ŀ��
		String servletpath=request.getServletPath(); //��ȡservletPath����/CustomerServlet
		String queryString=request.getQueryString();  //��ȡ�ʺź�Ĳ�������
		
		if(queryString.contains("&pc=")){
			int index=queryString.lastIndexOf("&pc=");
			queryString=queryString.substring(0,index);
		}
		return contextPath+servletpath+"?"+queryString;
	}
}
