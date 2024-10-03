package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/Withdrawmoney")
public class Withdrawmoney extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String url ="jdbc:mysql://localhost:3306/project";
	String un = "root";
	String pn = "admin";
	Connection con = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt1 = null;
	PreparedStatement pstmt2 = null;
	String query = "INSERT INTO withdrawmoney (accountno, amount) VALUES (?, ?);";
	String query1 = "UPDATE createaccount SET balance = balance - ? WHERE accountno = ?;";
	String query2 = "SELECT balance FROM createaccount WHERE accountno = ?;";
	
	@Override
	public void init() throws ServletException {
		try {
			// Load MySQL driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Establish connection to the database
			con = DriverManager.getConnection(url, un, pn);
			System.out.println("Connection to the database established successfully.");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new ServletException("Error establishing database connection.", e);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String accountnoStr = req.getParameter("accountno");
		String amountStr = req.getParameter("amount");
		
		PrintWriter writer = resp.getWriter();
		resp.setContentType("text/html");
		
		// Check if parameters are valid
		if (accountnoStr == null || accountnoStr.isEmpty() || amountStr == null || amountStr.isEmpty()) {
			writer.println("<h1 style='color:red; text-align:center;'>Invalid Input: Account number or amount is missing</h1>");
			return;
		}
		
		try {
			int accountno = Integer.parseInt(accountnoStr);
			int amount = Integer.parseInt(amountStr);
			
			// Check if account exists and fetch balance
			pstmt2 = con.prepareStatement(query2);
			pstmt2.setInt(1, accountno);
			ResultSet rs = pstmt2.executeQuery();
			
			if (rs.next()) {
				int balance = rs.getInt("balance");
				
				if (balance >= amount) {
					// Start transaction
					con.setAutoCommit(false);

					// Insert withdrawal record
					pstmt = con.prepareStatement(query);
					pstmt.setInt(1, accountno);
					pstmt.setInt(2, amount);
					pstmt.executeUpdate();
					
					// Update balance
					pstmt1 = con.prepareStatement(query1);
					pstmt1.setInt(1, amount);
					pstmt1.setInt(2, accountno);
					int res1 = pstmt1.executeUpdate();
					
					if (res1 > 0) {
						con.commit(); // Commit the transaction
						writer.println("<h1 style='color:blue; display: flex; justify-content: center; align-items: center; height: 100vh;'>WITHDRAWAL SUCCESSFUL</h1>");
					}
				} else {
					writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>INSUFFICIENT BALANCE</h1>");
				}
			} else {
				writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>ACCOUNT NOT FOUND</h1>");
			}
			
			// Close result set
			rs.close();
		} catch (NumberFormatException e) {
			writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>Invalid input: Please enter valid numbers</h1>");
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				if (con != null) {
					con.rollback(); // Rollback the transaction in case of error
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			writer.println("<h1 style='color:red; display: flex; justify-content: center; align-items: center; height: 100vh;'>Error processing the request</h1>");
		} finally {
			try {
				// Close all resources
				if (pstmt != null) pstmt.close();
				if (pstmt1 != null) pstmt1.close();
				if (pstmt2 != null) pstmt2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void destroy() {
		try {
			if (con != null) con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
