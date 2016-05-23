package com.myapp.dbconfig.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.myapp.dbconfig.entity.User;


@Repository
public class UserDao extends AbstractDao<Integer, User> {
	
	public User findByUsername(String username) {
		Criteria criteria = createEntityCriteria();
		criteria.add(Restrictions.eq("username", username));
		return (User) criteria.uniqueResult();
	}
}
