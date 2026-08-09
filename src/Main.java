import exception.DuplicateUsernameException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import service.AuthService;
import ui.AppFrame;
import util.DatabaseSetup;

/**
 * Launches the e-commerce application and initializes the default environment.
 */
public class Main {

    /**
     * Starts the application and prepares the database and UI.
     */
    public static void main(String[] args) {
        DatabaseSetup.createTables();
        createDefaultAdmin();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new AppFrame().setVisible(true);
        });
    }


    /**
     * Ensures a default administrator account exists on startup.
     */
    private static void createDefaultAdmin() {
        try {
            AuthService.addAdmin(
                    "fahad",
                    "fahad01",
                    "fahad@email.com",
                    "01972742013",
                    "Dhaka"
            );
        } catch (DuplicateUsernameException ignored) {
        
        }
    }
}
