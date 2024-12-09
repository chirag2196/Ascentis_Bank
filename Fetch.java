package com.bank;

import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Fetch {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Hiber","root","Kchirag2196@");
		PreparedStatement pStatement = connection.prepareStatement("select image from student_address where aid = 1");
		ResultSet executeQuery = pStatement.executeQuery();
		executeQuery.next();
		Blob blob = executeQuery.getBlob(1);
		byte[] array = blob.getBytes(1, (int) blob.length());
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("src/main/java/com/bank/pic.png");
			fileOutputStream.write(array);
			System.out.println("File created");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		blob.free();
	}

}
