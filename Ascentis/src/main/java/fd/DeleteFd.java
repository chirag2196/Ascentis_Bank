package fd;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/drop_fd")
public class DeleteFd extends HttpServlet{
	
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
	    
	    int fd_Id = Integer.parseInt(req.getParameter("fd_Id"));
	    int account_number = (int) session.getAttribute("account_number");
	    String nameString = (String) session.getAttribute("name");
	    
	    if (isDeleted(fd_Id,account_number,nameString)) {
	    	outPrintWriter.print("{\"data\":1}");
		}
	    else {
	    	outPrintWriter.print("{\"data\":0}");
	    }
	    
	}

	private boolean isDeleted(int fd_Id,int account_number,String nameString) {
		Connection connection = DatabaseConnection.getConnection();
	    try {
	    	PreparedStatement pStatement2 = connection.prepareStatement("update account_info set balance = balance + ? where account_number = ?");
	    	PreparedStatement pStatement3 = connection.prepareStatement("select amount from fd where fd_id = ?");
	    	pStatement3.setInt(1, fd_Id);
	    	ResultSet executeQuery = pStatement3.executeQuery();
	    	executeQuery.next();
	    	double amount = executeQuery.getDouble(1);
	    	
	    	pStatement2.setDouble(1,amount);
	    	pStatement2.setInt(2, account_number);
	    	int update = pStatement2.executeUpdate();
	    	
			PreparedStatement pStatement = connection.prepareStatement("delete from fd where fd_id = ?");
			pStatement.setInt(1, fd_Id);
			int x = pStatement.executeUpdate();
			
			pStatement.close();
			pStatement2.close();
			pStatement3.close();
			PreparedStatement pStatement4 = connection.prepareStatement("insert into transaction_info values (?,?,?,?,?,?,?)");
			pStatement4.setString(1, LocalDateTime.now().toString());
			pStatement4.setInt(2, 111111);
			pStatement4.setString(3, "FD");
			pStatement4.setInt(4, account_number);
			pStatement4.setString(5, nameString);
			pStatement4.setDouble(6, amount);
			pStatement4.setString(7, "Completed");
			int executeUpdate = pStatement4.executeUpdate();
			pStatement4.close();
			connection.close();
			executeQuery.close();
			if (x >= 1 && update >=1 && executeUpdate >=1) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return false;
	}
}
