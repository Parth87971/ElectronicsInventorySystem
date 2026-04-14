package inventory.service;

import inventory.config.DBConnection;
import inventory.model.Sale;

import java.sql.*;

/**
 * Handles sale transactions with stock validation, automatic stock update,
 * and optional warranty creation.
 * Uses database transactions — commits only when all steps succeed.
 */
public class SaleService {

    /**
     * Adds a new sale, validates stock, updates stock, and optionally creates a warranty.
     *
     * @param sale            Sale details
     * @param createWarranty  true to auto-create a warranty record
     * @param warrantyMonths  warranty duration in months (used only if createWarranty is true)
     * @return a warning message (e.g. low stock alert) or null if none
     */
    public String addSale(Sale sale, boolean createWarranty, int warrantyMonths) throws SQLException {
        Connection conn = null;
        String warning = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Check available stock
            int availableQty;
            int reorderLevel;
            String checkSql = "SELECT available_quantity, reorder_level FROM stock WHERE product_id=?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, sale.getProductId());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("No stock record found for this product. Please purchase stock first.");
                }
                availableQty  = rs.getInt("available_quantity");
                reorderLevel  = rs.getInt("reorder_level");

                
            }

            if (sale.getQuantity() > availableQty) {
                throw new SQLException("Insufficient stock! Available: " + availableQty
                        + ", Requested: " + sale.getQuantity());
            }

            // Step 2: Insert into sale table
            String insertSql = "INSERT INTO sale (sale_date, quantity, selling_price, product_id, customer_id) "
                             + "VALUES (?, ?, ?, ?, ?)";
            int saleId;
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, sale.getSaleDate());
                ps.setInt(2, sale.getQuantity());
                ps.setDouble(3, sale.getSellingPrice());
                ps.setInt(4, sale.getProductId());
                ps.setInt(5, sale.getCustomerId());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                saleId = keys.getInt(1);
            }

            // Step 3: Decrease stock
            String updateSql = "UPDATE stock SET available_quantity = available_quantity - ? WHERE product_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, sale.getQuantity());
                ps.setInt(2, sale.getProductId());
                ps.executeUpdate();
            }

            // Step 4: Create warranty if requested
            if (createWarranty) {
                String wSql = "INSERT INTO warranty (sale_id, warranty_start, warranty_end) "
                            + "VALUES (?, ?, DATE_ADD(?, INTERVAL ? MONTH))";
                try (PreparedStatement ps = conn.prepareStatement(wSql)) {
                    ps.setInt(1, saleId);
                    ps.setDate(2, sale.getSaleDate());
                    ps.setDate(3, sale.getSaleDate());
                    ps.setInt(4, warrantyMonths);
                    ps.executeUpdate();
                }
            }

            conn.commit();

            // Check for low stock warning after commit
            int newQty = availableQty - sale.getQuantity();
            if (newQty <= reorderLevel) {
                warning = "Low Stock Alert! Product stock is now " + newQty
                        + " (reorder level: " + reorderLevel + "). Consider restocking.";
            }

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }

        return warning;
    }

    /**
     * Deletes a sale record and reverses the stock decrease.
     * Warranty linked to this sale is automatically deleted via ON DELETE CASCADE.
     */
    public void deleteSale(int saleId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get sale details
            int productId;
            int quantity;
            String fetchSql = "SELECT product_id, quantity FROM sale WHERE sale_id=?";
            try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
                ps.setInt(1, saleId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Sale not found with ID: " + saleId);
                }
                productId = rs.getInt("product_id");
                quantity  = rs.getInt("quantity");
            }

            // Delete sale (warranty cascades)
            String delSql = "DELETE FROM sale WHERE sale_id=?";
            try (PreparedStatement ps = conn.prepareStatement(delSql)) {
                ps.setInt(1, saleId);
                ps.executeUpdate();
            }

            // Restore stock
            String updateSql = "UPDATE stock SET available_quantity = available_quantity + ? WHERE product_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
