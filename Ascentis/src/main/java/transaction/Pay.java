package transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.DatabaseConnection;

public class Pay {

	public static int transactions(String date,int sender_account_number,String sender_name,String sender_email,int receiver_account_number,String receiver_name,String receiver_email,double amount)
	{
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement pStatement = connection.prepareStatement("select balance from account_info where account_number = ?");
			pStatement.setInt(1, sender_account_number);
			ResultSet rSet = pStatement.executeQuery();
			rSet.next();
			if (rSet.getDouble("balance") >= amount) 
			{
				PreparedStatement pStatement2 = connection.prepareStatement("update account_info set balance = balance + ? where account_number = ?");
				pStatement2.setDouble(1, amount);
				pStatement2.setInt(2, receiver_account_number);
				int x = pStatement2.executeUpdate();
				PreparedStatement pStatement3 = connection.prepareStatement("update account_info set balance = balance - ? where account_number = ?");
				pStatement3.setDouble(1, amount);
				pStatement3.setInt(2, sender_account_number);
				int y = pStatement3.executeUpdate();
				PreparedStatement pStatement4 = connection.prepareStatement("insert into transaction_info (date,sender_account_number,sender_name,receiver_account_number,receiver_name,amount,status) values (?,?,?,?,?,?,?)");
				pStatement4.setString(1, date);
				pStatement4.setInt(2,sender_account_number);
				pStatement4.setString(3, sender_name);
				pStatement4.setInt(4,receiver_account_number);
				pStatement4.setString(5,receiver_name);
				pStatement4.setDouble(6, amount);
				pStatement4.setString(7, "Completed");
				int z = pStatement4.executeUpdate();
				if (x == y && x > 0 && z > 0) 
				{
					TransactionEmailThread thread = new TransactionEmailThread(sender_email, receiver_email, amount);
					thread.start();
					
				}
			}
			else {
				PreparedStatement pStatement5 = connection.prepareStatement("insert into transaction_info (date,sender_account_number,sender_name,receiver_account_number,receiver_name,amount,status) values (?,?,?,?,?,?,?)");
				pStatement5.setString(1, date);
				pStatement5.setInt(2,sender_account_number);
				pStatement5.setString(3, sender_name);
				pStatement5.setInt(4,receiver_account_number);
				pStatement5.setString(5,receiver_name);
				pStatement5.setDouble(6, amount);
				pStatement5.setString(7, "Failed");
				int z = pStatement5.executeUpdate();
				if (z > 0) {
					return 1; 
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 2;
	}
}
