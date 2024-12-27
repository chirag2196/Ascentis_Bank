package fd;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/fd_pwd")
public class FDPassword extends HttpServlet
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
	    
	    String passString = req.getParameter("fdpassword");
	    int account_number = (int) session.getAttribute("account_number");
	    if (pwdCheck(account_number, passString) == 0) {
	    	outPrintWriter.print("{\"data\":0}");
	    	return;
		}
	    if (fdCreated(session)) {
	    	
	    	outPrintWriter.print("{\"data\":1}");
		    resp.setStatus(HttpServletResponse.SC_CREATED);
		}
	    
	    
	}
	
	private int pwdCheck(int account_number,String password)
	{
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement pStatement = connection.prepareStatement("select password from account_info where account_number = ?");
			pStatement.setInt(1, account_number);
			ResultSet set = pStatement.executeQuery();
			set.next();
			if (set.getString(1).equals(password)) {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private boolean fdCreated(HttpSession session)
	{
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement pStatement3 = connection.prepareStatement("update account_info set balance = balance - ? where account_number = ?");
			pStatement3.setDouble(1, (double)session.getAttribute("amount"));
			pStatement3.setInt(2, (int)session.getAttribute("account_number"));
			int executeUpdate3 = pStatement3.executeUpdate();
			PreparedStatement pStatement2 = connection.prepareStatement("insert into transaction_info values (?,?,?,?,?,?,?)");
			pStatement2.setString(1, (String)session.getAttribute("startDate"));
			pStatement2.setInt(2, (int)session.getAttribute("account_number"));
			pStatement2.setString(3, (String)session.getAttribute("name"));
			pStatement2.setInt(4, 111111);
			pStatement2.setString(5, "FD");
			pStatement2.setDouble(6, (double)session.getAttribute("amount"));
			pStatement2.setString(7, "Completed");
			int executeUpdate2 = pStatement2.executeUpdate();
			PreparedStatement pStatement = connection.prepareStatement("insert into fd (startDate,endDate,account_number,full_name,interest,duration,amount,maturity_amount) values(?,?,?,?,?,?,?,?)");
			pStatement.setString(1, (String)session.getAttribute("startDate"));
			pStatement.setString(2, (String)session.getAttribute("endDate"));
			pStatement.setInt(3, (int)session.getAttribute("account_number"));
			pStatement.setString(4, (String)session.getAttribute("name"));
			pStatement.setDouble(5, (double)session.getAttribute("interest"));
			pStatement.setInt(6, (int)session.getAttribute("duration"));
			pStatement.setDouble(7, (double)session.getAttribute("amount"));
			pStatement.setDouble(8, (double)session.getAttribute("maturity_amount"));
			int executeUpdate = pStatement.executeUpdate();
			if (executeUpdate >= 1 && executeUpdate2 >= 1 && executeUpdate3 >= 1) {
				GeneratePDF threadGeneratePDF = new GeneratePDF((String)session.getAttribute("name"), (String)session.getAttribute("startDate"), (String)session.getAttribute("email"), (String)session.getAttribute("endDate"), (int)session.getAttribute("duration"), (int)session.getAttribute("account_number"),(double)session.getAttribute("interest"), (double)session.getAttribute("amount"),(double)session.getAttribute("maturity_amount"));
				threadGeneratePDF.start();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			session.removeAttribute("amount");
		    session.removeAttribute("interest");
		    session.removeAttribute("duration");
		    session.removeAttribute("endDate");
		    session.removeAttribute("startDate");
		    session.removeAttribute("maturity_amount");
		}
		return false;
	}
}
