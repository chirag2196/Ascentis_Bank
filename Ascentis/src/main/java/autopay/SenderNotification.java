package autopay;

public class SenderNotification {

	String sender_name;
	int sender_account_number;
	int autopay_id;
	public SenderNotification(String sender_name, int sender_account_number,int autopay_id) {
		super();
		this.sender_name = sender_name;
		this.sender_account_number = sender_account_number;
		this.autopay_id = autopay_id;
	}
	
}
