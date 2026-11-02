import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public final class UIComponents {
    public static final Color NAVY = Color.BLACK;
    public static final Color NAVY_LIGHT = new Color(224, 224, 224);
    public static final Color GOLD = new Color(238, 238, 238);
    public static final Color IVORY = Color.WHITE;
    public static final Color PAPER = Color.WHITE;
    public static final Color TEXT = Color.BLACK;
    public static final Color MUTED = Color.BLACK;
    public static final Color SUCCESS = Color.BLACK;
    public static final Color ERROR = Color.BLACK;
    private static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font SEMI = new Font("SansSerif", Font.BOLD, 14);

    private UIComponents() { }

    public static JLabel heading(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(NAVY);
        return label;
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = label(text);
        label.setForeground(MUTED);
        return label;
    }

    public static JTextField field() {
        JTextField field = new JTextField();
        field.setFont(BODY);
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(214, 218, 224)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        field.setPreferredSize(new Dimension(240, 42));
        return field;
    }

    public static JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(SEMI);
        button.setForeground(Color.BLACK);
        button.setBackground(color);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton primaryButton(String text) { return button(text, new Color(225, 225, 225)); }
    public static JButton goldButton(String text) { return button(text, new Color(238, 238, 238)); }
    public static JButton secondaryButton(String text) { return button(text, NAVY_LIGHT); }
    public static JButton dangerButton(String text) { return button(text, ERROR); }

    public static RoundedPanel card() { return new RoundedPanel(PAPER, 18); }

    public static RoundedPanel card(Color color) { return new RoundedPanel(color, 18); }

    public static JPanel formRow(String caption, java.awt.Component component) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setOpaque(false);
        row.add(label(caption), BorderLayout.NORTH);
        row.add(component, BorderLayout.CENTER);
        return row;
    }

    public static String money(double value) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(value);
    }

    public static JTextArea textArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(BODY);
        area.setForeground(TEXT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return area;
    }

    public static class RoundedPanel extends JPanel {
        private final Color background;
        private final int radius;
        public RoundedPanel(Color background, int radius) {
            this.background = background;
            this.radius = radius;
            setBackground(background);
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(221, 225, 230)),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        }
    }

    public static void installHover(MouseListener listener, javax.swing.JComponent component) {
        component.addMouseListener(listener);
    }

    public static MouseListener cardHoverListener() {
        return new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            @Override public void mouseExited(MouseEvent e) { e.getComponent().setCursor(Cursor.getDefaultCursor()); }
        };
    }
}
