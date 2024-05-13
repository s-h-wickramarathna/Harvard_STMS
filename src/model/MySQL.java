/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author sanch
 */
public class MySQL {

    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/smtp", "root", "123456");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Statement Connection() throws Exception {
        Statement statement = connection.createStatement();
        return statement;
    }
    
    public static void Iud(String query){
        try {
        Connection().executeUpdate(query);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public static ResultSet Search(String query)throws Exception{
        ResultSet resultSet = Connection().executeQuery(query);
        return resultSet;
    }
    
}
