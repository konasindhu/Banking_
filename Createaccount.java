package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Createaccount")
public class Createaccount extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String url = "jdbc:mysql://localhost:3306/project";
    private String un = "root";
    private String pn = "sindhu";
    private Connection con = null;
    private PreparedStatement pstmt = null;
    private String query = "INSERT INTO Createaccount (accountno, password, name, balance) VALUES (?, ?, ?, ?);";

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
            con = DriverManager.getConnection(url, un, pn);
            System.out.println("Connection established: " + (con != null));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ServletException("Database driver not found", e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Failed to connect to the database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (con == null) {
            throw new ServletException("Database connection not initialized.");
        }

        int accountno = Integer.parseInt(req.getParameter("acc"));
        String password = req.getParameter("pw"); // Changed to String
        String name = req.getParameter("na");
        int balance = Integer.parseInt(req.getParameter("ib"));

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, accountno);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setInt(4, balance);

            int res = pstmt.executeUpdate();
            PrintWriter writer = resp.getWriter();
            if (res > 0) {
                writer.println("Account created successfully");
            } else {
                writer.println("Failed to create account");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        }
    }

    @Override
    public void destroy() {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

