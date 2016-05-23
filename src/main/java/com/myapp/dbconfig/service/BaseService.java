package com.myapp.dbconfig.service;

import org.springframework.stereotype.Service;

@Service
public class BaseService {
	private String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
