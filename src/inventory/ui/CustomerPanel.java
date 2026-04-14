package inventory.ui;

import inventory.dao.CustomerDAO;
import inventory.model.Customer;
import inventory.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import static inventory.ui.BrandPanel.*;

public class CustomerPanel extends JPanel {
    private JTextField txtId, txtName, txtPhone, txtEmail, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private final CustomerDAO dao = new CustomerDAO();

    public CustomerPanel() { setLayout(new BorderLayout()); setBackground(BG); add(buildTopPanel(), BorderLayout.NORTH); add(buildTablePanel(), BorderLayout.CENTER); loadData(); }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Customer Management", "Track customer records");
        JPanel fai = new JPanel(new BorderLayout(16, 0)); fai.setBackground(CARD_BG);
        fai.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER), BorderFactory.createEmptyBorder(16,24,14,24)));
        fai.add(buildFormPanel(), BorderLayout.CENTER);
        ImageIcon img = ImageHelper.loadScaled("store.png", 300, 220);
        if(img!=null){JLabel il=new JLabel(img);il.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(2,2,2,2)));fai.add(il,BorderLayout.EAST);}
        JPanel w = new JPanel(new BorderLayout()); w.setBackground(CARD_BG); w.add(header, BorderLayout.NORTH); w.add(fai, BorderLayout.CENTER); return w;
    }

    private JPanel buildFormPanel() {
        JPanel card = formCard(); JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints g = gbc();
        addField(form,g,0,0,"Customer ID",txtId=readOnlyField()); addField(form,g,0,1,"Customer Name *",txtName=field());
        addField(form,g,0,2,"Phone",txtPhone=field()); addField(form,g,0,3,"Email",txtEmail=field());
        addField(form,g,0,4,"Search",txtSearch=field());
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(accentBtn("Add",ACCENT_GREEN,e->doAdd()), accentBtn("Update",ACCENT,e->doUpdate()),
                accentBtn("Delete",ACCENT_RED,e->doDelete()), accentBtn("Search",ACCENT_ORANGE,e->doSearch()),
                outlineBtn("Clear",e->clearForm()), outlineBtn("Refresh",e->loadData())), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols={"ID","Customer Name","Phone","Email"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(tableModel); styleTable(table);
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){int r=table.getSelectedRow();if(r<0)return;
            txtId.setText(val(r,0));txtName.setText(val(r,1));txtPhone.setText(val(r,2));txtEmail.setText(val(r,3));}});
        JScrollPane sp=new JScrollPane(table);sp.setBorder(BorderFactory.createEmptyBorder());sp.getViewport().setBackground(Color.WHITE);return sp;
    }

    private void doAdd(){if(ValidationUtil.isBlank(txtName.getText())){w("Name required.");return;}
        if(!txtPhone.getText().trim().isEmpty()&&!ValidationUtil.isValidPhone(txtPhone.getText())){w("Invalid phone.");return;}
        if(!txtEmail.getText().trim().isEmpty()&&!ValidationUtil.isValidEmail(txtEmail.getText())){w("Invalid email.");return;}
        try{Customer c=new Customer();c.setCustomerName(txtName.getText().trim());c.setPhone(txtPhone.getText().trim());c.setEmail(txtEmail.getText().trim());dao.insert(c);
            JOptionPane.showMessageDialog(this,"Customer added.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doUpdate(){if(ValidationUtil.isBlank(txtId.getText())){w("Select a customer.");return;}if(ValidationUtil.isBlank(txtName.getText())){w("Name required.");return;}
        try{Customer c=new Customer();c.setCustomerId(Integer.parseInt(txtId.getText().trim()));c.setCustomerName(txtName.getText().trim());c.setPhone(txtPhone.getText().trim());c.setEmail(txtEmail.getText().trim());dao.update(c);
            JOptionPane.showMessageDialog(this,"Customer updated.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doDelete(){if(ValidationUtil.isBlank(txtId.getText())){w("Select a customer.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try{dao.delete(Integer.parseInt(txtId.getText().trim()));JOptionPane.showMessageDialog(this,"Deleted.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void doSearch(){String kw=txtSearch.getText().trim();if(kw.isEmpty()){loadData();return;}
        try{tableModel.setRowCount(0);for(Customer c:dao.search(kw))tableModel.addRow(new Object[]{c.getCustomerId(),c.getCustomerName(),c.getPhone(),c.getEmail()});}catch(Exception ex){err(ex);}}
    private void loadData(){try{tableModel.setRowCount(0);for(Customer c:dao.getAll())tableModel.addRow(new Object[]{c.getCustomerId(),c.getCustomerName(),c.getPhone(),c.getEmail()});}catch(Exception ex){err(ex);}}
    private void clearForm(){txtId.setText("");txtName.setText("");txtPhone.setText("");txtEmail.setText("");txtSearch.setText("");table.clearSelection();}
    private String val(int r,int c){Object o=tableModel.getValueAt(r,c);return o!=null?o.toString():"";}
    private void w(String m){JOptionPane.showMessageDialog(this,m,"Validation",JOptionPane.WARNING_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
