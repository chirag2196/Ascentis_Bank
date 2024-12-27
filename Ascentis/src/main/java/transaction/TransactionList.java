package transaction;

public class TransactionList {
	
	String date;
	String name;
	String type;
	String status;
	double amount;
	
	public TransactionList(String date, String name, String type, String status, double amount) {
		super();
		this.date = date;
		this.name = name;
		this.type = type;
		this.status = status;
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getStatus() {
		return status;
	}

	public double getAmount() {
		return amount;
	}
	
	
}
