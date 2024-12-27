package authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/change")
public class Validate extends HttpServlet
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
	    
	   
		HttpSession session = req.getSession(false);
//		PrintWriter outPrintWriter = resp.getWriter();
		
		String passwordString = req.getParameter("newPassword");
		Connection connection = DatabaseConnection.getConnection();
		PreparedStatement pStatement;
		try {
			pStatement = connection.prepareStatement("update account_info set password = ? where account_number = (select account_number from personal_info where email = ?)");
			pStatement.setString(1, passwordString);
			pStatement.setString(2, (String) session.getAttribute("email"));
			int result = pStatement.executeUpdate();
			if (result > 0) 
				{
				resp.setStatus(HttpServletResponse.SC_CREATED);
				session.invalidate();
//				outPrintWriter.print("{\"check\":true,\"message\":\"Otp is correct\"}");
				}
			} catch (SQLException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			
	}
		
}


