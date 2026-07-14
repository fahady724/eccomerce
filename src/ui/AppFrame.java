package ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.User;
import ui.panels.AdminDashboardPanel;
import ui.panels.CustomerDashboardPanel;
import ui.panels.LoginPanel;
import ui.panels.RegisterPanel;
import ui.panels.StorefrontPanel;


public class AppFrame extends JFrame {
    public static final String STORE = "store";
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String CUSTOMER = "customer";
    public static final String ADMIN = "admin";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    private final StorefrontPanel storefrontPanel;
    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final CustomerDashboardPanel customerDashboardPanel;
    private final AdminDashboardPanel adminDashboardPanel;

    private User currentUser;

    public AppFrame() {
        super("OmniCommerce");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        storefrontPanel = new StorefrontPanel(this);
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        customerDashboardPanel = new CustomerDashboardPanel(this);
        adminDashboardPanel = new AdminDashboardPanel(this);

        cardContainer.add(storefrontPanel, STORE);
        cardContainer.add(loginPanel, LOGIN);
        cardContainer.add(registerPanel, REGISTER);
        cardContainer.add(customerDashboardPanel, CUSTOMER);
        cardContainer.add(adminDashboardPanel, ADMIN);
        setContentPane(cardContainer);

        showScreen(STORE);
    }

  
    public void showScreen(String screenName) {
        if (LOGIN.equals(screenName)) {
            loginPanel.prepareForDisplay();
        } else if (REGISTER.equals(screenName)) {
            registerPanel.prepareForDisplay();
        } else if (STORE.equals(screenName)) {
            storefrontPanel.refreshView();
        } else if (ADMIN.equals(screenName)) {
            adminDashboardPanel.refreshData();
        }

        cardLayout.show(cardContainer, screenName);
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    public void completeLogin(User user) {
        currentUser = user;
        loginPanel.clearForm();
        storefrontPanel.refreshView();
        showScreen(STORE);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public void logout() {
        currentUser = null;
        loginPanel.clearForm();
        registerPanel.clearForm();
        storefrontPanel.refreshView();
        showScreen(STORE);
    }
}
