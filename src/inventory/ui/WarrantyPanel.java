package inventory.ui;

import inventory.dao.WarrantyDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static inventory.ui.BrandPanel.*;

public class WarrantyPanel extends JPanel {
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private final WarrantyDAO dao = new WarrantyDAO();

    public WarrantyPanel() { setLayout(new BorderLayout()); setBackground(BG); add(buildTopPanel(), BorderLayout.NORTH); add(buildTablePanel(), BorderLayout.CENTER); loadData(); }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Warranty Management", "Warranties are auto-created when a sale includes warranty coverage");
        JPanel fai = new JPanel(new BorderLayout(16, 0)); fai.setBackground(CARD_BG);
        fai.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER), BorderFactory.createEmptyBorder(16,24,14,24)));
        fai.add(buildControlPanel(), BorderLayout.CENTER);
        ImageIcon img = ImageHelper.loadScaled("devices.png", 300, 160);
        if(img!=null){JLabel il=new JLabel(img);il.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(2,2,2,2)));fai.add(il,BorderLayout.EAST);}
        JPanel w = new JPanel(new BorderLayout()); w.setBackground(CARD_BG); w.add(header, BorderLayout.NORTH); w.add(fai, BorderLayout.CENTER); return w;
    }

    private JPanel buildControlPanel() {
        JPanel card = formCard(); JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints g = gbc();
        addField(form,g,0,0,"Search (Product / Customer)",txtSearch=field());
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(accentBtn("Search",ACCENT_ORANGE,e->doSearch()), outlineBtn("View All",e->loadData()),
                outlineBtn("Clear",e->{txtSearch.setText("");loadData();})), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols={"Warranty ID","Sale ID","Sale Date","Product","Customer","Warranty Start","Warranty End"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(tableModel);styleTable(table);
        JScrollPane sp=new JScrollPane(table);sp.setBorder(BorderFactory.createEmptyBorder());sp.getViewport().setBackground(Color.WHITE);return sp;
    }

    private void loadData(){try{tableModel.setRowCount(0);for(Object[] row:dao.getAllWithDetails())tableModel.addRow(row);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}}
    private void doSearch(){String kw=txtSearch.getText().trim();if(kw.isEmpty()){loadData();return;}
        try{tableModel.setRowCount(0);for(Object[] row:dao.searchWithDetails(kw))tableModel.addRow(row);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}}
}
