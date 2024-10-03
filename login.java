package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
		    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        // Set content type for HTML response
	        response.setContentType("text/html");
	        PrintWriter out = response.getWriter();

	        // Get the account number and password from the login form
	        String accountNo = request.getParameter("accountNo");
	        String password = request.getParameter("password");

	        // Database connection details
	        String dbURL = "jdbc:mysql://localhost:3306/project";
	        String dbUser = "root";
	        String dbPassword = "sindhu";

	        try {
	            // Load the MySQL JDBC driver
	            Class.forName("com.mysql.cj.jdbc.Driver");

	            // Establish a connection to the database
	            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

	            // SQL query to check if the account exists with the provided password
	            String sql = "SELECT * FROM createaccount WHERE accountno = ? AND password = ?";
	            PreparedStatement ps = conn.prepareStatement(sql);
	            ps.setString(1, accountNo);
	            ps.setString(2, password);

	            // Execute the query
	            ResultSet rs = ps.executeQuery();

	            // If account exists, redirect to another HTML page
	            if (rs.next()) {
	                response.sendRedirect("dashboard.html");
	            } else {
	                // If no account found, display an error message
	                out.println("<html><body>");
	                out.println("<h3 >No Account Found. Please try again.</h3>");
	                out.println("<form action='index.html' method='post'>");
	                out.println("<input type='submit' value='Try Again'>");
	                out.println("</form>");
	                out.println("</body></html>");
	            }

	            // Close resources
	            rs.close();
	            ps.close();
	            conn.close();

	        } catch (Exception e) {
	            out.println("<h3>Error connecting to the database: " + e.getMessage() + "</h3>");
	        }
	    }

	    // Handles GET requests by calling the doPost method
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doPost(request, response);
	    }
	}


