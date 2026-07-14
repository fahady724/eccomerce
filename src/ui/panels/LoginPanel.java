package ui.panels;

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
import model.User;
import service.AuthService;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class LoginPanel extends JPanel {
    private final AppFrame app;
    private final JTextField usernameField = UiFactory.textField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JButton loginButton = UiFactory.primaryButton("Login");

    public LoginPanel(AppFrame app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);
        stylePasswordField();

        JPanel card = UiFactory.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(7, 7, 7, 7);

        JLabel title = new JLabel("Welcome back");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        c.gridy = 0;
        card.add(title, c);

        c.gridy++;
        card.add(UiFactory.mutedLabel("Your account role is detected automatically after login."), c);

        c.gridy++;
        card.add(new JLabel("Username"), c);
        c.gridy++;
        card.add(usernameField, c);
        c.gridy++;
        card.add(new JLabel("Password"), c);
        c.gridy++;
        card.add(passwordField, c);

        loginButton.addActionListener(e -> attemptLogin());

        JButton registerButton = UiFactory.secondaryButton("Create customer account");
        registerButton.addActionListener(e -> app.showScreen(AppFrame.REGISTER));

        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> loginButton.doClick());

        c.gridy++;
        c.insets = new Insets(18, 7, 7, 7);
        card.add(loginButton, c);
        c.gridy++;
        c.insets = new Insets(7, 7, 7, 7);
        card.add(registerButton, c);

        JButton backButton = UiFactory.secondaryButton("Back to Store");
        backButton.addActionListener(e -> {
            clearForm();
            app.showScreen(AppFrame.STORE);
        });
        c.gridy++;
        card.add(backButton, c);

        add(card);
    }

    private void stylePasswordField() {
        passwordField.setFont(Theme.BODY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = AuthService.login(username, password);
        if (user == null) {
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Welcome, " + user.getUsername() + "!", "Login successful", JOptionPane.INFORMATION_MESSAGE);
        app.completeLogin(user);
    }

    public void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public void prepareForDisplay() {
        clearForm();
        SwingUtilities.invokeLater(() -> {
            app.getRootPane().setDefaultButton(loginButton);
            usernameField.requestFocusInWindow();
        });
    }
}
