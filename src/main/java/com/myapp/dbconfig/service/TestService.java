package com.myapp.dbconfig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myapp.dbconfig.dao.TestDao;
import com.myapp.dbconfig.entity.TestModel;

@Service("testService")
@Transactional
public class TestService {
	
	@Autowired
	private TestDao testDao;

	public TestModel getTest() {
		return testDao.findById(1);
	}
}
