package autopay;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.DatabaseConnection;
import transaction.Pay;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/auto_pwd")
public class AutopayPassword extends HttpServlet
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
	    
	    String passString = req.getParameter("password");
	    int account_number = (int) session.getAttribute("account_number");
	    String sender_emailString = (String) session.getAttribute("email"); 
	    
	    Connection connection = DatabaseConnection.getConnection();
	    try {
	    	PreparedStatement pStatement = connection.prepareStatement("select password from account_info where account_number = ?");
		    pStatement.setInt(1, account_number);
		    ResultSet query = pStatement.executeQuery();
		    if (query.next()) {
				if (!query.getString(1).equals(passString)) {
					outPrintWriter.print("{\"data\":0}");
				}
				else {
					PreparedStatement pStatement2 = connection.prepareStatement("insert into autopay_info(start_date,end_date,next_pay_date,sender_account_number,sender_name,receiver_account_number,receiver_name,amount,frequency ) values(?,?,?,?,?,?,?,?,?)");
					pStatement2.setString(1,(String) session.getAttribute("startDate"));
					pStatement2.setString(2,(String) session.getAttribute("endDate"));
					pStatement2.setString(3,(String) session.getAttribute("next_pay_date"));
					pStatement2.setInt(4,(int) session.getAttribute("account_number"));
					pStatement2.setString(5,(String) session.getAttribute("name"));
					pStatement2.setInt(6,(int) session.getAttribute("receiver_account_number"));
					pStatement2.setString(7,(String) session.getAttribute("receiver_name"));
					pStatement2.setDouble(8,(double) session.getAttribute("amount"));
					pStatement2.setString(9,(String) session.getAttribute("frequency"));
					int x = pStatement2.executeUpdate();
					if (x > 0) {
						int y = Pay.transactions((String) session.getAttribute("startDate"),(int) session.getAttribute("account_number"),(String) session.getAttribute("name"),sender_emailString,(int) session.getAttribute("receiver_account_number"),(String) session.getAttribute("receiver_name"),receiver_email((int) session.getAttribute("receiver_account_number")),(double) session.getAttribute("amount"));
						if (y == 2) {
							resp.setStatus(HttpServletResponse.SC_CREATED);
							outPrintWriter.print("{\"data\":2}");
						}
						else if (y == 1) {
							outPrintWriter.print("{\"data\":1}");
						}
						session.removeAttribute("receiver_name");
					    session.removeAttribute("receiver_account_number");
					    session.removeAttribute("amount");
					    session.removeAttribute("endDate");
					    session.removeAttribute("startDate");
					    session.removeAttribute("frequency");
					    session.removeAttribute("next_pay_date");
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	}

	private String receiver_email(int attribute) {
		
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement pStatement = connection.prepareStatement("select email from personal_info where account_number = ?");
			pStatement.setInt(1, attribute);
			ResultSet executeQuery = pStatement.executeQuery();
			if (executeQuery.next()) {
				return executeQuery.getString(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
