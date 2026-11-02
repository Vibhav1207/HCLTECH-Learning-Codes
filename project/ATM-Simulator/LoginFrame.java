import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    private final AccountService accountService;
    private final JTextField accountField = UIComponents.field();
    private final JPasswordField pinField = new JPasswordField();

    public LoginFrame(AccountService accountService) {
        super("HDFC | Secure Login");
        this.accountService = accountService;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIComponents.IVORY);
        root.add(brandPanel(), BorderLayout.WEST);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UIComponents.IVORY);
        UIComponents.RoundedPanel loginCard = UIComponents.card();
        loginCard.setPreferredSize(new Dimension(430, 470));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        JLabel title = UIComponents.heading("Welcome back", 27);
        JLabel subtitle = UIComponents.muted("Sign in to access your account securely.");
        loginCard.add(title);
        loginCard.add(Box.createVerticalStrut(8));
        loginCard.add(subtitle);
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(UIComponents.formRow("ACCOUNT NUMBER", accountField));
        loginCard.add(Box.createVerticalStrut(16));
        pinField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
        pinField.setEchoChar('\u2022');
        pinField.setPreferredSize(new Dimension(240, 42));
        pinField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(214, 218, 224)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        loginCard.add(UIComponents.formRow("FOUR-DIGIT PIN", pinField));
        loginCard.add(Box.createVerticalStrut(25));
        JButton login = UIComponents.primaryButton("Sign in");
        login.setAlignmentX(LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        login.addActionListener(e -> login());
        loginCard.add(login);
        loginCard.add(Box.createVerticalStrut(12));
        JButton register = UIComponents.goldButton("Create new account");
        register.setAlignmentX(LEFT_ALIGNMENT);
        register.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        register.addActionListener(e -> {
            setVisible(false);
            new RegisterFrame(accountService, this).setVisible(true);
        });
        loginCard.add(register);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        JButton admin = UIComponents.secondaryButton("Admin login");
        JButton exit = UIComponents.dangerButton("Exit");
        admin.addActionListener(e -> adminLogin());
        exit.addActionListener(e -> System.exit(0));
        actions.add(admin);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(exit);
        loginCard.add(Box.createVerticalStrut(18));
        loginCard.add(actions);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 60);
        right.add(loginCard, gbc);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel brandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(430, 680));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 50, 50, 50));
        panel.add(Box.createVerticalGlue());
        JLabel bank = new JLabel("HDFC");
        bank.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 26));
        bank.setForeground(Color.BLACK);
        bank.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(bank);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void login() {
        try {
            String number = accountField.getText().trim();
            String pin = new String(pinField.getPassword());
            BAcc account = accountService.login(number, pin);
            pinField.setText("");
            setVisible(false);
            new DashboardFrame(accountService, account, this).setVisible(true);
        } catch (AccountNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Account not found", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidPinException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Sign-in failed", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unexpected error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminLogin() {
        JPanel form = new JPanel(new java.awt.GridLayout(2, 1, 8, 8));
        JTextField user = UIComponents.field();
        JPasswordField password = new JPasswordField();
        password.setPreferredSize(new Dimension(240, 42));
        form.add(UIComponents.formRow("USERNAME", user));
        form.add(UIComponents.formRow("PASSWORD", password));
        int result = JOptionPane.showConfirmDialog(this, form, "Administrator access", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (accountService.authenticateAdmin(user.getText().trim(), new String(password.getPassword()))) {
                setVisible(false);
                new AdminFrame(accountService, this).setVisible(true);
            } else JOptionPane.showMessageDialog(this, "Invalid administrator credentials.", "Access denied", JOptionPane.ERROR_MESSAGE);
        }
    }
}
