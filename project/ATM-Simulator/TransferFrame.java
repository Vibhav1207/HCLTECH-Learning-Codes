import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TransferFrame extends JFrame {
    private final AccountService accountService;
    private final BAcc sender;
    private final DashboardFrame dashboard;
    private final JTextField receiverField = UIComponents.field();
    private final JTextField amountField = UIComponents.field();
    private final JLabel receiverName = UIComponents.muted("Enter an account number and select Find account.");
    private BAcc receiver;

    public TransferFrame(AccountService accountService, BAcc sender, DashboardFrame dashboard) {
        super("HDFC | Transfer money");
        this.accountService = accountService;
        this.sender = sender;
        this.dashboard = dashboard;
        setSize(590, 500);
        setLocationRelativeTo(dashboard);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(UIComponents.IVORY);
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        root.add(UIComponents.heading("Transfer money", 25), BorderLayout.NORTH);
        UIComponents.RoundedPanel card = UIComponents.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(UIComponents.muted("Send money securely to another HDFC customer."));
        card.add(Box.createVerticalStrut(18));
        JPanel receiverRow = new JPanel(new BorderLayout(8, 0));
        receiverRow.setOpaque(false);
        receiverRow.add(receiverField, BorderLayout.CENTER);
        JButton find = UIComponents.secondaryButton("Find account");
        find.addActionListener(e -> lookupReceiver());
        receiverRow.add(find, BorderLayout.EAST);
        card.add(UIComponents.formRow("RECEIVER ACCOUNT NUMBER", receiverRow));
        card.add(Box.createVerticalStrut(8));
        card.add(receiverName);
        card.add(Box.createVerticalStrut(16));
        card.add(UIComponents.formRow("TRANSFER AMOUNT (₹)", amountField));
        card.add(Box.createVerticalGlue());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton cancel = UIComponents.secondaryButton("Cancel");
        JButton confirm = UIComponents.primaryButton("Confirm transfer");
        cancel.addActionListener(e -> dispose());
        confirm.addActionListener(e -> transfer());
        buttons.add(cancel); buttons.add(confirm);
        card.add(buttons);
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void lookupReceiver() {
        try {
            receiver = accountService.findAccount(receiverField.getText().trim());
            if (receiver.getAccountNumber().equals(sender.getAccountNumber())) {
                receiver = null;
                throw new AccountNotFoundException("You cannot select your own account as receiver.");
            }
            receiverName.setText("Receiver: " + receiver.getHolderName() + "  •  " + receiver.getAccountType());
            receiverName.setForeground(UIComponents.SUCCESS);
        } catch (AccountNotFoundException ex) {
            receiverName.setText(ex.getMessage());
            receiverName.setForeground(UIComponents.ERROR);
        }
    }

    private void transfer() {
        try {
            if (receiver == null) throw new AccountNotFoundException("Find and verify the receiver first.");
            double amount;
            try { amount = Double.parseDouble(amountField.getText().trim()); }
            catch (NumberFormatException ex) { throw new InvalidAmountException("Enter a valid transfer amount."); }
            int answer = JOptionPane.showConfirmDialog(this,
                    "Transfer " + UIComponents.money(amount) + " to " + receiver.getHolderName() + "?",
                    "Confirm transfer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answer != JOptionPane.YES_OPTION) return;
            accountService.transfer(sender.getAccountNumber(), receiver.getAccountNumber(), amount);
            dashboard.refreshHome();
            JOptionPane.showMessageDialog(this, "Transfer completed successfully.", "Transaction complete", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transfer could not be completed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
