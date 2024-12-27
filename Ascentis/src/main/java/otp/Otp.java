package otp;

import java.security.SecureRandom;

public class Otp {
	public static String getOTP()
	{
		SecureRandom secureRandom = new SecureRandom();
	    StringBuilder otpBuilder = new StringBuilder(6);
	    
	    for (int i = 0; i < 6; i++) {
	        int num = secureRandom.nextInt(10); 
	        otpBuilder.append(num);
	    }
	    
	    return otpBuilder.toString();
	}
}
