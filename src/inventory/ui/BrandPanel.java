package inventory.ui;

import inventory.dao.BrandDAO;
import inventory.model.Brand;
import inventory.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BrandPanel extends JPanel {

    // ---- Shared Design Tokens ----
    static final Color ACCENT        = new Color(37, 99, 235);
    static final Color ACCENT_GREEN  = new Color(22, 163, 74);
    static final Color ACCENT_RED    = new Color(220, 38, 38);
    static final Color ACCENT_ORANGE = new Color(234, 88, 12);
    static final Color ACCENT_TEAL   = new Color(13, 148, 136);
    static final Color HEADER_BG     = new Color(248, 250, 252);
    static final Color BORDER        = new Color(226, 232, 240);
    static final Color BG            = new Color(248, 250, 252);
    static final Color CARD_BG       = Color.WHITE;
    static final Color TEXT_PRIMARY   = new Color(30, 41, 59);
    static final Color TEXT_MUTED     = new Color(100, 116, 139);
    static final Color READONLY_BG   = new Color(241, 245, 249);
    static final String FONT         = "Tahoma";

    private JTextField txtBrandId, txtBrandName, txtCountry, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private final BrandDAO dao = new BrandDAO();

    public BrandPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Brand Management",
                "Register and manage product brands");

        JPanel formAndImage = new JPanel(new BorderLayout(16, 0));
        formAndImage.setBackground(CARD_BG);
        formAndImage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 24, 14, 24)));
        formAndImage.add(buildFormPanel(), BorderLayout.CENTER);

        ImageIcon img = ImageHelper.loadScaled("devices.png", 320, 240);
        if (img != null) {
            JLabel imgLabel = new JLabel(img);
            imgLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            formAndImage.add(imgLabel, BorderLayout.EAST);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(formAndImage, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFormPanel() {
        JPanel card = formCard();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = gbc();

        addField(form, g, 0, 0, "Brand ID", txtBrandId = readOnlyField());
        addField(form, g, 0, 1, "Brand Name *", txtBrandName = field());
        addField(form, g, 0, 2, "Country", txtCountry = field());
        addField(form, g, 0, 3, "Search", txtSearch = field());

        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(
                accentBtn("Add", ACCENT_GREEN, e -> doAdd()),
                accentBtn("Update", ACCENT, e -> doUpdate()),
                accentBtn("Delete", ACCENT_RED, e -> doDelete()),
                accentBtn("Search", ACCENT_ORANGE, e -> doSearch()),
                outlineBtn("Clear", e -> clearForm()),
                outlineBtn("Refresh", e -> loadData())
        ), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols = {"Brand ID", "Brand Name", "Country"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    txtBrandId.setText(val(r, 0));
                    txtBrandName.setText(val(r, 1));
                    txtCountry.setText(val(r, 2));
                }
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    // ---- CRUD ----
    private void doAdd() {
        if (ValidationUtil.isBlank(txtBrandName.getText())) { warn("Brand name is required."); return; }
        try { Brand b = new Brand(); b.setBrandName(txtBrandName.getText().trim());
            b.setCountry(txtCountry.getText().trim()); dao.insert(b);
            info("Brand added successfully."); clearForm(); loadData();
        } catch (Exception ex) { error(ex); }
    }
    private void doUpdate() {
        if (ValidationUtil.isBlank(txtBrandId.getText())) { warn("Select a brand."); return; }
        if (ValidationUtil.isBlank(txtBrandName.getText())) { warn("Brand name is required."); return; }
        try { Brand b = new Brand(); b.setBrandId(Integer.parseInt(txtBrandId.getText().trim()));
            b.setBrandName(txtBrandName.getText().trim()); b.setCountry(txtCountry.getText().trim());
            dao.update(b); info("Brand updated."); clearForm(); loadData();
        } catch (Exception ex) { error(ex); }
    }
    private void doDelete() {
        if (ValidationUtil.isBlank(txtBrandId.getText())) { warn("Select a brand."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this brand?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dao.delete(Integer.parseInt(txtBrandId.getText().trim()));
            info("Brand deleted."); clearForm(); loadData();
        } catch (Exception ex) { error(ex); }
    }
    private void doSearch() {
        String kw = txtSearch.getText().trim(); if (kw.isEmpty()) { loadData(); return; }
        try { List<Brand> list = dao.search(kw); tableModel.setRowCount(0);
            for (Brand b : list) tableModel.addRow(new Object[]{b.getBrandId(), b.getBrandName(), b.getCountry()});
        } catch (Exception ex) { error(ex); }
    }
    private void loadData() {
        try { List<Brand> list = dao.getAll(); tableModel.setRowCount(0);
            for (Brand b : list) tableModel.addRow(new Object[]{b.getBrandId(), b.getBrandName(), b.getCountry()});
        } catch (Exception ex) { error(ex); }
    }
    private void clearForm() {
        txtBrandId.setText(""); txtBrandName.setText(""); txtCountry.setText("");
        txtSearch.setText(""); table.clearSelection();
    }
    private String val(int r, int c) { Object o = tableModel.getValueAt(r, c); return o != null ? o.toString() : ""; }

    // ====================================================================
    //   SHARED UI HELPERS
    // ====================================================================

    /** Module header with title and subtitle. */
    static JPanel moduleHeader(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 24, 12, 24)));

        JLabel t = new JLabel(title);
        t.setFont(new Font(FONT, Font.BOLD, 22));
        t.setForeground(TEXT_PRIMARY);
        p.add(t, BorderLayout.NORTH);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font(FONT, Font.PLAIN, 13));
        s.setForeground(TEXT_MUTED);
        s.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        p.add(s, BorderLayout.CENTER);

        return p;
    }

    static JPanel formCard() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(CARD_BG);
        return p;
    }

    static GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    static void addField(JPanel form, GridBagConstraints g, int col, int row, String labelText, JComponent comp) {
        g.gridx = col; g.gridy = row; g.weightx = 0;
        form.add(label(labelText), g);
        g.gridx = col + 1; g.weightx = 1.0;
        form.add(comp, g);
    }

    static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT, Font.PLAIN, 14));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    static JTextField field() {
        JTextField f = new JTextField(22);
        f.setFont(new Font(FONT, Font.PLAIN, 14));
        return f;
    }

    static JTextField readOnlyField() {
        JTextField f = new JTextField(22);
        f.setFont(new Font(FONT, Font.ITALIC, 14));
        f.setEditable(false);
        f.setBackground(READONLY_BG);
        f.setForeground(TEXT_MUTED);
        f.setToolTipText("Auto-generated (read only)");
        return f;
    }

    /** Colored filled button. */
    static JButton accentBtn(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font(FONT, Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    /** Outlined button. */
    static JButton outlineBtn(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(CARD_BG);
        b.setForeground(TEXT_PRIMARY);
        b.setFont(new Font(FONT, Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 18, 7, 18)));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(new Color(241, 245, 249)); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(CARD_BG); }
        });
        return b;
    }

    /** Horizontal row of buttons. */
    static JPanel buttonRow(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setOpaque(false);
        for (JButton b : buttons) p.add(b);
        return p;
    }

    /** Style a JTable. */
    static void styleTable(JTable t) {
        t.setRowHeight(34);
        t.setFont(new Font(FONT, Font.PLAIN, 14));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(241, 245, 249));
        t.setSelectionBackground(new Color(219, 234, 254));
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);

        t.getTableHeader().setFont(new Font(FONT, Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(241, 245, 249));
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setPreferredSize(new Dimension(0, 38));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    void info(String msg)  { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
    void warn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE); }
    void error(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
}
