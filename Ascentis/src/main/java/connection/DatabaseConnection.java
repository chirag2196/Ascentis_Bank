package connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
	public static Connection getConnection() 
	{
		Connection connection = null;
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ascentis_bank","root","Kchirag2196@");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	public static void main(String[] args) {
		System.out.println(DatabaseConnection.getConnection());
	}
}
