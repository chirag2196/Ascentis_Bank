package autopay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import connection.DatabaseConnection;
import autopay.SenderNotification;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailNotification extends Thread{

	ArrayList<SenderNotification> list;
	int account_number;
	String name;
	
	public EmailNotification(ArrayList<SenderNotification> list, int account_number, String name) {
		super();
		this.list = list;
		this.account_number = account_number;
		this.name = name;
	}
	
	@Override
	public void run() {
		
		for (SenderNotification n : list) {
			delete_autopay(n.autopay_id);
			email_send(n.sender_account_number,account_number,name);
		}
	}
	
	private static String sender_email(int account_number)
	{
		try {
			Connection connection = DatabaseConnection.getConnection();
			PreparedStatement pStatement = connection.prepareStatement("select email from personal_info where account_number = ?");
			pStatement.setInt(1, account_number);
			ResultSet set = pStatement.executeQuery();
			set.next();
			return set.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void email_send(int sender_account_number, int account_number, String name) {
		
		String sender_to = sender_email(sender_account_number);
		if (sender_to == null) {
			System.out.println("Email is null");
			return;
		}
		String fromString = "ascentisbank@gmail.com";
		String sender_subjectString = "Important Update: Autopay Deletion Notification";
		String sender_textString = "We are writing to inform you that your autopay arrangement with "+name+" has been successfully deleted. This action was taken because the account number associated with this autopay "+account_number+"  has been closed.\r\n\n"
				+ "Thank you for your understanding. If you have any questions or need further assistance, please do not hesitate to contact us.\r\n\n"
				+ "Best regards,\r\n"
				+ "Ascentis Bank\r\n";
		
		String username = "ascentisbank";
		String password = "csmuiubvfybjxngx";
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth",true);
		properties.put("mail.smtp.starttls.enable",true);
		properties.put("mail.smtp.port","587");
		properties.put("mail.smtp.host","smtp.gmail.com");
		System.out.println("con1");
		
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
			message.setFrom(new InternetAddress(fromString));
			message.setRecipient(Message.RecipientType.TO,new InternetAddress(sender_to));
			message.setSubject(sender_subjectString);
			message.setText(sender_textString);
			System.out.println("con3");
			Transport.send(message);
			
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			
		}
		
	}

	private void delete_autopay(int autopay_id) {
		try {
			Connection connection = DatabaseConnection.getConnection();
			PreparedStatement pStatement = connection.prepareStatement("delete from autopay_info where autopay_id = ?");
			pStatement.setInt(1, autopay_id);
			int i = pStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
