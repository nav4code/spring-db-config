package com.myapp.dbconfig.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myapp.dbconfig.beanbuilder.DataSourceBuilder;
import com.myapp.dbconfig.config.HibernateConfiguration;
import com.myapp.dbconfig.dto.DataSourceDTO;
import com.myapp.dbconfig.entity.TestModel;
import com.myapp.dbconfig.entity.User;
import com.myapp.dbconfig.service.TestService;
import com.myapp.dbconfig.service.UserService;
import com.myapp.dbconfig.util.PasswordUtil;
import com.myapp.dbconfig.util.SAMLValidateUtil;

@Controller
@PropertySources(value = {
		@PropertySource("classpath:/sso.properties")})
public class TestController {
	
	@Autowired
	private TestService testService;
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordUtil passwordUtil;
	@Autowired
	HibernateConfiguration config;
/*	@Autowired
	private SAMLProtocolResponseValidator validator;
*/
	@Autowired
	Environment environment;
	@Autowired
	SAMLValidateUtil samlUtil;
	
	@RequestMapping(value = "/ws", method = RequestMethod.GET)
	@ResponseBody
	public TestModel getTest() {
		return testService.getTest();
	}
	
	@RequestMapping(value = "/datasource", method = RequestMethod.POST)
	@ResponseBody
	public String createDataSource(@RequestBody DataSourceDTO dto) {
		DataSource datasource = DataSourceBuilder.getDataSource(dto);
		config.register(dto.getName(), DataSource.class, datasource);
		DataSource source = config.getBean(dto.getName(), DataSource.class);
		System.out.println(source.getClass());
		return "true";
	}
	
	@RequestMapping(value = "/comein", method = RequestMethod.POST)
	@ResponseBody
	public String comein(@RequestParam String SAMLResponse) {
		System.out.println("saml response: " + SAMLResponse);
		Map<String,String> claims = samlUtil.getInfo(SAMLResponse);
		if(claims == null) { 
			return "failure";
		} else {
			return "successfully verified with user: " + claims.get("username"); 
		}
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public String login(@RequestParam(required=false) String username,
			@RequestParam(required=false) String password,
			@RequestParam(required=false) String sso,
			HttpServletRequest request,
			HttpServletResponse response) {
		if(!StringUtils.isEmpty(sso)) {
			System.out.println("sso url: " + environment.getRequiredProperty(sso+".url"));
			response.setHeader("Location", environment.getRequiredProperty(sso+".url"));
			response.setStatus(302);
			return "";
		} else {
			if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
				return "enter_username_password";
			}
			User user = userService.findByUsername(username);
			if(user == null ) { 
				return "user_not_found";
			}
			if(passwordUtil.getSHA1Hash(password).equals(user.getPassword())) {
				return "successful_login";
			} else {
				return "password_not_matched";
			}
		}
	}
	
	
}
