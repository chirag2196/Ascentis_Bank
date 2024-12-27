package registration;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Currency;
import java.util.Properties;
import java.util.UUID;

public class OpeningPdf extends Thread{

	String emailString;
	String name;
	String account_type;
	int account_number;
	String startString;
	
	public OpeningPdf(String emailString, String name, String account_type, int account_number, String startString) {
		super();
		this.emailString = emailString;
		this.name = name;
		this.account_type = account_type;
		this.account_number = account_number;
		this.startString = startString;
	}

	@Override
	public void run() {
		String filenameString = pdfgenerator();
		if (filenameString.equals(null)) {
			System.out.println("File not generated");
			return;
		}
		File file = new File("C:/Users/91831/OneDrive/Desktop/IDE/AscentisBank/"+filenameString);
		if (!file.exists()) {
			System.out.println("File not found");
			return;
		}
		String sender_to = emailString;
		String fromString = "ascentisbank@gmail.com";
		String sender_subjectString = "Bank Account Opening";
		String sender_textString = "We are pleased to inform you that your bank account has been successfully opened with Ascentis Bank. Thank you for choosing us for your banking needs!\n\nAttached to this email, you will find a confirmation document containing your account details for your records. Please keep this information secure.\r\n"
				+ "Thank you for banking with us!\r\n"
				+ "Best Regards,\r\n"
				+ "Ascentis Bank\r\n";
				
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth",true);
		properties.put("mail.smtp.starttls.enable",true);
		properties.put("mail.smtp.port","587");
		properties.put("mail.smtp.host","smtp.gmail.com");
		System.out.println("con1");
		String username = "ascentisbank";
		String password = "csmuiubvfybjxngx";
		
		Session session = Session.getInstance(properties,new Authenticator() 
		{
			
			protected PasswordAuthentication getPasswordAuthentication() 
			{
				return new PasswordAuthentication(username,password);
			}
		});
		System.out.println("con2");
		try 
		{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromString));
			message.setRecipient(Message.RecipientType.TO,new InternetAddress(sender_to));
			message.setSubject(sender_subjectString);
			MimeBodyPart part1 = new MimeBodyPart();
			part1.setText(sender_textString);
			
			MimeBodyPart part2 = new MimeBodyPart();
			part2.attachFile(file);
			
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(part1);
			multipart.addBodyPart(part2);
			
			message.setContent(multipart);
			System.out.println("con3");
			Transport.send(message);
			System.out.println("Email sent");
			if (file.delete()) {
				System.out.println("File deleted successfully");
			}
			else {
				System.out.println("File not deleted");
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			
		}
	}
	
    public String pdfgenerator() {
        Document document = new Document(PageSize.A4);
        try {
            String dest = "Welcome_Letter"+ UUID.randomUUID().toString() +".pdf"; // Output file
            PdfWriter.getInstance(document, new FileOutputStream("C:/Users/91831/OneDrive/Desktop/IDE/AscentisBank/"+dest));
            document.open();

            BaseColor customGreen = new BaseColor(0, 38, 119);
            // Set font
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24,Font.NORMAL, customGreen);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 15,Font.NORMAL, new BaseColor(0,0,0));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18,Font.NORMAL, customGreen);

            Image bankLogo = Image.getInstance("C:/Users/91831/OneDrive/Desktop/IDE/AscentisBank/logo.jpeg");
            bankLogo.scaleToFit(200, 100);
            document.add(bankLogo);
            
            // Title
            Paragraph title = new Paragraph("Account Confirmation", titleFont);
            title.setSpacingBefore(10);
            title.setSpacingAfter(10);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph name1 = new Paragraph("Name: "+ name , normalFont);
            name1.setSpacingAfter(5);
            document.add(name1);

            
            // Date and Receipt Number
            Paragraph receiptNumber = new Paragraph("Account No: "+ account_number , normalFont);
            receiptNumber.setSpacingAfter(5);
            document.add(receiptNumber);

            Paragraph date = new Paragraph("Opening Date: "+startString, normalFont);
            date.setSpacingAfter(5);
            document.add(date);
            
            Paragraph type = new Paragraph("Account Type: "+ account_type , normalFont);
            type.setSpacingAfter(5);
            document.add(type);
            
            Chunk underlinedText = new Chunk("Welcome Letter", titleFont);
            underlinedText.setUnderline(0.8f, -1f);
            
            Paragraph title2 = new Paragraph(underlinedText);
            title2.setSpacingBefore(20);
            title2.setSpacingAfter(10);
            title2.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title2);
            
            Paragraph contentParagraph = new Paragraph("We sincerely thank you for choosing Ascentis Bank as your trusted financial partner. We are committed to providing you with exceptional service and a range of banking products designed to meet your needs.\n\nAscentis has been dedicated to fostering financial growth and security for our customers. With a network robust online and mobile banking services, we strive to make your banking experience convenient and accessible.\n\nOur team is here to support you every step of the way, ensuring that your banking journey is smooth and rewarding.\n\n Welcome to the Ascentis Bank family!");
           
            contentParagraph.setSpacingBefore(20);
            contentParagraph.setSpacingAfter(10);
            document.add(contentParagraph);

           

            // Footer
            Paragraph footer = new Paragraph("\nThank you for choosing our bank!", footerFont);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(footer);
            
            Paragraph infoParagraph = new Paragraph("Head Branch:\nAscentis Bank\n295, Sudama Nagar,\nIndore,\nM.P.- 452008",normalFont);
            infoParagraph.setSpacingBefore(50);
            document.add(infoParagraph);
            
            return dest;

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        
        System.out.println("PDF created successfully.");
        return null;
    }
}
