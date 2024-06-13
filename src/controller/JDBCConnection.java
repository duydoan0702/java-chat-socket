package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
	public static Connection getJDBCConnection() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:mysql://localhost:3306/test";
        final String user = "root";
        final String password = "Ns369369";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; 
        
    }
        public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection connection = getJDBCConnection();
        
        if (connection != null) {
            System.out.println("Data connection successful");
        } else {
            System.out.println("Data connection failed");
        }
    }
}
