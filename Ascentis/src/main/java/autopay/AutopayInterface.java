package autopay;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/autopay")
public class AutopayInterface extends HttpServlet
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
	    
	    String party_nameString = req.getParameter("recipientName");
	    int party_account_number = Integer.parseInt(req.getParameter("recipientAccountNumber"));
	    int x = check(party_nameString, party_account_number);
	    if (x == 0) {
			outPrintWriter.print("{\"data\":0}");
			return;
		}
	    else if (x == 1) {
	    	outPrintWriter.print("{\"data\":1}");
	    	return;
		}
	    session.setAttribute("receiver_name", party_nameString);
	    session.setAttribute("receiver_account_number", party_account_number);
	    double party_amount = Double.parseDouble(req.getParameter("amount"));
	    session.setAttribute("amount", party_amount);
	    String pay_end_dateString = req.getParameter("endDate");
	    session.setAttribute("endDate", pay_end_dateString);
	    String pay_start_dateString = req.getParameter("autoPayDate");
	    session.setAttribute("startDate", pay_start_dateString);
	    String frequencyString = req.getParameter("paymentFrequency");
	    session.setAttribute("frequency", frequencyString);
	    LocalDateTime datetime = LocalDateTime.parse(req.getParameter("autoPayDate"));
	    LocalDate date = datetime.toLocalDate();
	    if (frequencyString.equals("weekly")) {
			date = date.plusWeeks(1);
		}
	    else if (frequencyString.equals("monthly")) {
	    	date = date.plusMonths(1);
		}
	    else  {
	    	date = date.plusMonths(3);
		}  
	    session.setAttribute("next_pay_date", date.toString());
	    
	    outPrintWriter.print("{\"data\":2}");
	}
	
	private static int check(String name,int account_number)
	{
		Connection connection = DatabaseConnection.getConnection();
		int isExists = 0;
		try(PreparedStatement pStatement = connection.prepareStatement("select full_name from personal_info where account_number = ?")){
			pStatement.setInt(1, account_number);
			ResultSet resultSet = pStatement.executeQuery();
			if (resultSet.next()) {
				if (!resultSet.getString(1).equals(name)) {
					isExists = 1;
				}
				else {
					isExists = 2;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			return isExists;	
	}
}
