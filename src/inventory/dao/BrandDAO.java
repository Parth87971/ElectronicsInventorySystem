package inventory.dao;

import inventory.config.DBConnection;
import inventory.model.Brand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public void insert(Brand b) throws SQLException {
        String sql = "INSERT INTO brand (brand_name, country) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBrandName());
            ps.setString(2, b.getCountry());
            ps.executeUpdate();
        }
    }

    public void update(Brand b) throws SQLException {
        String sql = "UPDATE brand SET brand_name=?, country=? WHERE brand_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBrandName());
            ps.setString(2, b.getCountry());
            ps.setInt(3, b.getBrandId());
            ps.executeUpdate();
        }
    }

    public void delete(int brandId) throws SQLException {
        String sql = "DELETE FROM brand WHERE brand_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, brandId);
            ps.executeUpdate();
        }
    }

    public Brand getById(int brandId) throws SQLException {
        String sql = "SELECT * FROM brand WHERE brand_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, brandId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extract(rs);
            }
        }
        return null;
    }

    public List<Brand> getAll() throws SQLException {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM brand ORDER BY brand_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extract(rs));
            }
        }
        return list;
    }

    public List<Brand> search(String keyword) throws SQLException {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM brand WHERE brand_name LIKE ? OR country LIKE ? ORDER BY brand_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extract(rs));
            }
        }
        return list;
    }

    private Brand extract(ResultSet rs) throws SQLException {
        return new Brand(
                rs.getInt("brand_id"),
                rs.getString("brand_name"),
                rs.getString("country")
        );
    }
}
