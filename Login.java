package com.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.*;

@WebServlet("/login1")
public class Login extends HttpServlet {

    // In-memory storage for the form data
    private JSONObject lastFormData = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Enable CORS by adding necessary headers
        response.setHeader("Access-Control-Allow-Origin", "https://glorious-space-invention-7v7wvvvg6v4w2xgrr-5173.app.github.dev");  // Allow requests from any origin
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE"); // Specify allowed methods
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        // Set the request character encoding to handle form data properly
        request.setCharacterEncoding("UTF-8");
        
        
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }

        // Convert the JSON string to a JSONObject
        JSONObject jsonObject = new JSONObject(jsonBuffer.toString());

        // Retrieve data from the JSONObject
        String name = jsonObject.getString("name");
        String email = jsonObject.getString("email");
        String mobile = jsonObject.getString("mobile");
        String state = jsonObject.getString("state");
        String message = jsonObject.getString("message");

        // Retrieve form data from request
//        String name = request.getParameter("name");
        System.out.println(jsonObject.getString("name"));
//        String email = request.getParameter("email");
//        String mobile = request.getParameter("mobile");
//        String state = request.getParameter("state");
//        String message = request.getParameter("message");

        // Store the form data in memory using JSON (similar to Express body parsing)
        lastFormData = new JSONObject();
        lastFormData.put("name", name);
        lastFormData.put("email", email);
        lastFormData.put("mobile", mobile);
        lastFormData.put("state", state);
        lastFormData.put("message", message);

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Send JSON response with stored data
        JSONObject responseData = new JSONObject();
        responseData.put("message", "Form data received successfully");
        responseData.put("data", lastFormData);
        System.out.println(lastFormData);
        // Send response
        PrintWriter out = response.getWriter();
        out.print(responseData.toString());
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Enable CORS for GET requests
        response.setHeader("Access-Control-Allow-Origin", "https://shrew-causal-rattler.ngrok-free.app");  
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        
        // Set response type to HTML
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (lastFormData != null) {
            // If form data exists, display it in a simple HTML format
            out.println("<h1>Last Submitted Form Data:</h1>");
            out.println("<p><strong>Name:</strong> " + lastFormData.getString("name") + "</p>");
            out.println("<p><strong>Email:</strong> " + lastFormData.getString("email") + "</p>");
            out.println("<p><strong>Mobile:</strong> " + lastFormData.getString("mobile") + "</p>");
            out.println("<p><strong>State:</strong> " + lastFormData.getString("state") + "</p>");
            out.println("<p><strong>Message:</strong> " + lastFormData.getString("message") + "</p>");
        } else {
            // If no data has been submitted
            out.println("<h1>No form data has been submitted yet.</h1>");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle CORS preflight request
        response.setHeader("Access-Control-Allow-Origin", "https://glorious-space-invention-7v7wvvvg6v4w2xgrr-5173.app.github.dev");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
