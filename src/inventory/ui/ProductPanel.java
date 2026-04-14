package inventory.ui;

import inventory.dao.BrandDAO;
import inventory.dao.ProductDAO;
import inventory.model.Brand;
import inventory.model.Product;
import inventory.util.ComboItem;
import inventory.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import static inventory.ui.BrandPanel.*;

public class ProductPanel extends JPanel {
    private JTextField txtProductId, txtProductName, txtModel, txtCategory, txtPrice, txtSearch;
    private JComboBox<ComboItem> cmbBrand;
    private JTable table;
    private DefaultTableModel tableModel;
    private final ProductDAO productDAO = new ProductDAO();
    private final BrandDAO brandDAO = new BrandDAO();

    public ProductPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        loadBrands(); loadData();
    }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Product Management", "Add products with brand associations and pricing");
        JPanel formAndImage = new JPanel(new BorderLayout(16, 0));
        formAndImage.setBackground(CARD_BG);
        formAndImage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 24, 14, 24)));
        formAndImage.add(buildFormPanel(), BorderLayout.CENTER);
        ImageIcon img = ImageHelper.loadScaled("devices.png", 300, 270);
        if (img != null) { JLabel il = new JLabel(img); il.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(2,2,2,2))); formAndImage.add(il, BorderLayout.EAST); }
        JPanel w = new JPanel(new BorderLayout()); w.setBackground(CARD_BG);
        w.add(header, BorderLayout.NORTH); w.add(formAndImage, BorderLayout.CENTER); return w;
    }

    private JPanel buildFormPanel() {
        JPanel card = formCard(); JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints g = gbc();
        addField(form, g, 0, 0, "Product ID", txtProductId = readOnlyField());
        addField(form, g, 0, 1, "Product Name *", txtProductName = field());
        addField(form, g, 0, 2, "Model Number", txtModel = field());
        addField(form, g, 0, 3, "Category", txtCategory = field());
        addField(form, g, 0, 4, "Price *", txtPrice = field());
        g.gridx=0; g.gridy=5; g.weightx=0; form.add(label("Brand *"), g);
        g.gridx=1; g.weightx=1; cmbBrand = new JComboBox<>(); cmbBrand.setFont(new Font(FONT,Font.PLAIN,14)); form.add(cmbBrand, g);
        addField(form, g, 0, 6, "Search", txtSearch = field());
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(accentBtn("Add",ACCENT_GREEN,e->doAdd()), accentBtn("Update",ACCENT,e->doUpdate()),
                accentBtn("Delete",ACCENT_RED,e->doDelete()), accentBtn("Search",ACCENT_ORANGE,e->doSearch()),
                outlineBtn("Clear",e->clearForm()), outlineBtn("Refresh",e->{loadBrands();loadData();})), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols = {"ID","Product Name","Model","Category","Price","Brand"};
        tableModel = new DefaultTableModel(cols,0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel); styleTable(table);
        table.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) {
            int r = table.getSelectedRow(); if(r<0) return;
            txtProductId.setText(val(r,0)); txtProductName.setText(val(r,1)); txtModel.setText(val(r,2));
            txtCategory.setText(val(r,3)); txtPrice.setText(val(r,4));
            String bn = val(r,5); for(int i=0;i<cmbBrand.getItemCount();i++) if(cmbBrand.getItemAt(i).getLabel().equals(bn)){cmbBrand.setSelectedIndex(i);break;} }});
        JScrollPane sp = new JScrollPane(table); sp.setBorder(BorderFactory.createEmptyBorder()); sp.getViewport().setBackground(Color.WHITE); return sp;
    }

    private void loadBrands() { try{cmbBrand.removeAllItems(); for(Brand b:brandDAO.getAll())cmbBrand.addItem(new ComboItem(b.getBrandId(),b.getBrandName()));}catch(Exception ex){err(ex);} }
    private void loadData() { try{tableModel.setRowCount(0); for(Object[] row:productDAO.getAllWithBrand())tableModel.addRow(row);}catch(Exception ex){err(ex);} }
    private void doAdd() {
        if(ValidationUtil.isBlank(txtProductName.getText())){w("Product name required.");return;}
        if(!ValidationUtil.isNonNegativeDouble(txtPrice.getText())){w("Valid price required.");return;}
        if(cmbBrand.getSelectedItem()==null){w("Select a brand.");return;}
        try{Product p=new Product();p.setProductName(txtProductName.getText().trim());p.setModelNumber(txtModel.getText().trim());
            p.setCategory(txtCategory.getText().trim());p.setPrice(Double.parseDouble(txtPrice.getText().trim()));
            p.setBrandId(((ComboItem)cmbBrand.getSelectedItem()).getId());productDAO.insert(p);
            JOptionPane.showMessageDialog(this,"Product added.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}
    }
    private void doUpdate() {
        if(ValidationUtil.isBlank(txtProductId.getText())){w("Select a product.");return;}
        if(ValidationUtil.isBlank(txtProductName.getText())){w("Product name required.");return;}
        if(!ValidationUtil.isNonNegativeDouble(txtPrice.getText())){w("Valid price required.");return;}
        try{Product p=new Product();p.setProductId(Integer.parseInt(txtProductId.getText().trim()));
            p.setProductName(txtProductName.getText().trim());p.setModelNumber(txtModel.getText().trim());
            p.setCategory(txtCategory.getText().trim());p.setPrice(Double.parseDouble(txtPrice.getText().trim()));
            p.setBrandId(((ComboItem)cmbBrand.getSelectedItem()).getId());productDAO.update(p);
            JOptionPane.showMessageDialog(this,"Product updated.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}
    }
    private void doDelete() {
        if(ValidationUtil.isBlank(txtProductId.getText())){w("Select a product.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try{productDAO.delete(Integer.parseInt(txtProductId.getText().trim()));
            JOptionPane.showMessageDialog(this,"Product deleted.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}
    }
    private void doSearch() { String kw=txtSearch.getText().trim(); if(kw.isEmpty()){loadData();return;}
        try{tableModel.setRowCount(0);for(Object[] row:productDAO.searchWithBrand(kw))tableModel.addRow(row);}catch(Exception ex){err(ex);} }
    private void clearForm() { txtProductId.setText("");txtProductName.setText("");txtModel.setText("");txtCategory.setText("");txtPrice.setText("");txtSearch.setText("");if(cmbBrand.getItemCount()>0)cmbBrand.setSelectedIndex(0);table.clearSelection(); }
    private String val(int r,int c){Object o=tableModel.getValueAt(r,c);return o!=null?o.toString():"";}
    private void w(String m){JOptionPane.showMessageDialog(this,m,"Validation",JOptionPane.WARNING_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
