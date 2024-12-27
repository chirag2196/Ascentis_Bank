package fd;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/fd")
public class FDInfo extends HttpServlet
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
	    
	    double amount = Double.parseDouble(req.getParameter("depositAmount"));
	    double interest = Double.parseDouble(req.getParameter("interestRate"));
	    int duration = Integer.parseInt(req.getParameter("depositDuration"));
	    double maturity_amount = maturity(duration, amount, interest);
	    
	    
	    
	    LocalDateTime startDate = LocalDateTime.parse(req.getParameter("fixedDepositDate"));
	    LocalDate localDate = startDate.toLocalDate();
	    LocalDate endDate = null;
	    
	    if (duration == 6) {
			endDate = localDate.plusMonths(6);
		}
	    else if (duration == 1) {
	    	endDate = localDate.plusYears(1);
		}
	    else if (duration == 3) {
	    	endDate = localDate.plusYears(3);
		}
	    else if (duration == 5) {
	    	endDate = localDate.plusYears(5);
		}
	    
	    
	    if (balanceCheck((int)session.getAttribute("account_number"), amount) == 1) {
	    	session.setAttribute("amount", amount);
		    session.setAttribute("interest", interest);
		    session.setAttribute("duration", duration);
		    session.setAttribute("endDate", endDate.toString());
		    session.setAttribute("startDate", startDate.toString());
		    session.setAttribute("maturity_amount", maturity_amount);
		    outPrintWriter.print("{\"data\":1}");
		}
	    else {
	    	outPrintWriter.print("{\"data\":0}");
		}
	    
	    
	    
	    
	}
	
	private int balanceCheck(int account_number,double amount)
	{
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement pStatement = connection.prepareStatement("select balance from account_info where account_number = ?");
			pStatement.setInt(1, account_number);
			ResultSet resultSet = pStatement.executeQuery();
			resultSet.next();
			if (resultSet.getDouble(1) >= amount) {
				return 1;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private strictfp double maturity(int duration,double amount,double interest)
	{
		double maturity_amount;
		if (duration == 6) {
	    	maturity_amount = amount + (amount * interest * 0.5 / 100);
		}
	    else {
	    	maturity_amount = amount * Math.pow((1 + interest/100), duration);
	    }
		DecimalFormat df = new DecimalFormat("0.00");
	    String formattedValue = df.format(maturity_amount);
		return Double.parseDouble(formattedValue);
	}
}
