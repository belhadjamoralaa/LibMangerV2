package librarymanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
    private Library library;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JComboBox<String> filterComboBox;

    public BookPanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout());
        
        // Initialize components
        initComponents();
        loadBooks();
    }

    private void initComponents() {
        // Create table model with column names
        String[] columnNames = {"ID", "Titre", "Auteur", "Type", "Disponible", "Prix"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Create filter combobox
        String[] filterOptions = {"Tous les livres", "Livres disponibles", "Livres populaires"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> {
            int selectedIndex = filterComboBox.getSelectedIndex();
            switch (selectedIndex) {
                case 0 -> loadBooks();
                case 1 -> loadAvailableBooks();
                case 2 -> loadPopularBooks();
            }
        });
        
        // Create buttons
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        
        // Add action listeners
        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        refreshButton.addActionListener(e -> loadBooks());
        
        // Add components to control panel
        controlPanel.add(new JLabel("Filtrer: "));
        controlPanel.add(filterComboBox);
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        
        // Add control panel to main panel
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void loadBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all books from library
        List<Book> books = library.getAllBooks();
        
        // Add books to table
        for (Book book : books) {
            String type = book instanceof PhysicalBook ? "Physique" : "E-book";
            Object[] rowData = {
                book.getDbId(),
                book.getTitle(),
                book.getAuthor(),
                type,
                book.getTotalAvailable(),
                book.getPrice()
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void loadAvailableBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get available books from library
        List<Book> books = library.getAvailableBooks();
        
        // Add books to table
        for (Book book : books) {
            String type = book instanceof PhysicalBook ? "Physique" : "E-book";
            Object[] rowData = {
                book.getDbId(),
                book.getTitle(),
                book.getAuthor(),
                type,
                book.getTotalAvailable(),
                book.getPrice()
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void loadPopularBooks() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get popular books from library
        List<Book> books = library.getPopularBooks();
        
        // Add books to table
        for (Book book : books) {
            String type = book instanceof PhysicalBook ? "Physique" : "E-book";
            Object[] rowData = {
                book.getDbId(),
                book.getTitle(),
                book.getAuthor(),
                type,
                book.getTotalAvailable(),
                book.getPrice()
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void addBook() {
        // Create new dialog for adding book
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un livre", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Create form components
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"physical", "ebook"});
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JSpinner totalAvailableSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 0.01));
        
        // Add components to dialog
        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Titre:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Auteur:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Exemplaires disponibles:"));
        dialog.add(totalAvailableSpinner);
        dialog.add(new JLabel("Prix:"));
        dialog.add(priceSpinner);
        
        // Create OK and Cancel buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        // Add action listeners
        okButton.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String title = titleField.getText();
            String author = authorField.getText();
            int totalAvailable = (int) totalAvailableSpinner.getValue();
            double price = (double) priceSpinner.getValue();
            
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.");
                return;
            }
            
            Book book = BookFactory.createBook(type, title, author, totalAvailable, price);
            library.addBook(book);
            
            dialog.dispose();
            loadBooks();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add buttons to dialog
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        // Display dialog
        dialog.setVisible(true);
    }
    
    private void editBook() {
        // Get selected row
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre.");
            return;
        }
        
        // Get book ID from selected row
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find book in library
        Book book = library.findBookById(bookId);
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Livre non trouvé.");
            return;
        }
        
        // Create new dialog for editing book
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier un livre", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        // Create form components
        JLabel typeLabel = new JLabel(book instanceof PhysicalBook ? "Physique" : "E-book");
        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JSpinner totalAvailableSpinner = new JSpinner(new SpinnerNumberModel(book.getTotalAvailable(), 0, 100, 1));
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(book.getPrice(), 0.0, 1000.0, 0.01));
        
        // E-books can't have their availability or price changed
        if (book instanceof EBook) {
            totalAvailableSpinner.setEnabled(false);
            priceSpinner.setEnabled(false);
        }
        
        // Add components to dialog
        dialog.add(new JLabel("Type:"));
        dialog.add(typeLabel);
        dialog.add(new JLabel("Titre:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Auteur:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Exemplaires disponibles:"));
        dialog.add(totalAvailableSpinner);
        dialog.add(new JLabel("Prix:"));
        dialog.add(priceSpinner);
        
        // Create OK and Cancel buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        // Add action listeners
        okButton.addActionListener(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.");
                return;
            }
            
            // Update book properties
            book.setTitle(title);
            book.setAuthor(author);
            
            if (book instanceof PhysicalBook) {
                int totalAvailable = (int) totalAvailableSpinner.getValue();
                double price = (double) priceSpinner.getValue();
                book.setTotalAvailable(totalAvailable);
                ((PhysicalBook) book).setBorrowingPrice(price);
            }
            
            // Update book in library
            library.updateBook(book);
            
            dialog.dispose();
            loadBooks();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add buttons to dialog
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        // Display dialog
        dialog.setVisible(true);
    }
    
    private void deleteBook() {
        // Get selected row
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre.");
            return;
        }
        
        // Get book ID from selected row
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce livre ?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            // Delete book from library
            library.deleteBook(bookId);
            loadBooks();
        }
    }
}