package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Purchase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    /** Returns all purchases with product name and supplier name (joined). */
    public List<Object[]> getAllWithDetails() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT pu.purchase_id, pu.purchase_date, pr.product_name, "
                   + "s.supplier_name, pu.quantity, pu.cost_price "
                   + "FROM purchase pu "
                   + "JOIN product pr ON pu.product_id = pr.product_id "
                   + "JOIN supplier s ON pu.supplier_id = s.supplier_id "
                   + "ORDER BY pu.purchase_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("purchase_id"),
                        rs.getDate("purchase_date"),
                        rs.getString("product_name"),
                        rs.getString("supplier_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost_price")
                });
            }
        }
        return list;
    }

    /** Fetches a purchase record by ID (raw, without joins). */
    public Purchase getById(int purchaseId) throws SQLException {
        String sql = "SELECT * FROM purchase WHERE purchase_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Purchase(
                        rs.getInt("purchase_id"),
                        rs.getDate("purchase_date"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost_price"),
                        rs.getInt("product_id"),
                        rs.getInt("supplier_id")
                );
            }
        }
        return null;
    }

    public List<Object[]> searchWithDetails(String keyword) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT pu.purchase_id, pu.purchase_date, pr.product_name, "
                   + "s.supplier_name, pu.quantity, pu.cost_price "
                   + "FROM purchase pu "
                   + "JOIN product pr ON pu.product_id = pr.product_id "
                   + "JOIN supplier s ON pu.supplier_id = s.supplier_id "
                   + "WHERE pr.product_name LIKE ? OR s.supplier_name LIKE ? "
                   + "ORDER BY pu.purchase_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("purchase_id"),
                        rs.getDate("purchase_date"),
                        rs.getString("product_name"),
                        rs.getString("supplier_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost_price")
                });
            }
        }
        return list;
    }

    public int getTotalPurchases() throws SQLException {
        String sql = "SELECT COUNT(*) FROM purchase";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
