package fd;

public class FDlist {
	String fd_Id;
	String name;
	double interestRate;
	double amount;
	String endDate;
	public FDlist(String fd_Id,String name, double interestRate, double amount, String endDate) {
		super();
		this.fd_Id = fd_Id;
		this.name = name;
		this.interestRate = interestRate;
		this.amount = amount;
		this.endDate = endDate;
	}
	public String getFd_Id() {
		return fd_Id;
	}
	public String getName() {
		return name;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public double getAmount() {
		return amount;
	}
	public String getEndDate() {
		return endDate;
	}
	
	
}
