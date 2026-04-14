package inventory;

import inventory.config.DBConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
    public static void main(String[] args) {
        System.out.println("Initializing Database...");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop all tables in reverse dependency order
            stmt.executeUpdate("DROP TABLE IF EXISTS warranty;");
            stmt.executeUpdate("DROP TABLE IF EXISTS stock;");
            stmt.executeUpdate("DROP TABLE IF EXISTS sale;");
            stmt.executeUpdate("DROP TABLE IF EXISTS purchase;");
            stmt.executeUpdate("DROP TABLE IF EXISTS customer;");
            stmt.executeUpdate("DROP TABLE IF EXISTS supplier;");
            stmt.executeUpdate("DROP TABLE IF EXISTS product;");
            stmt.executeUpdate("DROP TABLE IF EXISTS brand;");
            System.out.println("Old tables dropped.");

            String sql = new String(Files.readAllBytes(Paths.get("D:\\ElectronicsInventorySystem\\sql\\schema.sql")));
            String[] statements = sql.split(";");

            for (String s : statements) {
                if (s.trim().isEmpty()) continue;
                // skip USE statements as our connection URL already selects the DB
                if (s.trim().toUpperCase().startsWith("CREATE DATABASE") || s.trim().toUpperCase().startsWith("USE")) continue;
                System.out.println("Executing: " + s.trim().substring(0, Math.min(50, s.trim().length())) + "...");
                stmt.execute(s.trim());
            }

            System.out.println("Database successfully initialized with sample data!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
