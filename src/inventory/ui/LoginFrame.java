package inventory.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Login screen. Credentials: admin / admin
 */
public class LoginFrame extends JFrame {

    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "admin";

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private int attempts = 0;

    public LoginFrame() {
        setTitle("EIMS - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Top blue header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 118, 210));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLbl = new JLabel("Electronics Inventory Management System");
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titleLbl, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // Center: Login form
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        JLabel loginTitle = new JLabel("Admin Login");
        loginTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        loginTitle.setForeground(new Color(30, 30, 30));
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginTitle);
        form.add(Box.createVerticalStrut(24));

        // Username
        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(userLbl);
        form.add(Box.createVerticalStrut(6));

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(300, 36));
        txtUsername.putClientProperty("JTextField.placeholderText", "Enter username");
        form.add(txtUsername);
        form.add(Box.createVerticalStrut(16));

        // Password
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passLbl);
        form.add(Box.createVerticalStrut(6));

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(300, 36));
        txtPassword.putClientProperty("JTextField.placeholderText", "Enter password");
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(24));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        loginBtn.setBackground(new Color(25, 118, 210));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        loginBtn.addActionListener(e -> doLogin());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(30, 30, 30));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        cancelBtn.addActionListener(e -> System.exit(0));

        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);
        form.add(btnPanel);
        form.add(Box.createVerticalStrut(16));

        JLabel hint = new JLabel("Default: admin / admin");
        hint.setFont(new Font("Tahoma", Font.ITALIC, 11));
        hint.setForeground(new Color(160, 160, 160));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(hint);

        formWrapper.add(form);
        add(formWrapper, BorderLayout.CENTER);

        // Keyboard
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocusInWindow(); }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { txtUsername.requestFocusInWindow(); }
        });
    }

    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (VALID_USER.equals(user) && VALID_PASS.equals(pass)) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        } else {
            attempts++;
            if (attempts >= 3) {
                JOptionPane.showMessageDialog(this, "Too many failed attempts.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials. Attempt " + attempts + " of 3.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }
}
