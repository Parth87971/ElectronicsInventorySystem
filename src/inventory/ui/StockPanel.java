package inventory.ui;

import inventory.dao.StockDAO;
import inventory.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static inventory.ui.BrandPanel.*;

public class StockPanel extends JPanel {
    private static final Color LOW_STOCK_BG = new Color(254, 226, 226);
    private JTextField txtSearch, txtReorderLevel, txtStockId;
    private JTable table;
    private DefaultTableModel tableModel;
    private final StockDAO dao = new StockDAO();

    public StockPanel() { setLayout(new BorderLayout()); setBackground(BG); add(buildTopPanel(), BorderLayout.NORTH); add(buildTablePanel(), BorderLayout.CENTER); loadData(); }

    private JPanel buildTopPanel() {
        JPanel header = moduleHeader("Stock Management", "Monitor stock levels and reorder alerts");
        JPanel fai = new JPanel(new BorderLayout(16, 0)); fai.setBackground(CARD_BG);
        fai.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER), BorderFactory.createEmptyBorder(16,24,14,24)));
        fai.add(buildControlPanel(), BorderLayout.CENTER);
        ImageIcon img = ImageHelper.loadScaled("warehouse.png", 300, 180);
        if(img!=null){JLabel il=new JLabel(img);il.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(2,2,2,2)));fai.add(il,BorderLayout.EAST);}
        JPanel w = new JPanel(new BorderLayout()); w.setBackground(CARD_BG); w.add(header, BorderLayout.NORTH); w.add(fai, BorderLayout.CENTER); return w;
    }

    private JPanel buildControlPanel() {
        JPanel card = formCard(); JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints g = gbc();
        addField(form,g,0,0,"Stock ID",txtStockId=readOnlyField()); addField(form,g,0,1,"Reorder Level",txtReorderLevel=field());
        addField(form,g,0,2,"Search Product",txtSearch=field());
        g.gridx=0;g.gridy=3;g.gridwidth=2;g.weightx=1;JLabel info=new JLabel("Stock is auto-managed by Purchases & Sales.");info.setFont(new Font(FONT,Font.ITALIC,12));info.setForeground(TEXT_MUTED);form.add(info,g);g.gridwidth=1;
        card.add(form, BorderLayout.CENTER);
        card.add(buttonRow(accentBtn("Search",ACCENT_ORANGE,e->doSearch()), accentBtn("Low Stock Only",ACCENT_RED,e->doLowStock()),
                accentBtn("Update Reorder",ACCENT,e->doUpdateReorder()), outlineBtn("View All",e->loadData()),
                outlineBtn("Clear",e->clearForm())), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTablePanel() {
        String[] cols={"Stock ID","Product ID","Product Name","Available Qty","Reorder Level"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(tableModel);styleTable(table);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable tbl,Object val,boolean sel,boolean focus,int row,int col){
                Component c=super.getTableCellRendererComponent(tbl,val,sel,focus,row,col);
                if(!sel){try{int qty=Integer.parseInt(tableModel.getValueAt(row,3).toString());int reorder=Integer.parseInt(tableModel.getValueAt(row,4).toString());
                    c.setBackground(qty<=reorder?LOW_STOCK_BG:(row%2==0?Color.WHITE:new Color(248,250,252)));}catch(Exception e){c.setBackground(row%2==0?Color.WHITE:new Color(248,250,252));}}
                setBorder(BorderFactory.createEmptyBorder(0,12,0,12));return c;}});
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){int r=table.getSelectedRow();if(r>=0){txtStockId.setText(val(r,0));txtReorderLevel.setText(val(r,4));}}});
        JScrollPane sp=new JScrollPane(table);sp.setBorder(BorderFactory.createEmptyBorder());sp.getViewport().setBackground(Color.WHITE);return sp;
    }

    private void loadData(){try{tableModel.setRowCount(0);for(Object[] row:dao.getAllWithProduct())tableModel.addRow(row);}catch(Exception ex){err(ex);}}
    private void doSearch(){String kw=txtSearch.getText().trim();if(kw.isEmpty()){loadData();return;}try{tableModel.setRowCount(0);for(Object[] row:dao.searchByProduct(kw))tableModel.addRow(row);}catch(Exception ex){err(ex);}}
    private void doLowStock(){try{List<Object[]>list=dao.getLowStock();tableModel.setRowCount(0);for(Object[] row:list)tableModel.addRow(row);
        if(list.isEmpty())JOptionPane.showMessageDialog(this,"No low-stock items.","Info",JOptionPane.INFORMATION_MESSAGE);}catch(Exception ex){err(ex);}}
    private void doUpdateReorder(){if(ValidationUtil.isBlank(txtStockId.getText())){w("Select a stock row.");return;}String rl=txtReorderLevel.getText().trim();
        if(!ValidationUtil.isPositiveInt(rl)&&!"0".equals(rl)){w("Valid reorder level required.");return;}
        try{dao.updateReorderLevel(Integer.parseInt(txtStockId.getText().trim()),Integer.parseInt(rl));JOptionPane.showMessageDialog(this,"Reorder level updated.","Success",JOptionPane.INFORMATION_MESSAGE);clearForm();loadData();}catch(Exception ex){err(ex);}}
    private void clearForm(){txtStockId.setText("");txtReorderLevel.setText("");txtSearch.setText("");table.clearSelection();}
    private String val(int r,int c){Object o=tableModel.getValueAt(r,c);return o!=null?o.toString():"";}
    private void w(String m){JOptionPane.showMessageDialog(this,m,"Validation",JOptionPane.WARNING_MESSAGE);}
    private void err(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
