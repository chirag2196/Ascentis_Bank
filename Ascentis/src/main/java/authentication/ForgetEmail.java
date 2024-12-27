package authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/forgot")
public class ForgetEmail extends HttpServlet
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
		System.out.println("Starting");
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    req.setCharacterEncoding("UTF-8");
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    
	    String emailString = req.getParameter("email");
	    String formTypeString = req.getParameter("form");
	    
	    Connection connection = DatabaseConnection.getConnection();
	    PrintWriter outPrintWriter = resp.getWriter();
	    HttpSession session = req.getSession(true);
	    try {
	    	PreparedStatement pStatement3 = connection.prepareStatement("select count(*) from blocked_emails where email = ?");
	    	pStatement3.setString(1, emailString);
	    	ResultSet executeQuery = pStatement3.executeQuery();
	    	executeQuery.next();
	    	if (executeQuery.getInt(1) == 0) {
	    	PreparedStatement pStatement = connection.prepareStatement("select count(*) from personal_info where email = ?");
	    	pStatement.setString(1, emailString);
			ResultSet resultSet = pStatement.executeQuery();
			resultSet.next();
			if (resultSet.getInt(1) == 1) {
				outPrintWriter.print("{\"exists\":true}");
				session.setAttribute("email", emailString);
				session.setAttribute("formType",formTypeString);
				System.out.println(formTypeString);
				resp.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure;Path=/; HttpOnly");
				session.setMaxInactiveInterval(30 * 60);
			}
			else {
				outPrintWriter.print("{\"exists\":false,\"message\":\"Email does not exist\"}");
			}
	    	}
	    	else {
	    		outPrintWriter.print("{\"exists\":true,\"message\":\"Your email is currently blocked\"}");
	    	}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	   
	    System.out.println("Ending");
	}
}
