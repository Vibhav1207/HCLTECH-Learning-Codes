import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class DashboardFrame extends JFrame {
    private final AccountService accountService;
    private final BAcc account;
    private final JFrame loginFrame;
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel balanceLabel = UIComponents.heading("₹0.00", 30);
    private final JLabel accountStatus = UIComponents.muted("");
    private final JLabel withdrawAvailableLabel = UIComponents.muted("");
    private final JLabel headerCustomer = UIComponents.label("");
    private final JTextField depositAmount = UIComponents.field();
    private final JTextField withdrawAmount = UIComponents.field();
    private final JPasswordField oldPin = new JPasswordField();
    private final JPasswordField newPin = new JPasswordField();
    private final JPasswordField confirmPin = new JPasswordField();

    public DashboardFrame(AccountService accountService, BAcc account, JFrame loginFrame) {
        super("HDFC | Dashboard");
        this.accountService = accountService;
        this.account = account;
        this.loginFrame = loginFrame;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { logout(); } });
        buildUI();
        refreshHome();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UIComponents.IVORY);
        root.setBorder(BorderFactory.createEmptyBorder(18, 28, 24, 28));
        root.add(buildTopBar(), BorderLayout.NORTH);
        content.setOpaque(false);
        content.add(buildHome(), "HOME");
        content.add(buildDeposit(), "DEPOSIT");
        content.add(buildWithdraw(), "WITHDRAW");
        content.add(buildDetails(), "DETAILS");
        content.add(buildChangePin(), "PIN");
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        brand.add(UIComponents.heading("HDFC", 20));
        brand.add(Box.createHorizontalStrut(15));
        headerCustomer.setText(account.getHolderName() + "  •  " + account.getAccountNumber());
        headerCustomer.setForeground(UIComponents.MUTED);
        brand.add(headerCustomer);
        JButton logout = UIComponents.secondaryButton("Log out");
        logout.addActionListener(e -> logout());
        bar.add(brand, BorderLayout.WEST);
        bar.add(logout, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildHome() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);
        JPanel intro = new JPanel(new BorderLayout());
        intro.setOpaque(false);
        intro.add(UIComponents.heading("Good to see you, " + account.getHolderName().split(" ")[0], 25), BorderLayout.WEST);
        intro.add(UIComponents.muted("Manage your money with confidence."), BorderLayout.EAST);
        page.add(intro, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(18, 18));
        body.setOpaque(false);
        UIComponents.RoundedPanel balance = UIComponents.card();
        balance.setPreferredSize(new Dimension(310, 190));
        balance.setLayout(new BoxLayout(balance, BoxLayout.Y_AXIS));
        JLabel available = new JLabel("AVAILABLE BALANCE");
        available.setForeground(Color.BLACK);
        available.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        balance.add(available);
        balance.add(Box.createVerticalStrut(14));
        balanceLabel.setForeground(Color.BLACK);
        balance.add(balanceLabel);
        balance.add(Box.createVerticalGlue());
        accountStatus.setForeground(Color.BLACK);
        balance.add(accountStatus);
        body.add(balance, BorderLayout.WEST);
        UIComponents.RoundedPanel actions = UIComponents.card();
        actions.setLayout(new BorderLayout(0, 12));
        actions.add(UIComponents.heading("Quick actions", 18), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 4, 12, 12));
        grid.setOpaque(false);
        grid.add(actionCard("Check balance", "View available funds", e -> checkBalance()));
        grid.add(actionCard("Deposit", "Add money", e -> showCard("DEPOSIT")));
        grid.add(actionCard("Withdraw", "Take out cash", e -> showCard("WITHDRAW")));
        grid.add(actionCard("Transfer", "Send money", e -> new TransferFrame(accountService, account, this).setVisible(true)));
        grid.add(actionCard("Mini statement", "Latest five entries", e -> new TransactionFrame(accountService, account, true).setVisible(true)));
        grid.add(actionCard("Transaction history", "Search all activity", e -> new TransactionFrame(accountService, account, false).setVisible(true)));
        grid.add(actionCard("Account details", "Review profile", e -> showCard("DETAILS")));
        grid.add(actionCard("Change PIN", "Update security", e -> showCard("PIN")));
        actions.add(grid, BorderLayout.CENTER);
        body.add(actions, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private UIComponents.RoundedPanel actionCard(String title, String subtitle, java.awt.event.ActionListener listener) {
        UIComponents.RoundedPanel card = UIComponents.card();
        card.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
        card.setLayout(new BorderLayout(4, 7));
        JLabel titleLabel = UIComponents.heading(title, 14);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(UIComponents.muted(subtitle), BorderLayout.CENTER);
        JButton open = UIComponents.secondaryButton("Open");
        open.setMargin(new Insets(3, 8, 3, 8));
        open.addActionListener(listener);
        card.add(open, BorderLayout.SOUTH);
        card.addMouseListener(UIComponents.cardHoverListener());
        return card;
    }

    private JPanel buildDeposit() {
        JPanel page = operationPage("Deposit funds", "Add money to your account.", "DEPOSIT");
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(UIComponents.formRow("AMOUNT (₹)", depositAmount));
        form.add(Box.createVerticalStrut(16));
        form.add(UIComponents.muted("Quick amounts"));
        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        quick.setOpaque(false);
        for (String value : new String[] { "500", "1000", "2000", "5000" }) {
            JButton b = UIComponents.goldButton("₹" + value);
            b.addActionListener(e -> depositAmount.setText(value));
            quick.add(b);
        }
        form.add(quick);
        page.add(form, BorderLayout.CENTER);
        JButton submit = UIComponents.primaryButton("Confirm deposit");
        submit.addActionListener(e -> deposit());
        addBottom(page, submit);
        return page;
    }

    private JPanel buildWithdraw() {
        JPanel page = operationPage("Withdraw funds", "Take out money within your account rules.", "WITHDRAW");
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        withdrawAvailableLabel.setText("Available now: " + UIComponents.money(account.getBalance()));
        form.add(withdrawAvailableLabel);
        form.add(Box.createVerticalStrut(18));
        form.add(UIComponents.formRow("AMOUNT (₹)", withdrawAmount));
        form.add(Box.createVerticalStrut(16));
        form.add(UIComponents.muted(account.getRulesSummary()));
        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        quick.setOpaque(false);
        for (String value : new String[] { "500", "1000", "2000", "5000" }) {
            JButton b = UIComponents.goldButton("₹" + value);
            b.addActionListener(e -> withdrawAmount.setText(value));
            quick.add(b);
        }
        form.add(quick);
        page.add(form, BorderLayout.CENTER);
        JButton submit = UIComponents.primaryButton("Confirm withdrawal");
        submit.addActionListener(e -> withdraw());
        addBottom(page, submit);
        return page;
    }

    private JPanel operationPage(String title, String subtitle, String cardName) {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIComponents.heading(title, 24), BorderLayout.WEST);
        top.add(UIComponents.muted(subtitle), BorderLayout.SOUTH);
        page.add(top, BorderLayout.NORTH);
        return page;
    }

    private void addBottom(JPanel page, JButton submit) {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);
        JButton cancel = UIComponents.secondaryButton("Cancel");
        cancel.addActionListener(e -> showCard("HOME"));
        bottom.add(cancel);
        bottom.add(submit);
        page.add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildDetails() {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        UIComponents.RoundedPanel card = UIComponents.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(UIComponents.heading("Account details", 24));
        card.add(Box.createVerticalStrut(22));
        card.add(detailRow("Account holder", account.getHolderName()));
        card.add(detailRow("Account number", account.getAccountNumber()));
        card.add(detailRow("Account type", account.getAccountType()));
        card.add(detailRow("Current balance", UIComponents.money(account.getBalance())));
        card.add(detailRow("Account status", account.getStatus()));
        card.add(detailRow("Created", account.getCreatedAtText().replace('T', ' ')));
        card.add(detailRow("Product rules", account.getRulesSummary()));
        card.add(Box.createVerticalGlue());
        card.add(UIComponents.muted("For your protection, your PIN is never shown here."));
        page.add(card, BorderLayout.CENTER);
        addBack(page);
        return page;
    }

    private JPanel detailRow(String key, String value) {
        JPanel row = new JPanel(new BorderLayout(20, 5));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(9, 0, 9, 0));
        row.add(UIComponents.muted(key), BorderLayout.WEST);
        row.add(UIComponents.label(value), BorderLayout.CENTER);
        return row;
    }

    private JPanel buildChangePin() {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        UIComponents.RoundedPanel card = UIComponents.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(UIComponents.heading("Change PIN", 24));
        card.add(Box.createVerticalStrut(8));
        card.add(UIComponents.muted("Choose a new four-digit PIN. Never share it with anyone."));
        card.add(Box.createVerticalStrut(24));
        configurePin(oldPin); configurePin(newPin); configurePin(confirmPin);
        card.add(UIComponents.formRow("CURRENT PIN", oldPin));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("NEW PIN", newPin));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("CONFIRM NEW PIN", confirmPin));
        JButton save = UIComponents.primaryButton("Update PIN");
        save.setAlignmentX(LEFT_ALIGNMENT);
        save.addActionListener(e -> changePin());
        card.add(Box.createVerticalStrut(20));
        card.add(save);
        page.add(card, BorderLayout.CENTER);
        addBack(page);
        return page;
    }

    private void configurePin(JPasswordField field) {
        field.setEchoChar('\u2022');
        field.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(240, 42));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(214, 218, 224)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
    }

    private void addBack(JPanel page) {
        JButton back = UIComponents.secondaryButton("Back to dashboard");
        back.addActionListener(e -> showCard("HOME"));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false); bottom.add(back); page.add(bottom, BorderLayout.SOUTH);
    }

    private void deposit() {
        try {
            accountService.deposit(account.getAccountNumber(), parseAmount(depositAmount.getText()));
            depositAmount.setText(""); refreshHome(); showCard("HOME");
            JOptionPane.showMessageDialog(this, "Deposit completed successfully.", "Transaction complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError(ex); }
    }

    private void withdraw() {
        try {
            accountService.withdraw(account.getAccountNumber(), parseAmount(withdrawAmount.getText()));
            withdrawAmount.setText(""); refreshHome(); showCard("HOME");
            JOptionPane.showMessageDialog(this, "Withdrawal completed successfully.", "Transaction complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError(ex); }
    }

    private void changePin() {
        try {
            String next = new String(newPin.getPassword());
            if (!next.equals(new String(confirmPin.getPassword()))) throw new InvalidPinException("New PINs do not match.");
            accountService.changePin(account.getAccountNumber(), new String(oldPin.getPassword()), next);
            oldPin.setText(""); newPin.setText(""); confirmPin.setText(""); showCard("HOME");
            JOptionPane.showMessageDialog(this, "PIN changed successfully.", "Security updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { showError(ex); }
    }

    private double parseAmount(String text) throws InvalidAmountException {
        try { return Double.parseDouble(text.trim()); }
        catch (NumberFormatException ex) { throw new InvalidAmountException("Enter a valid amount."); }
    }

    private void showError(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Action could not be completed", JOptionPane.WARNING_MESSAGE); }

    private void checkBalance() { JOptionPane.showMessageDialog(this, "Available balance\n" + UIComponents.money(account.getBalance()), "Balance", JOptionPane.INFORMATION_MESSAGE); }

    public void refreshHome() {
        balanceLabel.setText(UIComponents.money(account.getBalance()));
        accountStatus.setText(account.getAccountType() + "  •  " + account.getStatus());
        withdrawAvailableLabel.setText("Available now: " + UIComponents.money(account.getBalance()));
        content.revalidate(); content.repaint();
    }

    public void showCard(String name) { refreshHome(); cards.show(content, name); }

    private void logout() {
        dispose();
        loginFrame.setVisible(true);
    }
}
