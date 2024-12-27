package dashBoard;

public class AccountList {

	String fullName;
	String username;
	String accountNumber;
	String age;
	String email;
	String phone;
	String address;
	String AccountType;
	String Occupation;
	
	public AccountList(String fullName, String username, String accountNumber, String age, String email, String phone,
			String address, String accountType, String occupation) {
		super();
		this.fullName = fullName;
		this.username = username;
		this.accountNumber = accountNumber;
		this.age = age;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.AccountType = accountType;
		this.Occupation = occupation;
	}
	
	public String getFullName() {
		return fullName;
	}
	public String getUsername() {
		return username;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public String getAge() {
		return age;
	}
	public String getEmail() {
		return email;
	}
	public String getPhone() {
		return phone;
	}
	public String getAddress() {
		return address;
	}
	public String getAccountType() {
		return AccountType;
	}
	public String getOccupation() {
		return Occupation;
	}
	
}
