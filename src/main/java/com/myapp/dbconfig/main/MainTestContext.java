package com.myapp.dbconfig.main;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.myapp.dbconfig.config.HibernateConfiguration;
import com.myapp.dbconfig.entity.User;
import com.myapp.dbconfig.service.UserService;

public class MainTestContext {
	public static void main(String args[]) {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(new Class<?>[]{
		HibernateConfiguration.class});
		//context.register(SpringMvcConfiguration.class);
		
		HibernateConfiguration hc = context.getBean(HibernateConfiguration.class);
		Map<String,String> map = new HashMap<String,String>();
		map.put("driverClassName", "com.mysql.jdbc.Driver");
		map.put("url", "jdbc:mysql://localhost:3306/springboot?createDatabaseIfNotExist=true");
		map.put("username", "root");
		map.put("password", "root");
		hc.addTransactionManager("source", map);
		
		 
		DataSource ds = context.getBean("source", DataSource.class);
		map.clear();
		map.put("driverClassName", "com.mysql.jdbc.Driver");
		map.put("url", "jdbc:mysql://localhost:3306/springboot1?createDatabaseIfNotExist=true");
		map.put("username", "root");
		map.put("password", "root");
		hc.addTransactionManager("source1", map);
		
		//DataSource ds1 = context.getBean("source1", DataSource.class);
		//System.out.println(ds);
		UserService us = context.getBean(UserService.class);
		User user = us.findByUsername("user");
		System.out.println(user);
		
		context.close();
	}

	/*private static void feedBean(AbstractApplicationContext context, Class<?> clazz, String beanName, String nameProp) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
		  builder.addPropertyReference("propertyName", "someBean");  // add dependency to other bean 
		 builder.addPropertyValue("name", nameProp);      // set property value
		 DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getBeanFactory();
		 factory.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}*/

}
