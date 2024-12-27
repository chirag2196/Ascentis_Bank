package authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/validate")
public class ChangePassword extends HttpServlet
{
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    req.setCharacterEncoding("UTF-8");
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    
	    String userOtpString = req.getParameter("otp");
		HttpSession session = req.getSession(false);
		String otpString = (String) session.getAttribute("otp");
		PrintWriter outPrintWriter = resp.getWriter();
		LocalDateTime prevDateTime = (LocalDateTime) session.getAttribute("datetime");
		LocalDateTime currDateTime = LocalDateTime.now();
		Duration duration = Duration.between(prevDateTime, currDateTime);
		System.out.println(prevDateTime);
		System.out.println(currDateTime);
		
		
		if (userOtpString.equals(otpString) && duration.toMinutes() < 2)  {
			
				outPrintWriter.print("{\"check\":true,\"message\":\"Otp is correct\"}");
				
			} 
		else 
		{
			outPrintWriter.print("{\"check\":false,\"message\":\"Otp is incorrect\"}");
		}
	}
}
