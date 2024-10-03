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

import org.apache.catalina.connector.Response;


@WebServlet("/Depositmoney")
public class Depositmoney extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Database connection information
    String url = "jdbc:mysql://localhost:3306/project";
    String username = "root";
    String password = "sindhu";
    
    Connection con = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt1 = null;
    PreparedStatement pstmt2 = null;
    
    // SQL queries
    String insertDepositQuery = "INSERT INTO depositmoney (accountno, amount) VALUES (?, ?);";
    String updateBalanceQuery = "UPDATE createaccount SET balance = balance + ? WHERE accountno = ?;";
    String selectAccountQuery = "SELECT * FROM createaccount WHERE accountno = ?;";
    
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int accountNo = Integer.parseInt(req.getParameter("an"));
        int amount = Integer.parseInt(req.getParameter("am"));
        
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");
        
        try {
            // Check if account exists
            pstmt2 = con.prepareStatement(selectAccountQuery);
            pstmt2.setInt(1, accountNo);
            ResultSet rs = pstmt2.executeQuery();
            
            if (rs.next()) {
                // Account found, proceed with deposit
                
                // Insert into depositmoney table
                pstmt = con.prepareStatement(insertDepositQuery);
                pstmt.setInt(1, accountNo);
                pstmt.setInt(2, amount);
                pstmt.executeUpdate();
                
                // Update balance in createaccount table
                pstmt1 = con.prepareStatement(updateBalanceQuery);
                pstmt1.setInt(1, amount);
                pstmt1.setInt(2, accountNo);
                int updateCount = pstmt1.executeUpdate();
                
                if (updateCount > 0) {
                    writer.println("<h1 style='color:blue;'>Amount deposited successfully.</h1>");
                } else {
                    writer.println("<h1 style='color:red;'>Failed to update balance.</h1>");
                }
            } else {
                // Account not found
                writer.println("<h1 style='color:red;'>Account not found.</h1>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            writer.println("<h1 style='color:red;'>Error occurred while processing the request.</h1>");
        } 
    }

    @Override
    public void destroy() {
        // Close the database connection
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}