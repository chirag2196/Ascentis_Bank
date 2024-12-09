package com.bank;


import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class TransactionEmailThread extends Thread{
	
	String sender_emailString;
	String receiver_emailString;
	double amount;
	
	public TransactionEmailThread(String sender_emailString, String receiver_emailString,double amount) {
		super();
		this.sender_emailString = sender_emailString;
		this.receiver_emailString = receiver_emailString;
		this.amount = amount;
	}

	@Override
	public void run() {
		 	String sender_to = sender_emailString;
			String fromString = "ascentisbank@gmail.com";
			String sender_subjectString = "Money debited from your Ascentis bank account";
			String sender_textString = "Your account associated with these email id "+ sender_to+" was debited with the following amount : "+ amount;
			String receiver_to = receiver_emailString;
			String receiver_subjectString = "Money credited in your Ascentis bank account";
			String receiver_textString = "Your account associated with these email id "+ receiver_to+" was credited with the following amount : "+ amount;
			
			Properties properties = new Properties();
			properties.put("mail.smtp.auth",true);
			properties.put("mail.smtp.starttls.enable",true);
			properties.put("mail.smtp.port","587");
			properties.put("mail.smtp.host","smtp.gmail.com");
			System.out.println("con1");
			String username = "ascentisbank";
			String password = "";
			
			Session session = Session.getInstance(properties,new Authenticator() 
			{
				
				protected PasswordAuthentication getPasswordAuthentication() 
				{
					return new PasswordAuthentication(username,password);
				}
			});
			System.out.println("con2");
			try 
			{
				Message message = new MimeMessage(session);
				Message message2 = new MimeMessage(session);
				message.setFrom(new InternetAddress(fromString));
				message.setRecipient(Message.RecipientType.TO,new InternetAddress(sender_to));
				message.setSubject(sender_subjectString);
				message.setText(sender_textString);
				message2.setFrom(new InternetAddress(fromString));
				message2.setRecipient(Message.RecipientType.TO,new InternetAddress(receiver_to));
				message2.setSubject(receiver_subjectString);
				message2.setText(receiver_textString);
				System.out.println("con3");
				Transport.send(message);
				Transport.send(message2);
				
			}
			catch(Exception e) 
			{
				e.printStackTrace();
				
			}
			
		    System.out.println("con4");
	}
}
