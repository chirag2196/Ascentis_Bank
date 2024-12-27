package autopayment;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import transaction.Pay;

import connection.Auth;
import connection.DatabaseConnection;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletResponse;
import otp.Otp;

@WebListener
public class Automatic implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize the ScheduledExecutorService
        scheduler = Executors.newScheduledThreadPool(1); // Use 2 threads for concurrent tasks

        // Schedule autopay check every 24 hours
        scheduler.scheduleAtFixedRate(() -> {
            checkForAutopay();
        }, 0, 24, TimeUnit.HOURS);

//         Schedule blocked email check every hour
        scheduler.scheduleAtFixedRate(() -> {
            checkForBlockedEmails();
        }, 0, 1, TimeUnit.HOURS);

        System.out.println("Scheduler started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Shutdown the scheduler gracefully
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        System.out.println("Scheduler stopped.");
    }

    private void checkForAutopay() {
       
        System.out.println("Checking for autopay...");
        try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ascentis_bank","root","Kchirag2196@");
		
        PreparedStatement pStatement = connection.prepareStatement("select * from autopay_info where next_pay_date = ?");
        pStatement.setString(1, LocalDate.now().toString());
        ResultSet rSet = pStatement.executeQuery();
        while (rSet.next()) {
			int x = Pay.transactions( LocalDateTime.now().toString(),rSet.getInt(4), rSet.getString(5), email(rSet.getInt(4)), rSet.getInt(6),rSet.getString(7),email(rSet.getInt(6)), rSet.getDouble(8));
			LocalDate next_payDate = LocalDate.parse(rSet.getString(3));
			LocalDate endDate =  LocalDate.parse(rSet.getString(2));
			if (rSet.getString(9).equals("weekly")) {
				next_payDate = next_payDate.plusWeeks(1);
			}
			else if (rSet.getString(9).equals("monthly")) {
				next_payDate = next_payDate.plusMonths(1);
			}
			else {
				next_payDate = next_payDate.plusMonths(3);
			}
			if (endDate.isAfter(next_payDate)) {
				PreparedStatement pStatement2 = connection.prepareStatement("update autopay_info set next_pay_date = ? where sender_account_number = ? and receiver_account_number = ? and end_date = ?");
				pStatement2.setString(1, next_payDate.toString());
				pStatement2.setInt(2, rSet.getInt(4));
				pStatement2.setInt(3,rSet.getInt(6));
				pStatement2.setString(4, endDate.toString());
				pStatement2.executeUpdate();
			}
			else {
				PreparedStatement pStatement2 = connection.prepareStatement("delete from autopay_info where sender_account_number = ? and receiver_account_number = ? and end_date = ?");
				pStatement2.setInt(1, rSet.getInt(4));
				pStatement2.setInt(2,rSet.getInt(6));
				pStatement2.setString(3, endDate.toString());
				pStatement2.executeUpdate();
			}
			if (x == 1) {
				//Email send to sender that there is not sufficent balance to pay for your autopay transactions
				String to = email(rSet.getInt(4));
				String fromString = "ascentisbank@gmail.com";
				String otpString = Otp.getOTP();
				String subjectString = "Important: Autopay Transaction Failed Due to Insufficient Balance";
				String textString = "Dear User,\r\n"
						+ "We hope this message finds you well.\r\n"
						+ "We are writing to inform you that your recent autopay transaction scheduled for "+LocalDate.now()+" has failed due to insufficient balance in your account. As a result, the payment for "+rSet.getString(7)+" could not be processed.\r\n"
						+ "Details of the Transaction:\r\n"
						+ "Transaction Amount: "+rSet.getDouble(8)+"\r\n"
						+ "Scheduled Date: "+LocalDate.now()+"\r\n"
						
						+ "To ensure uninterrupted service, please take the following actions:\r\n"
						+ "Check Your Account Balance: Please verify your account balance and ensure that sufficient funds are available for future transactions.\r\n"
						+ "Update Payment Information: If necessary, update your payment method in your account settings.\r\n"
						+ "Retry Payment: You can manually process the payment through our website or app.\r\n"
						+ "If you have any questions or need assistance, please do not hesitate to contact our customer support team at. We are here to help!\r\n"
						+ "Thank you for your attention to this matter.\r\n"
						+ "Best regards,\r\n";
						
				
				Properties properties = new Properties();
				properties.put("mail.smtp.auth",true);
				properties.put("mail.smtp.starttls.enable",true);
				properties.put("mail.smtp.port","587");
				properties.put("mail.smtp.host","smtp.gmail.com");
				System.out.println("con1");
				String username = "ascentisbank";
				String password = "csmuiubvfybjxngx";
				
				Auth.name(username, password, properties, fromString, to, subjectString, textString);
			}
        }
        }catch (Exception e) {
			e.printStackTrace();
		}
    }


    private void checkForBlockedEmails() {
       
        System.out.println("Checking for blocked emails...");
        Connection connection = DatabaseConnection.getConnection();
        try {
			PreparedStatement pStatement = connection.prepareStatement("DELETE FROM blocked_emails WHERE blocked_at < NOW()  - INTERVAL 24 HOUR");
			pStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
	private String email(int attribute) {
		
		Connection connection = DatabaseConnection.getConnection();
		try {
			PreparedStatement pStatement = connection.prepareStatement("select email from personal_info where account_number = ?");
			pStatement.setInt(1, attribute);
			ResultSet executeQuery = pStatement.executeQuery();
			if (executeQuery.next()) {
				return executeQuery.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}


