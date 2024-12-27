package otp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Properties;

import connection.DatabaseConnection;
import connection.Auth;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/otp")
public class OtpSend extends HttpServlet
{
	private int count;
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    req.setCharacterEncoding("UTF-8");
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    PrintWriter outPrintWriter = resp.getWriter();
	    System.out.println("Send email");
	    HttpSession session2 = req.getSession(false);
	    if(session2.getAttribute("count") == null) {
	    	count = 0;
	    }
	    else {
	    	count = (int) session2.getAttribute("count");
	    }
	    if (count < 0 || count > 2) {
	    	
		    Connection connection = DatabaseConnection.getConnection();
		    try {
				PreparedStatement pStatement = connection.prepareStatement("insert into blocked_emails(email) values(?)");
						pStatement.setString(1, (String) session2.getAttribute("email"));
				pStatement.executeUpdate();
				pStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		    outPrintWriter.print("{\"exists\":false,\"message\":\"Max limit reached and your email is currently blocked\"}");
		    outPrintWriter.flush();
		    outPrintWriter.close();
		    
		    session2.invalidate();
		    return;
		}
	    
	    String to = (String) session2.getAttribute("email");
		String fromString = "ascentisbank@gmail.com";
		String otpString = Otp.getOTP();
		String subjectString = null;
		String textString = null;
		String formTypeString = (String) session2.getAttribute("formType");
		if (formTypeString.equals("forgot_email")) {
			subjectString = "Your One-Time Password (OTP) for Password Updation";
			textString = "Dear User,\r\n"
					+ "We received a request to update the password for your account associated with the email address "+to+". To proceed with this request, please use the One-Time Password (OTP) provided below:\r\n"
					+ "Your OTP: " +otpString +"\r\n"
					+ "This OTP is valid for the next 2 minutes. Please enter it in the designated field on our website to complete your password update.\r\n"
					+ "If you did not request a password change, please ignore this email. Your account remains secure.\r\n"
					+ "If you have any questions or need further assistance, feel free to reach out to our support team.\r\n"
					+ "Thank you!\r\n"
					+ "Best regards";
		}
		else if (formTypeString.equals("register_email")){
			subjectString = "Your One-Time Password (OTP) for Registration";
			textString ="Dear Future User,\n\nThank you for choosing Ascentis Bank!\n\nTo complete your registration process, please use the One-Time Password (OTP) provided below:\n\n**Your OTP:** "+ otpString+"\n\nThis OTP is valid for 2 minute. Please do not share this code with anyone.\n\nIf you did not request this OTP, please ignore this email. For any assistance, feel free to contact our support team.\n\nThank you for your attention!\n\nBest regards,\nAscentis Bank";
		}
		else {
			count= 0;
			subjectString = "Your One-Time Password (OTP) for Payment";
			textString = "Dear User,\r\n"
					+ "We received a request to make a payment from your account associated with the email address "+to+". To proceed with this request, please use the One-Time Password (OTP) provided below:\r\n"
					+ "Your OTP: " +otpString +"\r\n"
					+ "This OTP is valid for the next 2 minutes. Please enter it in the designated field on our website to complete a transaction.\r\n"
					+ "If you did not request for it, please contact the customer care immediately\r\n"
					+ "If you have any questions or need further assistance, feel free to reach out to our support team.\r\n"
					+ "Thank you!\r\n"
					+ "Best regards";
		}
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth",true);
		properties.put("mail.smtp.starttls.enable",true);
		properties.put("mail.smtp.port","587");
		properties.put("mail.smtp.host","smtp.gmail.com");
		System.out.println("con1");
		String username = "ascentisbank";
		String password = "csmuiubvfybjxngx";
		
		
		
		
		System.out.println("con2");
		try 
		{
			Auth.name(username, password, properties, fromString, to, subjectString, textString);
			count++;
			LocalDateTime dateTime = LocalDateTime.now();
			System.out.println(count);
			session2.setAttribute("otp", otpString);
			System.out.println(otpString);
			session2.setAttribute("count", count);
			session2.setAttribute("datetime", dateTime);
		    resp.setStatus(HttpServletResponse.SC_OK);
			outPrintWriter.print("{\"exists\":true,\"message\":\"Email sent successfully\"}");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			outPrintWriter.print("{\"exists\":false,\"message\":\"Email not sent successfully\"}");
		}
		finally {
            outPrintWriter.flush();  
            outPrintWriter.close();  
        }
		
	    System.out.println("con4");
	   
	}
}
