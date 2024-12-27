package transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;

import connection.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/transaction")
public class ValidateTransaction extends HttpServlet
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
	    
	    String userOtpString = req.getParameter("otp");
		String otpString = (String) session.getAttribute("otp");
		int account_number = (int) session.getAttribute("account_number");
		int from_account_number = (int) session.getAttribute("from_account_number");
		double amount = (double) session.getAttribute("amount");
		String nameString = (String) session.getAttribute("name");
		String from_nameString = (String) session.getAttribute("from_name");
		String dateString = (String) session.getAttribute("date");
		System.out.println(dateString);
		LocalDateTime prevDateTime = (LocalDateTime) session.getAttribute("datetime");
		LocalDateTime currDateTime = LocalDateTime.now();
		Duration duration = Duration.between(prevDateTime, currDateTime);
		
		
		if (userOtpString.equals(otpString) && duration.toMinutes() < 2) {
			
			int x = Pay.transactions(dateString, account_number, nameString,(String)session.getAttribute("email") ,from_account_number, from_nameString,(String)session.getAttribute("from_email"), amount);
			if (x == 1) {
				outPrintWriter.print("{\"data\":1,\"message\":\"Insufficient balance\"}");
			}
			else if (x == 2) {
				outPrintWriter.print("{\"data\":2,\"message\":\"Transaction Successful\"}");
				resp.setStatus(HttpServletResponse.SC_CREATED);
			}
		}
		else {
			outPrintWriter.print("{\"data\":0,\"message\":\"Otp is invalid\"}");
			Connection connection = DatabaseConnection.getConnection();
			try {
			PreparedStatement pStatement5 = connection.prepareStatement("insert into transaction_info (date,sender_account_number,sender_name,receiver_account_number,receiver_name,amount,status) values (?,?,?,?,?,?,?)");
			pStatement5.setString(1, dateString);
			pStatement5.setInt(2,account_number);
			pStatement5.setString(3, nameString);
			pStatement5.setInt(4,from_account_number);
			pStatement5.setString(5,from_nameString);
			pStatement5.setDouble(6, amount);
			pStatement5.setString(7, "Failed(Otp)");
			pStatement5.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}
		}
		
		session.removeAttribute("from_account_number");
		session.removeAttribute("amount");
		session.removeAttribute("formType");
		session.removeAttribute("date");
		session.removeAttribute("otpString");
		session.removeAttribute("from_name");
		session.removeAttribute("from_email");
	}
}
