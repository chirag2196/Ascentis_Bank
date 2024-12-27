package registration;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import connection.DatabaseConnection;

import java.time.Duration;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/validate_otp")
public class ValidateOtp extends HttpServlet
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		
		resp.setHeader("Access-Control-Allow-Origin", "https://ascentis-bank.onrender.com");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With");
		resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    PrintWriter outPrintWriter = resp.getWriter();
	    req.setCharacterEncoding("UTF-8");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    
	    
		String userOtpString = req.getParameter("otp");
		HttpSession session = req.getSession();
		String otpString = (String) session.getAttribute("otp");
		System.out.println(otpString);
		LocalDateTime prevDateTime = (LocalDateTime) session.getAttribute("datetime");
		LocalDateTime currDateTime = LocalDateTime.now();
		Duration duration = Duration.between(prevDateTime, currDateTime);
		
		if (userOtpString.equals(otpString) && duration.toMinutes() < 2) 
		{
			
			outPrintWriter.print("{\"check\":true,\"message\":\"Otp is correct\"}");
			System.out.println("Otp is correct");
			String full_name = (String) session.getAttribute("full_name");
			String dob = (String) session.getAttribute("dob");
			String res_address = (String) session.getAttribute("res_address");
			String gender = (String) session.getAttribute("gender");
			String phone = (String) session.getAttribute("phone");
			String city = (String) session.getAttribute("city");
			String postal_code = (String) session.getAttribute("postal_code");
			String country = (String) session.getAttribute("country");
			String occupation = (String) session.getAttribute("occupation");
			String account_type = (String) session.getAttribute("account_type");
			String state = (String) session.getAttribute("state");
			String nominee_name = (String) session.getAttribute("nominee_name");
			String nominee_age = (String) session.getAttribute("nominee_age");
			String nominee_contact = (String) session.getAttribute("nominee_contact");
			String nominee_relation = (String) session.getAttribute("nominee_relation");
			String username = (String) session.getAttribute("username");
			String password = (String) session.getAttribute("password");
			String email = (String) session.getAttribute("email");
			Connection connection = DatabaseConnection.getConnection();
			int account_number = 0; 
			try 
			{
				PreparedStatement pStatement = connection.prepareStatement("insert into personal_info (full_name,dob,gender,phone,email,res_address,city,state,postal_code,country,occupation,account_type) values(?,?,?,?,?,?,?,?,?,?,?,?)");
				pStatement.setString(1,full_name);
				pStatement.setString(2,dob);
				pStatement.setString(3,gender);
				pStatement.setString(4,phone);
				pStatement.setString(5,email);
				pStatement.setString(6,res_address);
				pStatement.setString(7,city);
				pStatement.setString(8,state);
				pStatement.setString(9,postal_code);
				pStatement.setString(10,country);
				pStatement.setString(11,occupation);
				pStatement.setString(12,account_type);
				
				int x = pStatement.executeUpdate();
				if (x >= 1) 
				{
					System.out.println("Personal_info table is updated");
				}
					pStatement.close();
					PreparedStatement pStatement3 = connection.prepareStatement("select account_number from personal_info where email = ? and phone = ?");
					pStatement3.setString(1, email);
					pStatement3.setString(2, phone);
					ResultSet rSet = pStatement3.executeQuery();
					if (rSet.next()) 
					{
						account_number = rSet.getInt(1);
						System.out.println("Account number generated");
					}
					pStatement3.close();
					rSet.close();
					PreparedStatement pStatement2 = connection.prepareStatement("insert into account_info (username,account_number,password) values(?,?,?)");
					pStatement2.setString(1, username);
					pStatement2.setInt(2, account_number);
					pStatement2.setString(3, password);
					int y = pStatement2.executeUpdate();
					if (y >= 1) 
					{
						System.out.println("Account_info table is updated");
					}
						pStatement2.close();
						PreparedStatement pStatement4 = connection.prepareStatement("insert into nominee_info values(?,?,?,?,?)");
						pStatement4.setString(2, nominee_name);
						pStatement4.setString(3, nominee_age);
						pStatement4.setString(4, nominee_contact);
						pStatement4.setString(5, nominee_relation);
						pStatement4.setInt(1, account_number);
						int z = pStatement4.executeUpdate();
						if (z >= 1) 
						{
							System.out.println("Nominee table is updated");
						}
							pStatement4.close();
							PreparedStatement pStatement5 = connection.prepareStatement("insert into login (account_number) values (?) ");
							pStatement5.setInt(1, account_number);
							int update = pStatement5.executeUpdate();
							if (update >= 1) {
								System.out.println("Login table upadated");
							}
							resp.setStatus(HttpServletResponse.SC_CREATED);
						outPrintWriter.print("{\"check\":true,\"message\":"+account_number+"}");
						OpeningPdf threadPdf = new OpeningPdf(email, full_name, account_type, account_number, LocalDate.now().toString());
						threadPdf.start();
						session.invalidate();
				
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		else 
		{
			outPrintWriter.print("{\"check\":false,\"message\":\"OTP is invalid\"}");
			System.out.println("Otp is incorrect");
		}
	}
}
