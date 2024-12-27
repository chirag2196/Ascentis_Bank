package transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import connection.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/transactionList")
public class AllTransactions extends HttpServlet
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
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    req.setCharacterEncoding("UTF-8");
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    PrintWriter outPrintWriter = resp.getWriter();
	    HttpSession session = req.getSession(false);
	    Connection connection = DatabaseConnection.getConnection();
	    int account_number = (int) session.getAttribute("account_number");
		PreparedStatement pStatement;
		ArrayList<TransactionList> tList = new ArrayList<TransactionList>();
		try {
			pStatement = connection.prepareStatement("select * from transaction_info where sender_account_number = ? or receiver_account_number = ? order by date desc");
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
		
		ObjectMapper objectMapper = new ObjectMapper();
	    String jsonResponse = objectMapper.writeValueAsString(tList);
	    outPrintWriter.write("{\"recentTransactions\":"+jsonResponse+"}");
	    outPrintWriter.flush();
	}
}
