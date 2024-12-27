package dashBoard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

import connection.DatabaseConnection;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/account_info")
public class AccountInfo extends HttpServlet{

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
//	    ArrayList<AccountList> list = new ArrayList<AccountList>();
	    AccountList infoAccountList = null;
	    Connection connection = DatabaseConnection.getConnection();
	    try {
			PreparedStatement pStatement = connection.prepareStatement("select * from personal_info where account_number = ?");
			pStatement.setInt(1, (int)session.getAttribute("account_number"));
			ResultSet executeQuery = pStatement.executeQuery();
			executeQuery.next();
			LocalDate dobDate = LocalDate.parse(executeQuery.getString(3));
			LocalDate nowDate = LocalDate.now();
			Period age = Period.between(dobDate, nowDate); 
			
			String addressString = executeQuery.getString(7)+", "+executeQuery.getString(8)+", "+executeQuery.getString(9);
			
			infoAccountList = new AccountList(executeQuery.getString(2), (String) session.getAttribute("username"), String.valueOf(executeQuery.getInt(1)), String.valueOf(age.getYears()), executeQuery.getString(6),executeQuery.getString(5), addressString,executeQuery.getString(13), executeQuery.getString(12));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    ObjectMapper objectMapper = new ObjectMapper();
	    String jsonResponse = objectMapper.writeValueAsString(infoAccountList);
	    outPrintWriter.write("{\"accountInfo\":"+jsonResponse+"}");
	    outPrintWriter.flush();
	}
}
