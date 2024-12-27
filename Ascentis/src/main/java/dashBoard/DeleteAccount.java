package dashBoard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import connection.DatabaseConnection;
import autopay.EmailNotification;
import autopay.SenderNotification;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/delete_account")
public class DeleteAccount extends HttpServlet {
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
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
		if (session == null) {
			outPrintWriter.print("{\"data\":0}");
			return;
		}

		String passwordString = req.getParameter("password");
		int account_number = (int) session.getAttribute("account_number");

		if (!check_pwd(passwordString, account_number)) {
			outPrintWriter.print("{\"data\":0}");
			return;
		} else if (check_autopay(account_number)) {
			outPrintWriter.print("{\"data\":1}");
			return;
		} else if (check_fd(account_number)) {
			outPrintWriter.print("{\"data\":2}");
			return;
		}
		double balance = amt(account_number);
		if (balance == -1.0) {
			outPrintWriter.print("{\"data\":0}");
			return;
		}
		delete_autopay(account_number,session);
		outPrintWriter.print("{\"data\":3,\"amount\":"+balance+"}");
		resp.setStatus(HttpServletResponse.SC_CREATED);
		session.invalidate();
		System.out.println("Deleted");

	}

	private void delete_autopay(int account_number,HttpSession session) {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pStatement = connection
						.prepareStatement("select sender_account_number,sender_name,autopay_id from autopay_info where receiver_account_number = ?")) {
			pStatement.setInt(1, account_number);
			ResultSet executeQuery = pStatement.executeQuery();
			ArrayList<SenderNotification> list = new ArrayList<SenderNotification>();
			while (executeQuery.next()) {
				list.add(new SenderNotification(executeQuery.getString(2), executeQuery.getInt(1),executeQuery.getInt(3)));
				
			}
			if (!list.isEmpty()) {
				EmailNotification thread = new EmailNotification(list,account_number,(String)session.getAttribute("name"));
				thread.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double amt(int account_number) {
		double balance = 0;
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pStatement = connection
						.prepareStatement("select balance from account_info where account_number = ?")) {
			pStatement.setInt(1, account_number);
			try (ResultSet query = pStatement.executeQuery();) {
				query.next();
				balance = query.getDouble(1);
				PreparedStatement pStatement2 = connection.prepareStatement("delete from nominee_info where account_number = ?");
				pStatement2.setInt(1, account_number);
				int i = pStatement2.executeUpdate();
				PreparedStatement pStatement3 = connection.prepareStatement("delete from account_info where account_number = ?");
				pStatement3.setInt(1, account_number);
				int j = pStatement3.executeUpdate();
				PreparedStatement pStatement4 = connection.prepareStatement("delete from personal_info where account_number = ?");
				pStatement4.setInt(1, account_number);
				int k = pStatement4.executeUpdate();
				if (i == j && j == k) {
					System.out.println("Successfully deleted");
				}
				else {
					return -1.0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return balance;
	}

	private static boolean check_pwd(String pwd, int account_number) {

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pStatement = connection
						.prepareStatement("select password from account_info where account_number = ?");) {
			pStatement.setInt(1, account_number);
			try (ResultSet resultSet = pStatement.executeQuery();) {
				resultSet.next();
				if (resultSet.getString(1).equals(pwd)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean check_autopay(int account_number) 
	{
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pStatement = connection
						.prepareStatement("select count(*) from autopay_info where sender_account_number = ?")) {
			pStatement.setInt(1, account_number);
			try (ResultSet query = pStatement.executeQuery();) {
				query.next();
				if (query.getInt(1) > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean check_fd(int account_number) {
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pStatement = connection
						.prepareStatement("select count(*) from fd where account_number = ?")) {
			pStatement.setInt(1, account_number);
			try (ResultSet query = pStatement.executeQuery();) {
				query.next();
				if (query.getInt(1) > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
