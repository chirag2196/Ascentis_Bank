package authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet
{
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		response.setHeader("Pragma", "no-cache");
	    System.out.println("CORS Headers: " + response.getHeader("Access-Control-Allow-Origin"));
	    response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
		req.setCharacterEncoding("UTF-8");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		resp.setHeader("Pragma", "no-cache");
		System.out.println("CORS Headers: " + resp.getHeader("Access-Control-Allow-Origin") + " post");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		String usernameString = req.getParameter("username");
		if (usernameString == null) {
			System.out.println("username " + usernameString);
		}
		else {
			System.out.println(usernameString);
		}
		String passwordString = req.getParameter("password");
		if (passwordString == null) {
			System.out.println("password " + passwordString);
		}
		else {
			System.out.println(passwordString);
		}
		PrintWriter ouPrintWriter = resp.getWriter();
		System.out.println("hello");
		Connection connection = DatabaseConnection.getConnection();
		int checks = check(connection,usernameString, passwordString);
		if (checks >= 800001) {
			HttpSession session = req.getSession(true);
			ouPrintWriter.print("{\"data\":2,\"session\":\""+session.getId()+"\"}");
			session.setAttribute("username", usernameString);
			session.setAttribute("account_number", checks);
			System.out.println(checks);
			ArrayList<String> l1 = name_retrive(connection,checks);
			session.setAttribute("name", l1.get(0));
			session.setAttribute("email",  l1.get(1));
			resp.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; SameSite=None; Secure;Path=/;");
			resp.setStatus(HttpServletResponse.SC_CREATED);
			session.setMaxInactiveInterval(30 * 60);
			System.out.println(session.getId());
			try {
			PreparedStatement pStatement = connection.prepareStatement("update login set last_login = cuurent_time,cuurent_time = NOW() where account_number = ?");
			pStatement.setInt(1, checks);
			int x = pStatement.executeUpdate();
			
			if (x >= 1) {
				System.out.println("Login time updated");
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
					}
		else if (checks == 1) {
			ouPrintWriter.print("{\"data\":1}");
			System.out.println("Password is wrong");
		}
		else{
			ouPrintWriter.print("{\"data\":0}");
			System.out.println("Username is wrong");
		}
		System.out.println("complete");
		
	}
	
	private static int check(Connection connection,String usernameString,String passwordString)
	{
		
		int isExists = 0;
		try {
			PreparedStatement pStatement = connection.prepareStatement("select password,account_number from account_info where username = ?");
			pStatement.setString(1, usernameString);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				if (!passwordString.equals(rSet.getString(1))) {
					isExists = 1;
				}
				else {
					isExists = rSet.getInt(2);
				}
			}
			rSet.close();
			pStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isExists;
	}
	
	private ArrayList<String> name_retrive(Connection connection,int account_number) {
		ArrayList<String> l1 = new ArrayList<String>();
		try {
		PreparedStatement pStatement = connection.prepareStatement("select full_name,email from personal_info where account_number = ?");
		pStatement.setInt(1, account_number);
		ResultSet rSet = pStatement.executeQuery();
		
		if (rSet.next()) {
			l1.add(rSet.getString(1)) ;
			l1.add(rSet.getString(2));
			return l1;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
