package com.myapp.dbconfig.dao;

import org.springframework.stereotype.Repository;

import com.myapp.dbconfig.entity.TestModel;

@Repository("testRepository")
public class TestDao extends AbstractDao<Integer, TestModel> {
	public TestModel findById(int id) {
		return getByKey(id);
	}
}
