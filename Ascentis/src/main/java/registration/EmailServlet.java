package registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/check_email")
public class EmailServlet extends HttpServlet{
	
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
	    response.setStatus(HttpServletResponse.SC_OK);
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		resp.setHeader("Pragma", "no-cache");
		req.setCharacterEncoding("UTF-8");

		String emailString = req.getParameter("email");
		String mobileString = req.getParameter("phone");
		String formTypeString = req.getParameter("form");
		System.out.println(emailString);
		System.out.println(mobileString);
		resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    
	    PrintWriter out = resp.getWriter();
	    HttpSession session = req.getSession(true);
	    
	    Connection connection = DatabaseConnection.getConnection();
	    boolean email = false,mobile = false;
	    
	    try {
	    	PreparedStatement pStatement3 = connection.prepareStatement("select count(*) from blocked_emails where email = ?");
	    	pStatement3.setString(1, emailString);
	    	ResultSet executeQuery = pStatement3.executeQuery();
	    	executeQuery.next();
	    	if (executeQuery.getInt(1) == 0) {
	    		PreparedStatement pStatement = connection.prepareStatement("select count(*) from personal_info where email = ?");
				PreparedStatement pStatement2 = connection.prepareStatement("select count(*) from personal_info where phone = ?");
				pStatement.setString(1, emailString);
				pStatement2.setString(1, mobileString);
				ResultSet rSet = pStatement.executeQuery();
				ResultSet rSet2 = pStatement2.executeQuery();
				
				if (rSet.next()) {
					email = rSet.getInt(1) != 0 ;
				}
				if (rSet2.next()) {
					mobile = rSet2.getInt(1) != 0 ;
				}
				pStatement.close();
				pStatement2.close();
				rSet.close();
				rSet2.close();
				connection.close();
				
			if (email && mobile) 
			{
		    	out.print("{\"exists\":true,\"message\":\"Both email and mobile no. already exists\"}");
				System.out.println("Both email and mobile no. already exists");
			}
			else if (mobile) 
			{
				out.print("{\"exists\":true,\"phoneMessage\":\"Mobile no. already exists\"}");
				System.out.println("Mobile no");
			}
			else if (email) 
			{
				out.print("{\"exists\":true,\"emailMessage\":\"Email already exists\"}");
				System.out.println("Email");
			}
			else 
			{
				out.print("{\"exists\":false,\"message\":\"Continue Register\"}");
			}
	    	}
		    else {
		    	out.print("{\"exists\":true,\"emailMessage\":\"Your email is currently blocked\"}");
		    }
	    }catch (Exception e) {
			e.printStackTrace();
		}
			
			
			session.setAttribute("email", emailString);
			session.setAttribute("formType",formTypeString);
			System.out.println("session");
			System.out.println(emailString);
			System.out.println("Session ID: " + session.getId());
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure;Path=/; HttpOnly");
			
			session.setMaxInactiveInterval(30 * 60);
		
	    System.out.println("Email check successfully");
	}
	
}
