
package com.myapp.dbconfig.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.myapp.dbconfig" })
@PropertySources(value = {
		@PropertySource("classpath:/source.properties"),
		@PropertySource("classpath:/sqlserver.properties"),
		@PropertySource("classpath:/mysql.properties"),
		@PropertySource("classpath:/hibernate.properties")})
public class HibernateConfiguration //implements BeanDefinitionRegistryPostProcessor,  {
implements ApplicationContextAware{
    @Autowired
    private Environment environment;
    private String source;
    private BeanDefinitionRegistry beanRegistry;
    private ApplicationContext context;
    @PostConstruct
    private void setSource() {
    	this.setSource(environment.getRequiredProperty("datasource"));
    }
    
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "com.myapp.dbconfig.entity" });
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
     }
	
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty(this.getSource()+".driver"));
        dataSource.setUrl(environment.getRequiredProperty(this.getSource()+".url"));
        dataSource.setUsername(environment.getRequiredProperty(this.getSource()+".username"));
        dataSource.setPassword(environment.getRequiredProperty(this.getSource()+".password"));
        return dataSource;
    }
    
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        return properties;        
    }
    
	@Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
       HibernateTransactionManager txManager = new HibernateTransactionManager();
       txManager.setSessionFactory(s);
       return txManager;
    }

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	//@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		
		this.beanRegistry = registry;
	}
	
	public void register(String beanName, Class<?> clazz, Object object) {
		RootBeanDefinition beanDef = new RootBeanDefinition(clazz);
		beanDef.setSource(object);
		beanRegistry.registerBeanDefinition(beanName, beanDef);
	}
	
	public <T> T getBean(String name, Class<T> clazz ) {
		return context.getBean(name, clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
	
	public void addTransactionManager(String sourceName, Map<String,String> map) {
		feedBean(context, DriverManagerDataSource.class, sourceName, map,null, DataSource.class);
		
		Map<String,String> sessionFactoryBeanMap = new HashMap<String,String>();
		sessionFactoryBeanMap.put("dataSource", sourceName);
		Map<String,String> sessionFactoryPropMap = new HashMap<String,String>();		
		sessionFactoryPropMap.put("packagesToScan", "com.myapp.dbconfig.entity");
		//sessionFactoryPropMap.put("hibernateProperties", "{\"hibernate.show_sql\":\"true\"}");
		feedBean(context, LocalSessionFactoryBean.class, "sessionFactory"+sourceName, sessionFactoryPropMap,sessionFactoryBeanMap, SessionFactory.class);
		
		
		Map<String,String> txnBeanMap = new HashMap<String,String>();
		txnBeanMap.put("sessionFactory", "sessionFactory"+sourceName);
		feedBean(context, HibernateTransactionManager.class, "txnManager"+sourceName, null,txnBeanMap, HibernateTransactionManager.class);

	}
	
	private static <T,E> E feedBean(ApplicationContext context, Class<T> clazz, String beanName, Map<String,String> map, Map<String,String> beanMap, Class<E> eClazz) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
		if(beanMap != null) {
			for(Map.Entry<String, String> entry: beanMap.entrySet()) {
				builder.addPropertyReference(entry.getKey(), entry.getValue());
			}
		}
		if(map != null ) {
			for(Map.Entry<String, String> entry: map.entrySet()) {
				builder.addPropertyValue(entry.getKey(), entry.getValue());
			}
		}
		AbstractApplicationContext acontext = (AbstractApplicationContext)context;
		DefaultListableBeanFactory factory = (DefaultListableBeanFactory)acontext.getBeanFactory();
		factory.registerBeanDefinition(beanName, builder.getBeanDefinition());
		return context.getBean(beanName, eClazz);
	}
	
}