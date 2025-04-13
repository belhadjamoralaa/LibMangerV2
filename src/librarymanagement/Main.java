package librarymanagement;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize database
        DBInitializer dbInitializer = new DBInitializer();
        dbInitializer.initializeDatabase();
        
        // Get library instance
        Library library = Library.getInstance();
        
        // Start GUI
        SwingUtilities.invokeLater(() -> {
            new MainGUI(library);
        });
    }
}