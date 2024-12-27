package connection;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Auth {

	public static void name(String username,String password,Properties properties,String fromString,String to,String subjectString,String textString) {
		Session session = Session.getInstance(properties,new Authenticator() 
		{
			
			protected PasswordAuthentication getPasswordAuthentication() 
			{
				return new PasswordAuthentication(username,password);
			}
		});
		System.out.println("a class");
		try 
		{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromString));
			message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
			message.setSubject(subjectString);
			message.setText(textString);
			System.out.println("con3");
			Transport.send(message);
	
		}catch (Exception e) {
		e.printStackTrace();
		}
	}
}
