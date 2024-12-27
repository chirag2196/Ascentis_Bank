package fd;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SelectableChannel;
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

@WebServlet("/existing_fd")
public class ExistingFD extends HttpServlet{

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
	    ArrayList<FDlist> list = new ArrayList<FDlist>();
	
	    Connection connection = DatabaseConnection.getConnection();
	    try {
			PreparedStatement pStatement = connection.prepareStatement("select fd_id,full_name,interest,amount,endDate from fd where account_number = ?");
			pStatement.setInt(1,(int) session.getAttribute("account_number"));
			ResultSet executeQuery = pStatement.executeQuery();
			while (executeQuery.next()) {
				list.add(new FDlist(String.valueOf(executeQuery.getInt(1)),executeQuery.getString(2), executeQuery.getDouble(3), executeQuery.getDouble(4), executeQuery.getString(5)));
				
			}
			} catch (Exception e) {
			e.printStackTrace();
		}
	    ObjectMapper objectMapper = new ObjectMapper();
	    String jsonResponse = objectMapper.writeValueAsString(list);
	    outPrintWriter.write("{\"recentFD\":"+jsonResponse+"}");
	    outPrintWriter.flush();
	}
}
