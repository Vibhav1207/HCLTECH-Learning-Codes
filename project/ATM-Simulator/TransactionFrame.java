import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TransactionFrame extends JFrame {
    private final AccountService accountService;
    private final BAcc account;
    private final boolean mini;
    private final JTextField searchField = UIComponents.field();
    private final JComboBox<String> filterBox = new JComboBox<String>();
    private final DefaultTableModel model = new DefaultTableModel(new Object[] { "Transaction ID", "Transaction Type", "Amount", "Description", "Date and Time", "Status" }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
        @Override public Class<?> getColumnClass(int column) { return column == 4 ? LocalDateTime.class : String.class; }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);

    public TransactionFrame(AccountService accountService, BAcc account, boolean mini) {
        super("HDFC | " + (mini ? "Mini statement" : "Transaction history"));
        this.accountService = accountService;
        this.account = account;
        this.mini = mini;
        setSize(950, 590);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(UIComponents.IVORY);
        root.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UIComponents.heading(mini ? "Mini statement" : "Transaction history", 24), BorderLayout.WEST);
        header.add(UIComponents.muted("Account " + account.getAccountNumber()), BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tools.setOpaque(false);
        searchField.setToolTipText("Search transaction ID or description");
        tools.add(new JLabel("Search:")); tools.add(searchField);
        filterBox.setModel(new DefaultComboBoxModel<String>(new String[] { "All types", "DEPOSIT", "WITHDRAWAL", "TRANSFER SENT", "TRANSFER RECEIVED" }));
        tools.add(new JLabel("Type:")); tools.add(filterBox);
        JButton clear = UIComponents.secondaryButton("Clear");
        clear.addActionListener(e -> { searchField.setText(""); filterBox.setSelectedIndex(0); applyFilter(); });
        tools.add(clear);
        root.add(tools, BorderLayout.SOUTH);
        table.setRowSorter(sorter);
        table.setRowHeight(34);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public void setValue(Object value) { setText(value instanceof LocalDateTime ? ((LocalDateTime) value).format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) : ""); }
        });
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {{ setHorizontalAlignment(SwingConstants.RIGHT); }});
        table.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) showSelected(); } });
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener() { @Override public void update() { applyFilter(); } });
        filterBox.addActionListener(e -> applyFilter());
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(root);
    }

    private void refresh() {
        model.setRowCount(0);
        List<Transaction> transactions = accountService.getTransactionsFor(account.getAccountNumber(), mini ? 5 : Integer.MAX_VALUE);
        for (Transaction t : transactions) {
            model.addRow(new Object[] { t.getId(), t.getType(), UIComponents.money(t.getAmount()), t.getDescription(), t.getDateTime(), t.getStatus() });
        }
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        String type = (String) filterBox.getSelectedItem();
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String rowType = String.valueOf(entry.getValue(1));
                String rowText = entry.getStringValue(0) + " " + entry.getStringValue(1) + " " + entry.getStringValue(3);
                return ("All types".equals(type) || rowType.equals(type)) && (text.isEmpty() || rowText.toLowerCase().contains(text.toLowerCase()));
            }
        });
    }

    private void showSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) javax.swing.JOptionPane.showMessageDialog(this, table.getValueAt(row, 3), "Transaction description", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private abstract static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        public abstract void update();
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }
}
