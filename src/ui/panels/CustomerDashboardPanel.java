package ui.panels;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import ui.AppFrame;
import ui.Theme;
import ui.components.UiFactory;


public class CustomerDashboardPanel extends JPanel {
    public CustomerDashboardPanel(AppFrame app) {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        JLabel title = new JLabel("Customer Dashboard");
        title.setFont(Theme.TITLE);
        add(title, BorderLayout.NORTH);
        add(UiFactory.mutedLabel("Next milestone: customer navigation, cart, checkout, orders, and profile."), BorderLayout.CENTER);
        JButton logout = UiFactory.secondaryButton("Logout");
        logout.addActionListener(e -> app.logout());
        add(logout, BorderLayout.SOUTH);
    }
}
