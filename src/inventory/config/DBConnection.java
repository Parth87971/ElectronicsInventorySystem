package inventory.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class.
 * Provides a centralized method to obtain MySQL connections.
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/electronics_inventory"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Parth@01234";   // <-- Update with your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. "
                    + "Add mysql-connector-j JAR to classpath.", e);
        }
    }

    /** Returns a new connection to the electronics_inventory database. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
