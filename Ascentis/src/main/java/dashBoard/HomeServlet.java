package dashBoard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import connection.DatabaseConnection;
import transaction.TransactionList;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeServlet extends HttpServlet
{
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		response.setHeader("Pragma", "no-cache");
	    response.setStatus(HttpServletResponse.SC_OK);
	    System.out.println("CORS Headers: " + response.getHeader("Access-Control-Allow-Origin"));
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		resp.setHeader("Pragma", "no-cache");
	    System.out.println("CORS Headers: " + resp.getHeader("Access-Control-Allow-Origin"));
	    req.setCharacterEncoding("UTF-8");
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    
	    System.out.println("Send email");
	    HttpSession session = req.getSession(false);
		if (session == null) {
			System.out.println("session is null");
			return;
		}
		System.out.println(session.getId());
		Connection connection = DatabaseConnection.getConnection();
		PrintWriter outPrintWriter = resp.getWriter();
		
		int account_number = (int) session.getAttribute("account_number");
		String name = (String) session.getAttribute("name");
		double balance = balance_retrive(connection,account_number);;
		ArrayList<TransactionList> tList = transaction(connection,account_number);
		LocalDateTime dateTime = login(connection, account_number);
		ObjectMapper objectMapper = new ObjectMapper();
	    String jsonResponse = objectMapper.writeValueAsString(tList);
	    outPrintWriter.write("{\"recentTransactions\":"+jsonResponse+",\"accountOwnerName\":\""+ name +"\",\"accountBalance\":"+balance+",\"datetime\":\""+dateTime+"\"}");
	        outPrintWriter.flush();
	}

	private ArrayList<TransactionList> transaction(Connection connection, int account_number) {
		PreparedStatement pStatement;
		ArrayList<TransactionList> tList = new ArrayList<TransactionList>();
		try {
			pStatement = connection.prepareStatement("select * from transaction_info where sender_account_number = ? or receiver_account_number = ? order by date desc limit 5");
			pStatement.setInt(1, account_number);
			pStatement.setInt(2, account_number);
			ResultSet resultSet = pStatement.executeQuery();
			while(resultSet.next()) 
			{
				String name,type;
				if (resultSet.getInt(2) == account_number) {
					name = resultSet.getString(5);
					type ="Outgoing";
				}
				else {
					name = resultSet.getString(3);
					type ="Incoming";
				}
				tList.add(new TransactionList(resultSet.getString(1),
						name,type ,resultSet.getString(7) ,resultSet.getDouble(6) ));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tList;
	}

	private double balance_retrive(Connection connection, int account_number) {
		PreparedStatement pStatement;
		try {
			pStatement = connection.prepareStatement("select balance from account_info where account_number = ?");
			pStatement.setInt(1,account_number);
			ResultSet executeQuery = pStatement.executeQuery();
			if (executeQuery.next()) {
				return executeQuery.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return -1.0;
	}
	
	private static LocalDateTime login(Connection connection,int account_number) {
		PreparedStatement pStatement;
		try {
			pStatement = connection.prepareStatement("select last_login from login where account_number = ?");
			pStatement.setInt(1,account_number);
			ResultSet executeQuery = pStatement.executeQuery();
			if (executeQuery.next()) {
				Timestamp ts = executeQuery.getTimestamp(1);
				if (ts != null) {
					
					return ts.toLocalDateTime();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}
