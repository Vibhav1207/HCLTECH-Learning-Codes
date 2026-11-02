import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RegisterFrame extends JFrame {
    private final AccountService accountService;
    private final JFrame loginFrame;
    private final JTextField nameField = UIComponents.field();
    private final JPasswordField pinField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();
    private final JTextField depositField = UIComponents.field();
    private final JComboBox<String> typeBox = new JComboBox<String>(new String[] { "SAVINGS", "CURRENT" });

    public RegisterFrame(AccountService accountService, JFrame loginFrame) {
        super("HDFC | Create Account");
        this.accountService = accountService;
        this.loginFrame = loginFrame;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 700);
        setLocationRelativeTo(loginFrame);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(UIComponents.IVORY);
        root.setBorder(BorderFactory.createEmptyBorder(30, 55, 30, 55));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        header.add(UIComponents.heading("Open your HDFC account", 23));
        root.add(header, BorderLayout.NORTH);
        UIComponents.RoundedPanel card = UIComponents.card();
        card.setLayout(new GridLayout(6, 1, 10, 10));
        configurePassword(pinField);
        configurePassword(confirmField);
        card.add(UIComponents.formRow("FULL NAME", nameField));
        card.add(UIComponents.formRow("FOUR-DIGIT PIN", pinField));
        card.add(UIComponents.formRow("CONFIRM PIN", confirmField));
        card.add(UIComponents.formRow("ACCOUNT TYPE", typeBox));
        card.add(UIComponents.formRow("INITIAL DEPOSIT (MINIMUM ₹500)", depositField));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        buttons.setOpaque(false);
        JButton back = UIComponents.secondaryButton("Back to login");
        JButton create = UIComponents.primaryButton("Create account");
        back.addActionListener(e -> closeToLogin());
        create.addActionListener(e -> createAccount());
        buttons.add(back);
        buttons.add(create);
        card.add(buttons);
        root.add(card, BorderLayout.CENTER);
        JTextArea note = UIComponents.textArea("Your PIN is protected with a salted cryptographic hash and is never displayed.");
        root.add(note, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void configurePassword(JPasswordField field) {
        field.setEchoChar('\u2022');
        field.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(240, 42));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new java.awt.Color(214, 218, 224)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
    }

    private void createAccount() {
        try {
            String pin = new String(pinField.getPassword());
            String confirmation = new String(confirmField.getPassword());
            if (!pin.equals(confirmation)) throw new InvalidAmountException("PIN and confirmation PIN do not match.");
            double deposit;
            try { deposit = Double.parseDouble(depositField.getText().trim()); }
            catch (NumberFormatException ex) { throw new InvalidAmountException("Enter a valid initial deposit."); }
            BAcc account = accountService.register(nameField.getText(), pin, (String) typeBox.getSelectedItem(), deposit);
            JOptionPane.showMessageDialog(this, "Account created successfully!\nYour account number is: " + account.getAccountNumber(),
                    "Welcome to HDFC", JOptionPane.INFORMATION_MESSAGE);
            closeToLogin();
        } catch (InvalidAmountException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Please check your details", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not create account", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeToLogin() {
        dispose();
        loginFrame.setVisible(true);
    }
}
