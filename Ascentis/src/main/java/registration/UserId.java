package registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import connection.DatabaseConnection;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/check_user_id")
public class UserId extends HttpServlet{

	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
	    
	    HttpSession session = req.getSession(false); 

		String user_idString = req.getParameter("username");
		
		resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    
	    PrintWriter out = resp.getWriter();
	    
	    Connection connection = DatabaseConnection.getConnection();
	    
	    try {
			PreparedStatement pStatement = connection.prepareStatement("select count(*) from account_info where username = ?");
			pStatement.setString(1, user_idString);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				if (rSet.getInt(1) != 0) {
					out.print("{\"exists\":true,\"message\":\"Username  already exists\"}");
					System.out.println("Inside");
					return;
				}
				
			
			else {
				out.print("{\"exists\":false,\"message\":\"Continue\"}");
				System.out.println(req.getParameter("username"));
				System.out.println(req.getParameter("password"));
				session.setAttribute("full_name", req.getParameter("full_name"));
				session.setAttribute("dob", req.getParameter("dob"));
				session.setAttribute("res_address", req.getParameter("res_address"));
				session.setAttribute("gender", req.getParameter("gender"));
				session.setAttribute("phone", req.getParameter("phone"));
				session.setAttribute("city", req.getParameter("city"));
				session.setAttribute("postal_code", req.getParameter("postal_code"));
				session.setAttribute("country", req.getParameter("country"));
				session.setAttribute("occupation", req.getParameter("occupation"));
				session.setAttribute("account_type", req.getParameter("account_type"));
				session.setAttribute("state", req.getParameter("state"));
				session.setAttribute("nominee_name", req.getParameter("nominee_name"));
				session.setAttribute("nominee_age", req.getParameter("nominee_age"));
				session.setAttribute("nominee_contact", req.getParameter("nominee_contact"));
				session.setAttribute("nominee_relation", req.getParameter("nominee_relation"));
				session.setAttribute("username", req.getParameter("username"));
				session.setAttribute("password", req.getParameter("password"));
			}
			}
			pStatement.close();
			rSet.close();
			connection.close();
		} 
	    catch (Exception e)
	    {
			e.printStackTrace();
		}
	    System.out.println("user id check successfully");
	    
	}
}
