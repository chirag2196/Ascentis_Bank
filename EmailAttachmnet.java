package com.bank;

import java.util.Properties;
import java.util.UUID;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailAttachmnet {
	
	public static void main(String[] args) {
		int account_number = 800001;
		String account_type = "Current";
		String startString = "2024-12-01";
		String name = "Chirag Khandelwal";
		
		Document document = new Document(PageSize.A4);
        try {
            String dest = "Welcome Letter"+ UUID.randomUUID().toString() +".pdf"; // Output file
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            BaseColor customGreen = new BaseColor(0, 38, 119);
            // Set font
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24,Font.NORMAL, customGreen);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 15,Font.NORMAL, new BaseColor(0,0,0));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18,Font.NORMAL, customGreen);

            Image bankLogo = Image.getInstance("logo.jpeg");
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
//            title2.add(underlinedText);
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
            

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        
        System.out.println("PDF created successfully.");
       
    }
}

