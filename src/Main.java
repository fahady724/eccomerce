import exception.DuplicateUsernameException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import service.AuthService;
import ui.AppFrame;
import util.DatabaseSetup;


public class Main {

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


    private static void createDefaultAdmin() {
        try {
            AuthService.addAdmin(
                    "fahad",
                    "14529882",
                    "fahad@email.com",
                    "01972742013",
                    "Dhaka"
            );
        } catch (DuplicateUsernameException ignored) {
        
        }
    }
}