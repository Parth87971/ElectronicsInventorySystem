package inventory.ui;

import inventory.dao.ProductDAO;
import inventory.dao.PurchaseDAO;
import inventory.dao.SupplierDAO;
import inventory.model.Product;
import inventory.model.Purchase;
import inventory.model.Supplier;
import inventory.service.PurchaseService;
import inventory.util.ComboItem;
import inventory.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;

import static inventory.ui.BrandPanel.*;

public class PurchasePanel extends JPanel {
    private JTextField txtPurchaseId, txtDate, txtQuantity, txtCostPrice, txtSearch;
    private JComboBox<ComboItem> cmbProduct, cmbSupplier;
    private JTable table;
    private DefaultTableModel tableModel;
    private final PurchaseDAO dao = new PurchaseDAO();
    private final PurchaseService service = new PurchaseService();
    private final ProductDAO productDAO = new ProductDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public PurchasePanel() { setLayout(new BorderLayout()); setBackground(BG); add(buildTopPanel(), BorderLayout.NORTH); add(buildTablePanel(), BorderLayout.CENTER); loadCombos(); loadData(); }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Purchase Management", "Record purchases from suppliers \u2014 stock auto-updates");
        JPanel fai = new JPanel(new BorderLayout(16, 0)); fai.setBackground(CARD_BG);
        fai.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER), BorderFactory.createEmptyBorder(16,24,14,24)));
        fai.add(buildFormPanel(), BorderLayout.CENTER);
        ImageIcon img = ImageHelper.loadScaled("warehouse.png", 300, 300);
        if(img!=null){JLabel il=new JLabel(img);il.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(2,2,2,2)));fai.add(il,BorderLayout.EAST);}
        JPanel w = new JPanel(new BorderLayout()); w.setBackground(CARD_BG); w.add(header, BorderLayout.NORTH); w.add(fai, BorderLayout.CENTER); return w;
    }

    private JPanel buildFormPanel() {
        JPanel card = formCard(); JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints g = gbc();
        addField(form,g,0,0,"Purchase ID",txtPurchaseId=readOnlyField());
        g.gridx=0;g.gridy=1;g.weightx=0;form.add(label("Purchase Date *"),g);g.gridx=1;g.weightx=1;txtDate=field();txtDate.setText(LocalDate.now().toString());txtDate.setToolTipText("yyyy-MM-dd");form.add(txtDate,g);
        g.gridx=0;g.gridy=2;g.weightx=0;form.add(label("Product *"),g);g.gridx=1;g.weightx=1;cmbProduct=new JComboBox<>();cmbProduct.setFont(new Font(FONT,Font.PLAIN,14));form.add(cmbProduct,g);
        g.gridx=0;g.gridy=3;g.weightx=0;form.add(label("Supplier *"),g);g.gridx=1;g.weightx=1;cmbSupplier=new JComboBox<>();cmbSupplier.setFont(new Font(FONT,Font.PLAIN,14));form.add(cmbSupplier,g);
        addField(form,g,0,4,"Quantity *",txtQuantity=field()); addField(form,g,0,5,"Cost Price *",txtCostPrice=field());
        addField(form,g,0,6,"Search",txtSearch=field());
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(accentBtn("Add Purchase",ACCENT_GREEN,e->doAdd()), accentBtn("Delete",ACCENT_RED,e->doDelete()),
                accentBtn("Search",ACCENT_ORANGE,e->doSearch()), outlineBtn("Clear",e->clearForm()),
                outlineBtn("Refresh",e->{loadCombos();loadData();})), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols={"ID","Date","Product","Supplier","Quantity","Cost Price"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(tableModel);styleTable(table);
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){int r=table.getSelectedRow();if(r>=0)txtPurchaseId.setText(val(r,0));}});
        JScrollPane sp=new JScrollPane(table);sp.setBorder(BorderFactory.createEmptyBorder());sp.getViewport().setBackground(Color.WHITE);return sp;
    }

    private void loadCombos(){try{cmbProduct.removeAllItems();for(Product p:productDAO.getAll())cmbProduct.addItem(new ComboItem(p.getProductId(),p.getProductName()));}catch(Exception ex){err(ex);}
        try{cmbSupplier.removeAllItems();for(Supplier s:supplierDAO.getAll())cmbSupplier.addItem(new ComboItem(s.getSupplierId(),s.getSupplierName()));}catch(Exception ex){err(ex);}}
    private void loadData(){try{tableModel.setRowCount(0);for(Object[] row:dao.getAllWithDetails())tableModel.addRow(row);}catch(Exception ex){err(ex);}}
    private void doAdd(){if(!ValidationUtil.isValidDate(txtDate.getText())){w("Valid date required (yyyy-MM-dd).");return;}
        if(!ValidationUtil.isPositiveInt(txtQuantity.getText())){w("Positive quantity required.");return;}
        if(!ValidationUtil.isNonNegativeDouble(txtCostPrice.getText())){w("Valid cost price required.");return;}
        if(cmbProduct.getSelectedItem()==null){w("Select a product.");return;}if(cmbSupplier.getSelectedItem()==null){w("Select a supplier.");return;}
        try{Purchase p=new Purchase();p.setPurchaseDate(Date.valueOf(txtDate.getText().trim()));p.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
            p.setCostPrice(Double.parseDouble(txtCostPrice.getText().trim()));p.setProductId(((ComboItem)cmbProduct.getSelectedItem()).getId());
            p.setSupplierId(((ComboItem)cmbSupplier.getSelectedItem()).getId());service.addPurchase(p);
            JOptionPane.showMessageDialog(this,"Purchase added. Stock updated.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doDelete(){if(ValidationUtil.isBlank(txtPurchaseId.getText())){w("Select a purchase.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try{service.deletePurchase(Integer.parseInt(txtPurchaseId.getText().trim()));JOptionPane.showMessageDialog(this,"Deleted.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doSearch(){String kw=txtSearch.getText().trim();if(kw.isEmpty()){loadData();return;}
        try{tableModel.setRowCount(0);for(Object[] row:dao.searchWithDetails(kw))tableModel.addRow(row);}catch(Exception ex){err(ex);}}
    private void clearForm(){txtPurchaseId.setText("");txtDate.setText(LocalDate.now().toString());txtQuantity.setText("");txtCostPrice.setText("");txtSearch.setText("");
        if(cmbProduct.getItemCount()>0)cmbProduct.setSelectedIndex(0);if(cmbSupplier.getItemCount()>0)cmbSupplier.setSelectedIndex(0);table.clearSelection();}
    private String val(int r,int c){Object o=tableModel.getValueAt(r,c);return o!=null?o.toString():"";}
    private void w(String m){JOptionPane.showMessageDialog(this,m,"Validation",JOptionPane.WARNING_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
