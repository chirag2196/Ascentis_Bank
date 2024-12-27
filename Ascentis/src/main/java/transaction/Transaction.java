package transaction;

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

@WebServlet("/pay")
public class Transaction extends HttpServlet
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
	    PrintWriter outPrintWriter = resp.getWriter();
	    HttpSession session = req.getSession(false);
	    
	    int from_account_number = Integer.parseInt(req.getParameter("recipientAccountNumber"));
	    String from_name = req.getParameter("recipientName");
	    double amount = Double.parseDouble(req.getParameter("amount"));
	    String formTypeString = req.getParameter("form");
	    String dateString = req.getParameter("transactionDate");
	    String from_emailString = null;
	    
	    Connection connection = DatabaseConnection.getConnection();
		int isExists = 0;
		try {
			PreparedStatement pStatement = connection.prepareStatement("select full_name,email from personal_info where account_number = ?");
			pStatement.setInt(1, from_account_number);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				if (!from_name.equals(rSet.getString(1))) {
					isExists = 1;
				}
				else {
					isExists = 2;
					from_emailString = rSet.getString(2);
				}
			}
			rSet.close();
			pStatement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    if (isExists == 0) {
	    	outPrintWriter.print("{\"data\":0}");
		}
	    else if (isExists == 1) {
			outPrintWriter.print("{\"data\":1}");
		}
	    else {
			session.setAttribute("from_name", from_name);
			session.setAttribute("from_account_number", from_account_number);
			session.setAttribute("amount", amount);
			session.setAttribute("formType",formTypeString);
			session.setAttribute("date", dateString);
			session.setAttribute("from_email", from_emailString);
			outPrintWriter.print("{\"data\":2}");
			resp.setStatus(HttpServletResponse.SC_CREATED);
		}
	   
	    
	    
	}
	
	
}
