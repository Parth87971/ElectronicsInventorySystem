package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Sale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    /** Returns all sales with product name and customer name (joined). */
    public List<Object[]> getAllWithDetails() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT sa.sale_id, sa.sale_date, pr.product_name, "
                   + "c.customer_name, sa.quantity, sa.selling_price "
                   + "FROM sale sa "
                   + "JOIN product pr ON sa.product_id = pr.product_id "
                   + "JOIN customer c ON sa.customer_id = c.customer_id "
                   + "ORDER BY sa.sale_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("sale_id"),
                        rs.getDate("sale_date"),
                        rs.getString("product_name"),
                        rs.getString("customer_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("selling_price")
                });
            }
        }
        return list;
    }

    public Sale getById(int saleId) throws SQLException {
        String sql = "SELECT * FROM sale WHERE sale_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Sale(
                        rs.getInt("sale_id"),
                        rs.getDate("sale_date"),
                        rs.getInt("quantity"),
                        rs.getDouble("selling_price"),
                        rs.getInt("product_id"),
                        rs.getInt("customer_id")
                );
            }
        }
        return null;
    }

    public List<Object[]> searchWithDetails(String keyword) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT sa.sale_id, sa.sale_date, pr.product_name, "
                   + "c.customer_name, sa.quantity, sa.selling_price "
                   + "FROM sale sa "
                   + "JOIN product pr ON sa.product_id = pr.product_id "
                   + "JOIN customer c ON sa.customer_id = c.customer_id "
                   + "WHERE pr.product_name LIKE ? OR c.customer_name LIKE ? "
                   + "ORDER BY sa.sale_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("sale_id"),
                        rs.getDate("sale_date"),
                        rs.getString("product_name"),
                        rs.getString("customer_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("selling_price")
                });
            }
        }
        return list;
    }

    public int getTotalSales() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sale";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantity * selling_price), 0) FROM sale";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        }
    }
}
