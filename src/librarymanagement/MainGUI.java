package librarymanagement;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private BookPanel bookPanel;
    private MemberPanel memberPanel;
    private LoanPanel loanPanel;
    private Library library;

    public MainGUI(Library library) {
        this.library = library;
        
        // Set up the frame
        setTitle("Système de Gestion de Bibliothèque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize components
        initComponents();
        
        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        // Create a tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        bookPanel = new BookPanel(library);
        memberPanel = new MemberPanel(library);
        loanPanel = new LoanPanel(library);
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Livres", new ImageIcon(), bookPanel, "Gestion des livres");
        tabbedPane.addTab("Membres", new ImageIcon(), memberPanel, "Gestion des membres");
        tabbedPane.addTab("Emprunts", new ImageIcon(), loanPanel, "Gestion des emprunts");
        
        // Add tabbed pane to frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        // Set the menu bar
        setJMenuBar(menuBar);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Système de Gestion de Bibliothèque\nVersion 2.0\n© 2025",
            "À propos",
            JOptionPane.INFORMATION_MESSAGE);
    }
}