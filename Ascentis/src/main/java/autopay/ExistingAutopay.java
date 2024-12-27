package autopay;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import connection.DatabaseConnection;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/autopayList")
public class ExistingAutopay extends HttpServlet
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
	    ArrayList<AutopayList> tList = new ArrayList<AutopayList>();
	    Connection connection = DatabaseConnection.getConnection();
	    try 
	    {
	    PreparedStatement pStatement = connection.prepareStatement("select autopay_id,start_date,end_date,receiver_name,frequency,amount from autopay_info where sender_account_number = ?");
	    pStatement.setInt(1, (int) session.getAttribute("account_number"));
	    ResultSet executeQuery = pStatement.executeQuery();
	    while (executeQuery.next()) 
	    {
	    	tList.add(new AutopayList(String.valueOf(executeQuery.getInt(1)),executeQuery.getString(2), executeQuery.getString(3),executeQuery.getString(4),executeQuery.getString(5), executeQuery.getDouble(6)));
				
		}
			
		}
	    catch (Exception e) {
			e.printStackTrace();
		}
	    ObjectMapper objectMapper = new ObjectMapper();
	    String jsonResponse = objectMapper.writeValueAsString(tList);
	    outPrintWriter.write("{\"existingAutopay\":"+jsonResponse+"}");
	    outPrintWriter.flush();
	}
}
