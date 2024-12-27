package fd;

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
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Properties;
import java.util.UUID;

public class GeneratePDF extends Thread{
	
	String nameString;
	String startString;
	String emailString;
	String endString;
	int duration;
	int account_number;
	double interest;
	double amount;
	double maturity_amount;
	
	public GeneratePDF(String nameString, String startString,String emailString, String endString, int duration, int account_number,
			double interest, double amount, double maturity_amount) {
		super();
		this.nameString = nameString;
		this.startString = startString;
		this.endString = endString;
		this.duration = duration;
		this.account_number = account_number;
		this.interest = interest;
		this.amount = amount;
		this.maturity_amount = maturity_amount;
		this.emailString = emailString;
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
		String sender_subjectString = "Fixed Deposit Receipt";
		String sender_textString = "We are pleased to inform you that your Fixed Deposit (FD) has been successfully created with Ascentis Bank. Thank you for choosing us for your investment needs.\n\nYour investment is secure with us, and you will earn interest on your deposit as per the terms agreed upon. We encourage you to keep this receipt safe for future reference.\r\n"
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
            String dest = "receipt"+ UUID.randomUUID().toString() +".pdf"; // Output file
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
            Paragraph title = new Paragraph("Fixed Deposit Receipt", titleFont);
            title.setSpacingBefore(40);
            title.setSpacingAfter(10);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Date and Receipt Number
            Paragraph receiptNumber = new Paragraph("Account No: "+ account_number , normalFont);
            receiptNumber.setSpacingAfter(10);
            document.add(receiptNumber);
            LocalDateTime datet = LocalDateTime.parse(startString);
            LocalDate date2 = datet.toLocalDate();
            Paragraph date = new Paragraph("Date: "+date2, normalFont);
            date.setSpacingAfter(30);
            document.add(date);

            // Table for deposit details
            PdfPTable table = new PdfPTable(2); // 2 columns
            table.setWidthPercentage(100);
            
            // Table header
            PdfPCell header1 = new PdfPCell(new Paragraph("Description"));
//            header1.setBackgroundColor(customGreen);
            header1.setVerticalAlignment(Element.ALIGN_MIDDLE); 
            header1.setPadding(10f);  
            header1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header1);
            
            PdfPCell header2 = new PdfPCell(new Paragraph("Details"));
//            header2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header2.setPadding(10f);
            header2.setHorizontalAlignment(Element.ALIGN_CENTER);
            header2.setVerticalAlignment(Element.ALIGN_MIDDLE); 
            table.addCell(header2);

            // Adding rows
            PdfPCell r1c1 = new PdfPCell(new Paragraph("Customer Name"));
            r1c1.setPadding(6f);
            r1c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(r1c1);
           
            PdfPCell r1c2 =new PdfPCell(new Paragraph(nameString));
            r1c2.setPadding(6f);
            r1c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(r1c2);
            
//            Currency inrCurrency = Currency.getInstance("INR");
            // Get the symbol of the currency
//            String rupeeSymbol = inrCurrency.getSymbol();
//            String rupeeSymbol = "\u20B9";
            PdfPCell r2c1 =new PdfPCell(new Paragraph("Deposit Amount"));
            r2c1.setPadding(6f);
            r2c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(r2c1);
            
            PdfPCell r2c2 =new PdfPCell(new Paragraph("Rs "+amount));
            r2c2.setPadding(6f);
            r2c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(r2c2);
           
            PdfPCell r3c1 = new PdfPCell(new Paragraph("Duration"));
            r3c1.setPadding(6f);
            r3c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(r3c1);
            String yearString = "years";
            if (duration == 6) {
				yearString = "months";
			}
            
            PdfPCell r3c2 =new PdfPCell(new Paragraph(duration +" " + yearString));
            r3c2.setPadding(6f);
            r3c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(r3c2);
            
            PdfPCell r4c1 =new PdfPCell(new Paragraph("Interest Rate"));
            r4c1.setPadding(6f);
            r4c1.setVerticalAlignment(Element.ALIGN_BASELINE);
            r4c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(r4c1);
            
            PdfPCell r4c2 =new PdfPCell(new Paragraph(interest+"% per annum"));
            r4c2.setPadding(6f);
            r4c2.setVerticalAlignment(Element.ALIGN_BOTTOM);
            r4c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(r4c2);
            
            PdfPCell r5c1 =new PdfPCell(new Paragraph("Maturity Date"));
            r5c1.setPadding(6f);
            r5c1.setVerticalAlignment(Element.ALIGN_BASELINE);
            r5c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(r5c1);
            
            PdfPCell r5c2 =new PdfPCell(new Paragraph(endString));
            r5c2.setPadding(6f);
            r5c2.setVerticalAlignment(Element.ALIGN_BOTTOM);
            r5c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(r5c2);
            
            PdfPCell r6c1 =new PdfPCell(new Paragraph("Maturity Amount"));
            r6c1.setPadding(6f);
            r6c1.setVerticalAlignment(Element.ALIGN_BASELINE);
            r6c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(r6c1);
            
            PdfPCell r6c2 =new PdfPCell(new Paragraph("Rs "+maturity_amount));
            r6c2.setPadding(6f);
            r6c2.setVerticalAlignment(Element.ALIGN_BOTTOM);
            r6c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(r6c2);
            
            document.add(table);

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

