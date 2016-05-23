package com.myapp.dbconfig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myapp.dbconfig.dao.UserDao;
import com.myapp.dbconfig.entity.User;

@Transactional("txnManagersource")
@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}
	
	
}
