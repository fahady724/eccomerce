package ui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.User;
import service.UserService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class ProfileFrame extends JFrame {
    private final AppFrame app;
    private final JTextField email = UiFactory.textField(24);
    private final JTextField phone = UiFactory.textField(24);
    private final JTextField address = UiFactory.textField(24);

    public ProfileFrame(AppFrame app) {
        super("OmniCommerce - My Profile");
        this.app = app;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 470);
        setMinimumSize(new Dimension(480, 430));
        setLocationRelativeTo(app);
        buildUi();
    }

    private void buildUi() {
        User user = app.getCurrentUser();
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("My Profile");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel card = UiFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addReadOnly(card, gbc, 0, "Username", user.getUsername());
        addReadOnly(card, gbc, 2, "Role", user.getRole());
        addField(card, gbc, 4, "Email", email);
        addField(card, gbc, 6, "Phone", phone);
        addField(card, gbc, 8, "Delivery address", address);
        email.setText(safe(user.getEmail()));
        phone.setText(safe(user.getPhone()));
        address.setText(safe(user.getAddress()));
        root.add(card, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        JButton close = UiFactory.secondaryButton("Close");
        close.addActionListener(e -> dispose());
        JButton save = UiFactory.primaryButton("Save Changes");
        save.addActionListener(e -> saveProfile());
        actions.add(close, BorderLayout.WEST);
        actions.add(save, BorderLayout.EAST);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(save);
    }

    private void addReadOnly(JPanel panel, GridBagConstraints gbc, int row, String text, String value) {
        gbc.gridy = row; gbc.insets = new Insets(row == 0 ? 0 : 12, 0, 5, 0);
        JLabel label = new JLabel(text); label.setFont(Theme.BODY_BOLD); panel.add(label, gbc);
        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel display = new JLabel(value); display.setFont(Theme.BODY); display.setForeground(Theme.TEXT_MUTED);
        display.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(display, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String text, JTextField field) {
        gbc.gridy = row; gbc.insets = new Insets(12, 0, 5, 0);
        JLabel label = new JLabel(text); label.setFont(Theme.BODY_BOLD); panel.add(label, gbc);
        gbc.gridy = row + 1; gbc.insets = new Insets(0, 0, 0, 0); panel.add(field, gbc);
    }

    private void saveProfile() {
        String emailText = email.getText().trim();
        String phoneText = phone.getText().trim();
        String addressText = address.getText().trim();
        if (!emailText.isEmpty() && !emailText.contains("@")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.", "Invalid email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (UserService.updateProfile(app.getCurrentUser().getId(), emailText, phoneText, addressText)) {
            app.getCurrentUser().setEmail(emailText);
            app.getCurrentUser().setPhone(phoneText);
            app.getCurrentUser().setAddress(addressText);
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Profile could not be updated.", "Update failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safe(String value) { return value == null ? "" : value; }
}
