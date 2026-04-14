package inventory.ui;

import inventory.dao.ProductDAO;
import inventory.dao.SaleDAO;
import inventory.dao.StockDAO;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static inventory.ui.BrandPanel.*;

public class DashboardPanel extends JPanel {

    private JLabel lblProducts, lblStock, lblSales, lblLowStock;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 118, 210));
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JLabel titleLbl = new JLabel("ELECTRONICS INVENTORY MANAGEMENT SYSTEM");
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        header.add(titleLbl, BorderLayout.WEST);

        JLabel roleLbl = new JLabel("Admin Dashboard");
        roleLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        roleLbl.setForeground(new Color(200, 220, 255));
        roleLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(roleLbl, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ---- Center ----
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 14, 24));

        // Stats row — all white cards, blue text, simple
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);

        lblProducts = new JLabel("0");
        lblStock    = new JLabel("0");
        lblSales    = new JLabel("0");
        lblLowStock = new JLabel("0");

        statsRow.add(statCard("Total Products",  lblProducts));
        statsRow.add(statCard("Total Stock Qty", lblStock));
        statsRow.add(statCard("Total Sales",     lblSales));
        statsRow.add(statCard("Low Stock Items", lblLowStock));

        centerPanel.add(statsRow, BorderLayout.NORTH);

        // Bottom: Image + Info
        JPanel bottomSplit = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomSplit.setOpaque(false);

        // Image
        JPanel imageCard = new JPanel(new BorderLayout());
        imageCard.setBackground(CARD_BG);
        imageCard.setBorder(BorderFactory.createLineBorder(BORDER));
        ImagePanel imgPanel = new ImagePanel("D:\\ElectronicsInventorySystem\\res\\images\\store.png");
        imageCard.add(imgPanel, BorderLayout.CENTER);
        bottomSplit.add(imageCard);

        // Info
        JPanel infoCard = new JPanel(new BorderLayout(0, 10));
        infoCard.setBackground(CARD_BG);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));

        JLabel welcomeLabel = new JLabel("Welcome to EIMS");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(25, 118, 210));
        infoCard.add(welcomeLabel, BorderLayout.NORTH);

        String infoHtml = "<html><body style='width:100%; font-family:Tahoma; font-size:11px; color:#555;'>"
            + "<p>A complete desktop inventory management solution for mobile and electronics retail shops. "
            + "Built with Java Swing, JDBC, and MySQL.</p>"
            + "<p style='margin-top:12px;'><b style='color:#333;'>Modules:</b></p>"
            + "<table cellpadding='3' style='margin-top:4px;'>"
            + "<tr><td><b>Brand & Product</b></td><td>Register brands, add products</td></tr>"
            + "<tr><td><b>Supplier & Customer</b></td><td>Maintain directories</td></tr>"
            + "<tr><td><b>Purchases</b></td><td>Auto stock update on purchase</td></tr>"
            + "<tr><td><b>Sales & Billing</b></td><td>Auto stock decrement, alerts</td></tr>"
            + "<tr><td><b>Warranty</b></td><td>Auto-generated per sale</td></tr>"
            + "<tr><td><b>Stock Monitor</b></td><td>Real-time levels, reorder alerts</td></tr>"
            + "</table>"
            + "</body></html>";

        JLabel descLabel = new JLabel(infoHtml);
        descLabel.setVerticalAlignment(SwingConstants.TOP);
        infoCard.add(descLabel, BorderLayout.CENTER);
        bottomSplit.add(infoCard);

        centerPanel.add(bottomSplit, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        refreshStats();
    }

    /** Simple white stat card — single color scheme, not rainbow. */
    private JPanel statCard(String title, JLabel valueLbl) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(120, 120, 120));
        card.add(titleLbl, BorderLayout.NORTH);

        valueLbl.setFont(new Font("Tahoma", Font.BOLD, 30));
        valueLbl.setForeground(new Color(25, 118, 210));
        card.add(valueLbl, BorderLayout.CENTER);

        return card;
    }

    public void refreshStats() {
        try { lblProducts.setText(String.valueOf(new ProductDAO().getCount())); } catch (Exception e) { lblProducts.setText("?"); }
        try { lblStock.setText(String.valueOf(new StockDAO().getTotalStockQuantity())); } catch (Exception e) { lblStock.setText("?"); }
        try { lblSales.setText(String.valueOf(new SaleDAO().getTotalSales())); } catch (Exception e) { lblSales.setText("?"); }
        try { lblLowStock.setText(String.valueOf(new StockDAO().getLowStockCount())); } catch (Exception e) { lblLowStock.setText("?"); }
    }

    static class ImagePanel extends JPanel {
        private Image image;
        ImagePanel(String path) {
            try { File f = new File(path); if (f.exists()) image = new ImageIcon(f.getAbsolutePath()).getImage(); }
            catch (Exception ignored) {}
            setBackground(new Color(241, 245, 249));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
