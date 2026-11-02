import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AdminFrame extends JFrame {
    private final AccountService accountService;
    private final JFrame loginFrame;
    private final JLabel summary = UIComponents.heading("", 18);
    private final JTextField search = UIComponents.field();
    private final DefaultTableModel accountsModel = new DefaultTableModel(new Object[] { "Account number", "Customer name", "Type", "Balance", "Status", "Created" }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable accountsTable = new JTable(accountsModel);
    private final DefaultTableModel transactionsModel = new DefaultTableModel(new Object[] { "ID", "Account", "Type", "Amount", "Description", "Date", "Status" }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public AdminFrame(AccountService accountService, JFrame loginFrame) {
        super("HDFC | Administration");
        this.accountService = accountService;
        this.loginFrame = loginFrame;
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(UIComponents.IVORY);
        root.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        header.add(UIComponents.heading("Administration", 26), BorderLayout.WEST);
        JButton logout = UIComponents.secondaryButton("Return to login");
        logout.addActionListener(e -> { dispose(); loginFrame.setVisible(true); });
        header.add(logout, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);
        JPanel overview = new JPanel(new BorderLayout()); overview.setOpaque(false);
        overview.add(summary, BorderLayout.WEST);
        overview.add(UIComponents.muted("Customer PINs and authentication data are never displayed."), BorderLayout.EAST);
        root.add(overview, BorderLayout.SOUTH);
        JTabbedPane tabs = new JTabbedPane();
        JPanel accounts = new JPanel(new BorderLayout(8, 8)); accounts.setBackground(UIComponents.IVORY);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT)); searchBar.setOpaque(false);
        searchBar.add(new JLabel("Search account:")); searchBar.add(search);
        JButton clear = UIComponents.secondaryButton("Clear"); clear.addActionListener(e -> search.setText("")); searchBar.add(clear);
        accounts.add(searchBar, BorderLayout.NORTH);
        accountsTable.setRowSorter(new TableRowSorter<DefaultTableModel>(accountsModel));
        accountsTable.setRowHeight(30);
        search.getDocument().addDocumentListener(new SimpleDocumentListener() { @Override public void update() { filterAccounts(); } });
        accounts.add(new JScrollPane(accountsTable), BorderLayout.CENTER);
        JTable transactionTable = new JTable(transactionsModel); transactionTable.setAutoCreateRowSorter(true); transactionTable.setRowHeight(30);
        tabs.addTab("All accounts", accounts);
        tabs.addTab("All transactions", new JScrollPane(transactionTable));
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void refresh() {
        accountsModel.setRowCount(0);
        List<BAcc> accounts = accountService.getAllAccounts();
        double total = 0;
        for (BAcc a : accounts) {
            total += a.getBalance();
            accountsModel.addRow(new Object[] { a.getAccountNumber(), a.getHolderName(), a.getAccountType(), UIComponents.money(a.getBalance()), a.getStatus(), a.getCreatedAtText().replace('T', ' ') });
        }
        transactionsModel.setRowCount(0);
        for (Transaction t : accountService.getAllTransactions()) transactionsModel.addRow(new Object[] { t.getId(), t.getAccountNumber(), t.getType(), UIComponents.money(t.getAmount()), t.getDescription(), t.getDateTimeText(), t.getStatus() });
        summary.setText(accounts.size() + " registered accounts  •  Total customer balances: " + UIComponents.money(total));
    }

    private void filterAccounts() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) accountsTable.getRowSorter();
        String text = search.getText().trim().toLowerCase();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private abstract static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        public abstract void update();
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }
}
