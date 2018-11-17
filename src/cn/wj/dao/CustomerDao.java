package cn.wj.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import cn.wj.bean.Customer;
import cn.wj.bean.PageBean;

import com.mysql.jdbc.PreparedStatement;

public class CustomerDao {
	private QueryRunner qr = new TxQueryRunner();// 手动支持事物的QueryRunner

	/**
	 * 查询所有客户
	 * 
	 * @param pc
	 * @param ps
	 * @return
	 */
	public PageBean<Customer> findAllDao(int pc, int ps) {
		try {
			/*
			 * 1，创建PageBean对象pb 2，设置pb的pc和ps 3，得到tr（总记录数）设置给pb
			 * 4，得到beanlist，设置给pb 5，返回pb
			 */
			PageBean<Customer> pb = new PageBean<Customer>();
			pb.setPc(pc);
			pb.setPs(ps);
			String sql = "select count(*) from t_customer";
			Number num = (Number) qr.query(sql, new ScalarHandler());
			int tr = num.intValue();
			pb.setTr(tr);
			/*
			 * 查询到beanlist
			 */
			sql = "select * from t_customer order by cname limit ?,?";
			List<Customer> beanList = qr.query(sql,
					new BeanListHandler<Customer>(Customer.class), (pc - 1)
							* ps, ps);
			pb.setBeanList(beanList);
			return pb;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 批处理添加用户
	 * @throws ClassNotFoundException
	 */
	@Test
	public void fun1() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		// 加载驱动类
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager
				.getConnection(
						"jdbc:mysql://localhost:3306/customers?rewriteBatchedStatements=true",
						"root", "161360238");
		String sql = "insert into t_customer (cid,cname,gender,birthday,cellphone,email,description)"
				+ " values(?,?,?,?,?,?,?)";
		stmt = (PreparedStatement) conn.prepareStatement(sql);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1006; i++) {
			if (i % 2 == 0) {
				stmt.setObject(1, CommonUtils.uuid());
				stmt.setObject(2, "user" + 1);
				stmt.setObject(3, "男");
				stmt.setObject(4,
						new Date(System.currentTimeMillis()).toString());
				stmt.setObject(5, "123123123");
				stmt.setObject(6, "123123123@qq.com");
				stmt.setObject(7, "锄禾日当午");
				stmt.execute();
			} else {
				stmt.setObject(1, CommonUtils.uuid());
				stmt.setObject(2, "user" + 1);
				stmt.setObject(3, "女");
				stmt.setObject(4,
						new Date(System.currentTimeMillis()).toString());
				stmt.setObject(5, "123123123");
				stmt.setObject(6, "123123123@qq.com");
				stmt.setObject(7, "汗滴禾下土");
				stmt.execute();
			}
		}
		stmt.executeBatch();
		long end = System.currentTimeMillis();
		System.out.println("插入1006条数据，耗时(毫秒)：" + (end - start));
	}

	public PageBean<Customer> query(Customer criteria, int pc, int ps) {
		try {
			/*
			 * 1. 创建PageBean对象　
			 * 2. 设置已有的属性，pc和ps
			 * 3. 得到tr
			 * 4. 得到beanList
			 */
			/*
			 * 创建pb，设置已有属性
			 */
			PageBean<Customer> pb = new PageBean<Customer>();
			pb.setPc(pc);
			pb.setPs(ps);
			
			/*
			 * 得到tr
			 */
			
			/*
			 * 1. 给出一个sql语句前半部
			 */
			StringBuilder cntSql = new StringBuilder("select count(*) from t_customer");
			StringBuilder whereSql = new StringBuilder(" where 1=1");
			/*
			 * 2. 判断条件，完成向sql中追加where子句
			 */
			/*
			 * 3. 创建一个ArrayList，用来装载参数值
			 */
			List<Object> params = new ArrayList<Object>();
			String cname = criteria.getCname();
			if(cname != null && !cname.trim().isEmpty()) {
				whereSql.append(" and cname like ?");
				params.add("%" + cname + "%");
			}
			
			String gender = criteria.getGender();
			if(gender != null && !gender.trim().isEmpty()) {
				whereSql.append(" and gender=?");
				params.add(gender);
			}
			
			String cellphone = criteria.getCellphone();
			if(cellphone != null && !cellphone.trim().isEmpty()) {
				whereSql.append(" and cellphone like ?");
				params.add("%" + cellphone + "%");
			}
			
			String email = criteria.getEmail();
			if(email != null && !email.trim().isEmpty()) {
				whereSql.append(" and email like ?");
				params.add("%" + email + "%");
			}
			
			/*
			 * select count(*) .. + where子句
			 * 执行之
			 */
			Number num = (Number)qr.query(cntSql.append(whereSql).toString(), 
					new ScalarHandler(), params.toArray());
			int tr = num.intValue();
			pb.setTr(tr);
			
			/*
			 * 得到beanList
			 */
			StringBuilder sql = new StringBuilder("select * from t_customer");
			// 我们查询beanList这一步，还需要给出limit子句
			StringBuilder limitSql = new StringBuilder(" limit ?,?");
			// params中需要给出limit后两个问号对应的值
			params.add((pc-1)*ps);
			params.add(ps);
			// 执行之
			List<Customer> beanList = qr.query(sql.append(whereSql).append(limitSql).toString(), 
					new BeanListHandler<Customer>(Customer.class), 
					params.toArray());
			pb.setBeanList(beanList);
			
			return pb;
			
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
