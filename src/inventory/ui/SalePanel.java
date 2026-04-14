package inventory.ui;

import inventory.dao.CustomerDAO;
import inventory.dao.ProductDAO;
import inventory.dao.SaleDAO;
import inventory.model.Customer;
import inventory.model.Product;
import inventory.model.Sale;
import inventory.service.SaleService;
import inventory.util.ComboItem;
import inventory.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;

import static inventory.ui.BrandPanel.*;

public class SalePanel extends JPanel {
    private JTextField txtSaleId, txtDate, txtQuantity, txtSellingPrice, txtSearch;
    private JComboBox<ComboItem> cmbProduct, cmbCustomer;
    private JCheckBox chkWarranty;
    private JSpinner spnWarrantyMonths;
    private JTable table;
    private DefaultTableModel tableModel;
    private final SaleDAO dao = new SaleDAO();
    private final SaleService service = new SaleService();
    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public SalePanel() { setLayout(new BorderLayout()); setBackground(BG); add(buildTopPanel(), BorderLayout.NORTH); add(buildTablePanel(), BorderLayout.CENTER); loadCombos(); loadData(); }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Sale Management", "Record sales with automatic stock & warranty handling");
        JPanel fai = new JPanel(new BorderLayout(16, 0)); fai.setBackground(CARD_BG);
        fai.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER), BorderFactory.createEmptyBorder(16,24,14,24)));
        fai.add(buildFormPanel(), BorderLayout.CENTER);
        ImageIcon img = ImageHelper.loadScaled("store.png", 300, 320);
        if(img!=null){JLabel il=new JLabel(img);il.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(2,2,2,2)));fai.add(il,BorderLayout.EAST);}
        JPanel w = new JPanel(new BorderLayout()); w.setBackground(CARD_BG); w.add(header, BorderLayout.NORTH); w.add(fai, BorderLayout.CENTER); return w;
    }

    private JPanel buildFormPanel() {
        JPanel card = formCard(); JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints g = gbc();
        addField(form,g,0,0,"Sale ID",txtSaleId=readOnlyField());
        g.gridx=0;g.gridy=1;g.weightx=0;form.add(label("Sale Date *"),g);g.gridx=1;g.weightx=1;txtDate=field();txtDate.setText(LocalDate.now().toString());form.add(txtDate,g);
        g.gridx=0;g.gridy=2;g.weightx=0;form.add(label("Product *"),g);g.gridx=1;g.weightx=1;cmbProduct=new JComboBox<>();cmbProduct.setFont(new Font(FONT,Font.PLAIN,14));form.add(cmbProduct,g);
        g.gridx=0;g.gridy=3;g.weightx=0;form.add(label("Customer *"),g);g.gridx=1;g.weightx=1;cmbCustomer=new JComboBox<>();cmbCustomer.setFont(new Font(FONT,Font.PLAIN,14));form.add(cmbCustomer,g);
        addField(form,g,0,4,"Quantity *",txtQuantity=field()); addField(form,g,0,5,"Selling Price *",txtSellingPrice=field());
        g.gridx=0;g.gridy=6;g.weightx=0;form.add(label("Warranty"),g);g.gridx=1;g.weightx=1;
        JPanel wr=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));wr.setOpaque(false);
        chkWarranty=new JCheckBox("Include");chkWarranty.setFont(new Font(FONT,Font.PLAIN,13));chkWarranty.setOpaque(false);chkWarranty.setSelected(true);
        spnWarrantyMonths=new JSpinner(new SpinnerNumberModel(12,1,60,1));spnWarrantyMonths.setFont(new Font(FONT,Font.PLAIN,13));
        wr.add(chkWarranty);wr.add(new JLabel("Months:"));wr.add(spnWarrantyMonths);form.add(wr,g);
        addField(form,g,0,7,"Search",txtSearch=field());
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(accentBtn("Record Sale",ACCENT_GREEN,e->doAdd()), accentBtn("Delete",ACCENT_RED,e->doDelete()),
                accentBtn("Search",ACCENT_ORANGE,e->doSearch()), outlineBtn("Clear",e->clearForm()),
                outlineBtn("Refresh",e->{loadCombos();loadData();})), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols={"ID","Date","Product","Customer","Quantity","Selling Price"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(tableModel);styleTable(table);
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){int r=table.getSelectedRow();if(r>=0)txtSaleId.setText(val(r,0));}});
        JScrollPane sp=new JScrollPane(table);sp.setBorder(BorderFactory.createEmptyBorder());sp.getViewport().setBackground(Color.WHITE);return sp;
    }

    private void loadCombos(){try{cmbProduct.removeAllItems();for(Product p:productDAO.getAll())cmbProduct.addItem(new ComboItem(p.getProductId(),p.getProductName()));}catch(Exception ex){err(ex);}
        try{cmbCustomer.removeAllItems();for(Customer c:customerDAO.getAll())cmbCustomer.addItem(new ComboItem(c.getCustomerId(),c.getCustomerName()));}catch(Exception ex){err(ex);}}
    private void loadData(){try{tableModel.setRowCount(0);for(Object[] row:dao.getAllWithDetails())tableModel.addRow(row);}catch(Exception ex){err(ex);}}
    private void doAdd(){if(!ValidationUtil.isValidDate(txtDate.getText())){w("Valid date required.");return;}
        if(!ValidationUtil.isPositiveInt(txtQuantity.getText())){w("Positive quantity required.");return;}
        if(!ValidationUtil.isNonNegativeDouble(txtSellingPrice.getText())){w("Valid price required.");return;}
        if(cmbProduct.getSelectedItem()==null){w("Select a product.");return;}if(cmbCustomer.getSelectedItem()==null){w("Select a customer.");return;}
        try{Sale s=new Sale();s.setSaleDate(Date.valueOf(txtDate.getText().trim()));s.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
            s.setSellingPrice(Double.parseDouble(txtSellingPrice.getText().trim()));s.setProductId(((ComboItem)cmbProduct.getSelectedItem()).getId());
            s.setCustomerId(((ComboItem)cmbCustomer.getSelectedItem()).getId());boolean warranty=chkWarranty.isSelected();int months=(int)spnWarrantyMonths.getValue();
            String warning=service.addSale(s,warranty,months);
            JOptionPane.showMessageDialog(this,"Sale recorded."+(warranty?" Warranty: "+months+" months.":""),"Success",JOptionPane.INFORMATION_MESSAGE);
            if(warning!=null)JOptionPane.showMessageDialog(this,warning,"Low Stock",JOptionPane.WARNING_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doDelete(){if(ValidationUtil.isBlank(txtSaleId.getText())){w("Select a sale.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try{service.deleteSale(Integer.parseInt(txtSaleId.getText().trim()));JOptionPane.showMessageDialog(this,"Deleted.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doSearch(){String kw=txtSearch.getText().trim();if(kw.isEmpty()){loadData();return;}
        try{tableModel.setRowCount(0);for(Object[] row:dao.searchWithDetails(kw))tableModel.addRow(row);}catch(Exception ex){err(ex);}}
    private void clearForm(){txtSaleId.setText("");txtDate.setText(LocalDate.now().toString());txtQuantity.setText("");txtSellingPrice.setText("");txtSearch.setText("");
        chkWarranty.setSelected(true);spnWarrantyMonths.setValue(12);if(cmbProduct.getItemCount()>0)cmbProduct.setSelectedIndex(0);if(cmbCustomer.getItemCount()>0)cmbCustomer.setSelectedIndex(0);table.clearSelection();}
    private String val(int r,int c){Object o=tableModel.getValueAt(r,c);return o!=null?o.toString():"";}
    private void w(String m){JOptionPane.showMessageDialog(this,m,"Validation",JOptionPane.WARNING_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
