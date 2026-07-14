package ui.panels;

import exception.DuplicateUsernameException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import service.AuthService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class RegisterPanel extends JPanel {
    private final AppFrame app;

    private final JTextField usernameField = UiFactory.textField(24);
    private final JTextField emailField = UiFactory.textField(24);
    private final JTextField phoneField = UiFactory.textField(24);
    private final JTextField addressField = UiFactory.textField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JPasswordField confirmPasswordField = new JPasswordField(24);
    private final JButton registerButton = UiFactory.primaryButton("Register");

    public RegisterPanel(AppFrame app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        stylePasswordField(passwordField);
        stylePasswordField(confirmPasswordField);

        JPanel card = UiFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 7, 5, 7);

        JLabel title = new JLabel("Create customer account");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        c.gridy = 0;
        card.add(title, c);

        addField(card, c, "Username", usernameField);
        addField(card, c, "Email", emailField);
        addField(card, c, "Phone", phoneField);
        addField(card, c, "Address", addressField);
        addField(card, c, "Password", passwordField);
        addField(card, c, "Confirm Password", confirmPasswordField);

        registerButton.addActionListener(e -> attemptRegistration());

        usernameField.addActionListener(e -> emailField.requestFocusInWindow());
        emailField.addActionListener(e -> phoneField.requestFocusInWindow());
        phoneField.addActionListener(e -> addressField.requestFocusInWindow());
        addressField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocusInWindow());
        confirmPasswordField.addActionListener(e -> registerButton.doClick());

        c.gridy++;
        c.insets = new Insets(16, 7, 5, 7);
        card.add(registerButton, c);

        JButton loginButton = UiFactory.secondaryButton("Already have an account? Login");
        loginButton.addActionListener(e -> app.showScreen(AppFrame.LOGIN));
        c.gridy++;
        c.insets = new Insets(5, 7, 5, 7);
        card.add(loginButton, c);

        JButton backButton = UiFactory.secondaryButton("Back to Store");
        backButton.addActionListener(e -> {
            clearForm();
            app.showScreen(AppFrame.STORE);
        });
        c.gridy++;
        card.add(backButton, c);
        add(card);
    }

    private void addField(JPanel card, GridBagConstraints c, String label, java.awt.Component field) {
        c.gridy++;
        card.add(new JLabel(label), c);
        c.gridy++;
        card.add(field, c);
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(Theme.BODY);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmation = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, email, and password are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmation)) {
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
         
            AuthService.register(username, password, email, phone, address);
            JOptionPane.showMessageDialog(this, "Registration successful. You may now log in.", "Registration complete", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            app.showScreen(AppFrame.LOGIN);
        } catch (DuplicateUsernameException exception) {
            usernameField.requestFocusInWindow();
            usernameField.selectAll();
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void clearForm() {
        usernameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

   
    public void prepareForDisplay() {
        clearForm();
        SwingUtilities.invokeLater(() -> {
            app.getRootPane().setDefaultButton(registerButton);
            usernameField.requestFocusInWindow();
        });
    }
}
