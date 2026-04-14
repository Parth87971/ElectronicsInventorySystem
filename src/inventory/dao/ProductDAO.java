package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public void insert(Product p) throws SQLException {
        String sql = "INSERT INTO product (product_name, model_number, category, price, brand_id) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getModelNumber());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getBrandId());
            ps.executeUpdate();
        }
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE product SET product_name=?, model_number=?, category=?, price=?, brand_id=? "
                   + "WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getModelNumber());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getBrandId());
            ps.setInt(6, p.getProductId());
            ps.executeUpdate();
        }
    }

    public void delete(int productId) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public Product getById(int productId) throws SQLException {
        String sql = "SELECT * FROM product WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extract(rs);
            }
        }
        return null;
    }

    /** Returns all products with brand name (joined). */
    public List<Object[]> getAllWithBrand() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.model_number, p.category, "
                   + "p.price, b.brand_name "
                   + "FROM product p JOIN brand b ON p.brand_id = b.brand_id "
                   + "ORDER BY p.product_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("model_number"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("brand_name")
                });
            }
        }
        return list;
    }

    public List<Product> getAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY product_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extract(rs));
            }
        }
        return list;
    }

    public List<Object[]> searchWithBrand(String keyword) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.model_number, p.category, "
                   + "p.price, b.brand_name "
                   + "FROM product p JOIN brand b ON p.brand_id = b.brand_id "
                   + "WHERE p.product_name LIKE ? OR p.model_number LIKE ? "
                   + "OR p.category LIKE ? OR b.brand_name LIKE ? "
                   + "ORDER BY p.product_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            ps.setString(3, pat);
            ps.setString(4, pat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("model_number"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("brand_name")
                });
            }
        }
        return list;
    }

    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM product";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private Product extract(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("model_number"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getInt("brand_id")
        );
    }
}
