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
	private QueryRunner qr = new TxQueryRunner();// �ֶ�֧�������QueryRunner

	/**
	 * ��ѯ���пͻ�
	 * 
	 * @param pc
	 * @param ps
	 * @return
	 */
	public PageBean<Customer> findAllDao(int pc, int ps) {
		try {
			/*
			 * 1������PageBean����pb 2������pb��pc��ps 3���õ�tr���ܼ�¼�������ø�pb
			 * 4���õ�beanlist�����ø�pb 5������pb
			 */
			PageBean<Customer> pb = new PageBean<Customer>();
			pb.setPc(pc);
			pb.setPs(ps);
			String sql = "select count(*) from t_customer";
			Number num = (Number) qr.query(sql, new ScalarHandler());
			int tr = num.intValue();
			pb.setTr(tr);
			/*
			 * ��ѯ��beanlist
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
	 * ����������û�
	 * @throws ClassNotFoundException
	 */
	@Test
	public void fun1() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		// ����������
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
				stmt.setObject(3, "��");
				stmt.setObject(4,
						new Date(System.currentTimeMillis()).toString());
				stmt.setObject(5, "123123123");
				stmt.setObject(6, "123123123@qq.com");
				stmt.setObject(7, "�����յ���");
				stmt.execute();
			} else {
				stmt.setObject(1, CommonUtils.uuid());
				stmt.setObject(2, "user" + 1);
				stmt.setObject(3, "Ů");
				stmt.setObject(4,
						new Date(System.currentTimeMillis()).toString());
				stmt.setObject(5, "123123123");
				stmt.setObject(6, "123123123@qq.com");
				stmt.setObject(7, "���κ�����");
				stmt.execute();
			}
		}
		stmt.executeBatch();
		long end = System.currentTimeMillis();
		System.out.println("����1006�����ݣ���ʱ(����)��" + (end - start));
	}

	public PageBean<Customer> query(Customer criteria, int pc, int ps) {
		try {
			/*
			 * 1. ����PageBean����
			 * 2. �������е����ԣ�pc��ps
			 * 3. �õ�tr
			 * 4. �õ�beanList
			 */
			/*
			 * ����pb��������������
			 */
			PageBean<Customer> pb = new PageBean<Customer>();
			pb.setPc(pc);
			pb.setPs(ps);
			
			/*
			 * �õ�tr
			 */
			
			/*
			 * 1. ����һ��sql���ǰ�벿
			 */
			StringBuilder cntSql = new StringBuilder("select count(*) from t_customer");
			StringBuilder whereSql = new StringBuilder(" where 1=1");
			/*
			 * 2. �ж������������sql��׷��where�Ӿ�
			 */
			/*
			 * 3. ����һ��ArrayList������װ�ز���ֵ
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
			 * select count(*) .. + where�Ӿ�
			 * ִ��֮
			 */
			Number num = (Number)qr.query(cntSql.append(whereSql).toString(), 
					new ScalarHandler(), params.toArray());
			int tr = num.intValue();
			pb.setTr(tr);
			
			/*
			 * �õ�beanList
			 */
			StringBuilder sql = new StringBuilder("select * from t_customer");
			// ���ǲ�ѯbeanList��һ��������Ҫ����limit�Ӿ�
			StringBuilder limitSql = new StringBuilder(" limit ?,?");
			// params����Ҫ����limit�������ʺŶ�Ӧ��ֵ
			params.add((pc-1)*ps);
			params.add(ps);
			// ִ��֮
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
