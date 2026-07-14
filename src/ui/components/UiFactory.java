package ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import ui.Theme;


public final class UiFactory {
    private UiFactory() {}

    public static JButton primaryButton(String text) {
        return filledButton(text, Theme.PRIMARY, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return filledButton(text, Theme.SURFACE_SOFT, Theme.TEXT);
    }

    public static JButton dangerButton(String text) {
        return filledButton(text, Theme.DANGER, Color.WHITE);
    }

    private static JButton filledButton(String text, Color background, Color foreground) {
        JButton button = baseButton(text);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(new CompoundBorder(BorderFactory.createLineBorder(background.darker()), new EmptyBorder(10, 17, 10, 17)));
        return button;
    }

    private static JButton baseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.BODY_BOLD);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JPanel brandLogo() {
        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setOpaque(false);

        JLabel mark = new JLabel("O", JLabel.CENTER);
        mark.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        mark.setOpaque(true);
        mark.setBackground(Theme.PRIMARY);
        mark.setForeground(Color.WHITE);
        mark.setPreferredSize(new Dimension(38, 38));
        mark.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY_DARK));

        JLabel wordmark = new JLabel("OMNI COMMERCE");
        wordmark.setFont(Theme.HEADING);
        wordmark.setForeground(Theme.TEXT);

        brand.add(mark, BorderLayout.WEST);
        brand.add(wordmark, BorderLayout.CENTER);
        return brand;
    }

    public static JTextField textField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(Theme.BODY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 42));
        field.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.BODY);
        label.setForeground(Theme.TEXT_MUTED);
        return label;
    }

    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.SURFACE);
        panel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }
}
