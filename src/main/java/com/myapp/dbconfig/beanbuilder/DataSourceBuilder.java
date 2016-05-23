package com.myapp.dbconfig.beanbuilder;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.myapp.dbconfig.dto.DataSourceDTO;

public class DataSourceBuilder {
	public static DataSource getDataSource(DataSourceDTO dto) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dto.getDriver());
        dataSource.setUrl(dto.getUrl());
        dataSource.setUsername(dto.getUsername());
        dataSource.setPassword(dto.getPassword());
        return dataSource;
	}
}
