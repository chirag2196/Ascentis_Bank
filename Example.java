package com.bank;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/getJsonData")
public class Example extends HttpServlet
{
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create a JSON object with sample data
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "success");
        jsonResponse.put("message", "Here is your JSON data");
        jsonResponse.put("data", "sample data");

        // Write JSON response
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

//    public static void main(String[] args) throws LifecycleException {
//        // Create and configure Tomcat instance
//        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(8080);
//
//        // Define web application context and add the servlet
//        tomcat.addContext("", null);
//        Tomcat.addServlet(tomcat.getHost(), "JsonServlet", new JsonServlet());
//        tomcat.addServletMappingDecoded("/api/getJsonData", "JsonServlet");
//
//        // Start the server
//        tomcat.start();
//        System.out.println("Server started at http://localhost:8080/api/getJsonData");
//        tomcat.getServer().await();
//    }
}
