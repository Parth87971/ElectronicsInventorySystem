package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    /** Returns all stock rows with product name (joined). */
    public List<Object[]> getAllWithProduct() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, p.product_id, p.product_name, "
                   + "s.available_quantity, s.reorder_level "
                   + "FROM stock s JOIN product p ON s.product_id = p.product_id "
                   + "ORDER BY p.product_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("stock_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("available_quantity"),
                        rs.getInt("reorder_level")
                });
            }
        }
        return list;
    }

    /** Returns stock for a given product, or null if not found. */
    public Stock getByProductId(int productId) throws SQLException {
        String sql = "SELECT * FROM stock WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Stock(
                        rs.getInt("stock_id"),
                        rs.getInt("product_id"),
                        rs.getInt("available_quantity"),
                        rs.getInt("reorder_level")
                );
            }
        }
        return null;
    }

    /** Update the reorder level for a stock entry. */
    public void updateReorderLevel(int stockId, int newLevel) throws SQLException {
        String sql = "UPDATE stock SET reorder_level=? WHERE stock_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newLevel);
            ps.setInt(2, stockId);
            ps.executeUpdate();
        }
    }

    /** Search stock by product name. */
    public List<Object[]> searchByProduct(String keyword) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, p.product_id, p.product_name, "
                   + "s.available_quantity, s.reorder_level "
                   + "FROM stock s JOIN product p ON s.product_id = p.product_id "
                   + "WHERE p.product_name LIKE ? "
                   + "ORDER BY p.product_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("stock_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("available_quantity"),
                        rs.getInt("reorder_level")
                });
            }
        }
        return list;
    }

    /** Returns items where available_quantity <= reorder_level. */
    public List<Object[]> getLowStock() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, p.product_id, p.product_name, "
                   + "s.available_quantity, s.reorder_level "
                   + "FROM stock s JOIN product p ON s.product_id = p.product_id "
                   + "WHERE s.available_quantity <= s.reorder_level "
                   + "ORDER BY s.available_quantity";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("stock_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("available_quantity"),
                        rs.getInt("reorder_level")
                });
            }
        }
        return list;
    }

    /** Total available quantity across all products. */
    public int getTotalStockQuantity() throws SQLException {
        String sql = "SELECT COALESCE(SUM(available_quantity), 0) FROM stock";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Count of low-stock items. */
    public int getLowStockCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM stock WHERE available_quantity <= reorder_level";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
