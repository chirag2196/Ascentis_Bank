package autopay;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/drop_autopay")
public class DeleteAutopay extends HttpServlet{

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
	    
	    int autopay_Id = Integer.parseInt(req.getParameter("autopay_Id"));
	    
	    if (isDeleted(autopay_Id)) {
	    	outPrintWriter.print("{\"data\":1}");
		}
	    else {
	    	outPrintWriter.print("{\"data\":0}");
	    }
	}

	private boolean isDeleted(int autopay_Id) {
		Connection connection = DatabaseConnection.getConnection();
	    try {
			PreparedStatement pStatement = connection.prepareStatement("delete from autopay_info where autopay_id = ?");
			pStatement.setInt(1, autopay_Id);
			int x = pStatement.executeUpdate();
			if (x >= 1) {
				return true;
			}
			pStatement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return false;
	}
}
