package autopay;

public class AutopayList {
	String autopay_Id;
	String startDate;
	String endDate;
	String name;
	String frequency;
	double amount;
	
	public AutopayList(String autopay_Id,String startDate, String endDate,String name, String frequency, double amount) {
		super();
		this.autopay_Id = autopay_Id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.frequency = frequency;
		this.amount = amount;
	}
	
	public String getAutopay_Id() {
		return autopay_Id;
	}
	public String getName() {
		return name;
	}
	public String getStartDate() {
		return startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public String getFrequency() {
		return frequency;
	}
	public double getAmount() {
		return amount;
	}
	
}
