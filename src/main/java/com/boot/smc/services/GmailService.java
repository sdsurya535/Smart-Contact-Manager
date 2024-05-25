package com.boot.smc.services;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;



@Service
public class GmailService {
	
	public boolean sendEmail(String to,String form,String subject,String text){
	    boolean flag = false;


	    Properties properties = new Properties();
	    properties.put("mail.smtp.auth",true);
	    properties.put("mail.smtp.starttls.enable",true);
	    properties.put("mail.smtp.port","587");
	    properties.put("mail.smtp.host","smtp.gmail.com");

	    String username = "codingsurya335@gmail.com";
	    String password = "jgbbjmkwwlyiqrhf";


	    Session session= Session.getInstance(properties, new Authenticator() {


	        @Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username,password);
	        }
	    });

	    try{
	        Message message = new MimeMessage(session);
	        message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
	        message.setFrom(new InternetAddress(form));
	        message.setSubject(subject);
	        message.setText(text);
	        message.setContent(text,"text/html");

	        Transport.send(message);
	        flag = true;

	    }catch (Exception e){
	        e.printStackTrace();
	    }


	    return flag;

	}

}
