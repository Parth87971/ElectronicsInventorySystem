package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public void insert(Supplier s) throws SQLException {
        String sql = "INSERT INTO supplier (supplier_name, phone, city, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSupplierName());
            ps.setString(2, s.getPhone());
            ps.setString(3, s.getCity());
            ps.setString(4, s.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(Supplier s) throws SQLException {
        String sql = "UPDATE supplier SET supplier_name=?, phone=?, city=?, email=? WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSupplierName());
            ps.setString(2, s.getPhone());
            ps.setString(3, s.getCity());
            ps.setString(4, s.getEmail());
            ps.setInt(5, s.getSupplierId());
            ps.executeUpdate();
        }
    }

    public void delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM supplier WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.executeUpdate();
        }
    }

    public Supplier getById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM supplier WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extract(rs);
            }
        }
        return null;
    }

    public List<Supplier> getAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY supplier_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extract(rs));
            }
        }
        return list;
    }

    public List<Supplier> search(String keyword) throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier WHERE supplier_name LIKE ? OR phone LIKE ? "
                   + "OR city LIKE ? OR email LIKE ? ORDER BY supplier_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            ps.setString(3, pat);
            ps.setString(4, pat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extract(rs));
            }
        }
        return list;
    }

    private Supplier extract(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("phone"),
                rs.getString("city"),
                rs.getString("email")
        );
    }
}
