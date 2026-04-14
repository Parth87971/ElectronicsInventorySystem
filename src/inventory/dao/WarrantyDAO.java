package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Warranty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyDAO {

    /** Returns all warranties with sale details (joined). */
    public List<Object[]> getAllWithDetails() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT w.warranty_id, w.sale_id, sa.sale_date, "
                   + "pr.product_name, c.customer_name, "
                   + "w.warranty_start, w.warranty_end "
                   + "FROM warranty w "
                   + "JOIN sale sa ON w.sale_id = sa.sale_id "
                   + "JOIN product pr ON sa.product_id = pr.product_id "
                   + "JOIN customer c ON sa.customer_id = c.customer_id "
                   + "ORDER BY w.warranty_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("warranty_id"),
                        rs.getInt("sale_id"),
                        rs.getDate("sale_date"),
                        rs.getString("product_name"),
                        rs.getString("customer_name"),
                        rs.getDate("warranty_start"),
                        rs.getDate("warranty_end")
                });
            }
        }
        return list;
    }

    public Warranty getBySaleId(int saleId) throws SQLException {
        String sql = "SELECT * FROM warranty WHERE sale_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Warranty(
                        rs.getInt("warranty_id"),
                        rs.getInt("sale_id"),
                        rs.getDate("warranty_start"),
                        rs.getDate("warranty_end")
                );
            }
        }
        return null;
    }

    /** Search warranties by product name or customer name. */
    public List<Object[]> searchWithDetails(String keyword) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT w.warranty_id, w.sale_id, sa.sale_date, "
                   + "pr.product_name, c.customer_name, "
                   + "w.warranty_start, w.warranty_end "
                   + "FROM warranty w "
                   + "JOIN sale sa ON w.sale_id = sa.sale_id "
                   + "JOIN product pr ON sa.product_id = pr.product_id "
                   + "JOIN customer c ON sa.customer_id = c.customer_id "
                   + "WHERE pr.product_name LIKE ? OR c.customer_name LIKE ? "
                   + "ORDER BY w.warranty_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("warranty_id"),
                        rs.getInt("sale_id"),
                        rs.getDate("sale_date"),
                        rs.getString("product_name"),
                        rs.getString("customer_name"),
                        rs.getDate("warranty_start"),
                        rs.getDate("warranty_end")
                });
            }
        }
        return list;
    }
}
