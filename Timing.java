package com.bank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.TextFormatParseInfoTree;

public class Timing {

	public static void main(String[] args) {
//		LocalDate date = LocalDate.now();
//		LocalTime time = LocalTime.now();
//		LocalDateTime dTime = LocalDateTime.now();
//		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        
//        // Format and print the time
//        String formattedTime = time.format(formatter);
//        System.out.println("Current Time: " + formattedTime);
//		
//        LocalDate newDateAfterDays = date.plusYears(1);
//        System.out.println("Date after adding 10 days: " + newDateAfterDays);
//        ArrayList<Integer> l1 = new ArrayList<Integer>();
//        l1.add(20);
//        l1.add(50);
//		System.out.println(l1.get(0));
//		System.out.println(l1.get(1));
//		System.out.println(dTime);
		
//		LocalDateTime dateTime = LocalDateTime.parse("2024-11-23T17:28:38");
		LocalDate dobDate = LocalDate.of(2002, 6, 6);
		LocalDate nowDate = LocalDate.now();
		
		System.out.println(Period.between(dobDate, nowDate).getYears());
//		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//		 scheduler.scheduleAtFixedRate(() -> {
//	           System.out.println("Hello Chirag");;
//	        }, 2, 5, TimeUnit.SECONDS);
//		 System.out.println("Scheduler started.");
//		 
//		 try {
//	            // Let it run for 20 seconds before shutting down
//	            Thread.sleep(10000);
//	        } catch (InterruptedException e) {
//	            Thread.currentThread().interrupt();
//	        }
//
//	        // Shutdown the scheduler
//	        scheduler.shutdown();
//	        System.out.println("Scheduler stopped.");
	}

}
