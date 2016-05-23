package com.myapp.dbconfig.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
	private static final String SALT = "abcd";
	
	public String getSHA1Hash(String passwordToHash) 	{
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(SALT.getBytes());
			byte[] bytes = md.digest(passwordToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return generatedPassword;
	}
	
	public static void main(String[] args) {
		PasswordUtil passwordUtil = new PasswordUtil();
		System.out.println(passwordUtil.getSHA1Hash("user")); //921c80b1516254c52dfd7b8a11c0b93e8d3a342b
	}
}
