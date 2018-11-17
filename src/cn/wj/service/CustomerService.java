package cn.wj.service;

import cn.wj.bean.Customer;
import cn.wj.bean.PageBean;
import cn.wj.dao.CustomerDao;
public class CustomerService {
	CustomerDao customerDao=new CustomerDao();

	public PageBean<Customer> findAllServer(int pc, int ps) {
		return customerDao.findAllDao(pc,ps);
	}

	/**
	 * 多条件组合查询
	 * @param criteria
	 * @return
	 */
	public PageBean<Customer> query(Customer criteria, int pc, int ps) {
		return customerDao.query(criteria, pc, ps);
	}

}
