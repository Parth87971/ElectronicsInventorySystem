package inventory.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private static final Color SIDEBAR_BG    = new Color(15, 23, 42);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton activeButton = null;

    public MainFrame() {
        setTitle("Electronics Inventory Management System");
        setSize(1320, 780);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 250, 252));

        contentPanel.add(new DashboardPanel(), "Dashboard");
        contentPanel.add(new BrandPanel(),     "Brands");
        contentPanel.add(new ProductPanel(),   "Products");
        contentPanel.add(new SupplierPanel(),  "Suppliers");
        contentPanel.add(new CustomerPanel(),  "Customers");
        contentPanel.add(new PurchasePanel(),  "Purchases");
        contentPanel.add(new SalePanel(),      "Sales");
        contentPanel.add(new StockPanel(),     "Stock");
        contentPanel.add(new WarrantyPanel(),  "Warranty");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(SIDEBAR_BG);

        // App title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(200, 70));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));

        JLabel appName = new JLabel("EIMS");
        appName.setFont(new Font("Tahoma", Font.BOLD, 20));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(appName);

        JLabel appSub = new JLabel("Inventory Manager");
        appSub.setFont(new Font("Tahoma", Font.PLAIN, 11));
        appSub.setForeground(new Color(130, 140, 160));
        appSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(appSub);

        sidebar.add(titlePanel);

        // Line separator
        JPanel sep = new JPanel();
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 60, 80)));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        // Nav items — plain text, no icons
        String[] items = {"Dashboard", "Brands", "Products", "Suppliers",
                "Customers", "Purchases", "Sales", "Stock", "Warranty"};

        for (String item : items) {
            sidebar.add(createNavButton(item, item));
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        // Exit
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setBackground(new Color(127, 29, 29));
        exitBtn.setMaximumSize(new Dimension(200, 40));
        exitBtn.setMinimumSize(new Dimension(200, 40));
        exitBtn.setPreferredSize(new Dimension(200, 40));
        exitBtn.setHorizontalAlignment(SwingConstants.CENTER);
        exitBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        exitBtn.setFocusPainted(false);
        exitBtn.setContentAreaFilled(true);
        exitBtn.setOpaque(true);
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.exit(0);
        });
        exitBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { exitBtn.setBackground(new Color(153, 27, 27)); }
            @Override public void mouseExited(MouseEvent e)  { exitBtn.setBackground(new Color(127, 29, 29)); }
        });
        sidebar.add(exitBtn);
        sidebar.add(Box.createVerticalStrut(12));

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setForeground(new Color(180, 190, 210));
        btn.setBackground(SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(200, 38));
        btn.setMinimumSize(new Dimension(200, 38));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if (activeButton != null) {
                activeButton.setBackground(SIDEBAR_BG);
                activeButton.setForeground(new Color(180, 190, 210));
            }
            btn.setBackground(SIDEBAR_ACTIVE);
            btn.setForeground(Color.WHITE);
            activeButton = btn;

            if ("Dashboard".equals(cardName)) {
                for (Component c : contentPanel.getComponents())
                    if (c instanceof DashboardPanel) ((DashboardPanel) c).refreshStats();
            }
        });

        return btn;
    }
}
