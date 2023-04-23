package db.management.advanced.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author AlpeenDGRYL
 */
public class DBconnect {

    String HOST;
    String USER_NAME;
    String PASSWORD;
    String DB_NAME;
    Connection conn;

    public void CreateConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            HOST = JOptionPane.showInputDialog("Enter the database host [localhost]");
            USER_NAME = JOptionPane.showInputDialog("Enter the database username [root]");
            PASSWORD = JOptionPane.showInputDialog("Enter the database password [null]");
            DB_NAME = JOptionPane.showInputDialog("Enter the database name [javadb]");

            String connectionString = "jdbc:mysql://" + HOST + ":3306/" + DB_NAME;

            conn = DriverManager.getConnection(connectionString, USER_NAME, PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }
}
