package librarymanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class LoanPanel extends JPanel {
    private Library library;
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private JButton borrowButton, returnButton, refreshButton;
    private JComboBox<String> filterComboBox;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public LoanPanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout());
        
        // Initialize components
        initComponents();
        loadLoans();
    }

    private void initComponents() {
        // Create table model with column names
        String[] columnNames = {"Membre", "Livre", "Date d'emprunt", "Date de retour"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        loanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Create filter combobox
        String[] filterOptions = {"Tous les emprunts", "Emprunts en cours", "Emprunts en retard (>14 jours)"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> {
            int selectedIndex = filterComboBox.getSelectedIndex();
            switch (selectedIndex) {
                case 0 -> loadLoans();
                case 1 -> loadCurrentLoans();
                case 2 -> loadOverdueLoans();
            }
        });
        
        // Create buttons
        borrowButton = new JButton("Emprunter un livre");
        returnButton = new JButton("Retourner un livre");
        refreshButton = new JButton("Actualiser");
        
        // Add action listeners
        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        refreshButton.addActionListener(e -> loadLoans());
        
        // Add components to control panel
        controlPanel.add(new JLabel("Filtrer: "));
        controlPanel.add(filterComboBox);
        controlPanel.add(borrowButton);
        controlPanel.add(returnButton);
        controlPanel.add(refreshButton);
        
        // Add control panel to main panel
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void loadLoans() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all loans from library
        List<Object[]> loanHistory = library.getLoanHistory();
        
        // Add loans to table
        for (Object[] loanInfo : loanHistory) {
            String memberName = (String) loanInfo[0];
            String bookTitle = (String) loanInfo[1];
            String loanDate = dateFormat.format(loanInfo[2]);
            String returnDate = loanInfo[3] != null ? dateFormat.format(loanInfo[3]) : "Non retourné";
            
            Object[] rowData = {
                memberName,
                bookTitle,
                loanDate,
                returnDate
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void loadCurrentLoans() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get current loans from library
        List<Object[]> loanHistory = library.getLoanHistory();
        
        // Add current loans to table
        for (Object[] loanInfo : loanHistory) {
            if (loanInfo[3] == null) {  // Return date is null
                String memberName = (String) loanInfo[0];
                String bookTitle = (String) loanInfo[1];
                String loanDate = dateFormat.format(loanInfo[2]);
                
                Object[] rowData = {
                    memberName,
                    bookTitle,
                    loanDate,
                    "Non retourné"
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void loadOverdueLoans() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get overdue loans from library (more than 14 days)
        List<Object[]> overdueLoans = library.getOverdueLoans(14);
        
        // Add overdue loans to table
        for (Object[] loanInfo : overdueLoans) {
            String memberName = (String) loanInfo[0];
            String bookTitle = (String) loanInfo[1];
            String loanDate = dateFormat.format(loanInfo[2]);
            
            Object[] rowData = {
                memberName,
                bookTitle,
                loanDate,
                "En retard"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void borrowBook() {
        // Create new dialog for borrowing book
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Emprunter un livre", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        // Get all members and books for selection
        List<Member> members = library.getAllMembers();
        List<Book> availableBooks = library.getAvailableBooks();
        
        String[] memberNames = members.stream()
                                     .map(m -> m.getDbId() + " - " + m.getName())
                                     .toArray(String[]::new);
                                     
        String[] bookTitles = availableBooks.stream()
                                          .map(b -> b.getDbId() + " - " + b.getTitle())
                                          .toArray(String[]::new);
        
        // Currency formatter
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
        // Create form components
        JComboBox<String> memberCombo = new JComboBox<>(memberNames);
        JComboBox<String> bookCombo = new JComboBox<>(bookTitles);
        
        // Add components to dialog
        dialog.add(new JLabel("Membre:"));
        dialog.add(memberCombo);
        dialog.add(new JLabel("Livre:"));
        dialog.add(bookCombo);
        
        // Create OK and Cancel buttons
        JButton okButton = new JButton("Emprunter");
        JButton cancelButton = new JButton("Annuler");
        
        // Add action listeners
        okButton.addActionListener(e -> {
            if (memberCombo.getSelectedIndex() == -1 || bookCombo.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(dialog, "Veuillez sélectionner un membre et un livre.");
                return;
            }
            
            // Parse IDs from selected items
            String memberSelection = (String) memberCombo.getSelectedItem();
            String bookSelection = (String) bookCombo.getSelectedItem();
            
            int memberId = Integer.parseInt(memberSelection.split(" - ")[0]);
            int bookId = Integer.parseInt(bookSelection.split(" - ")[0]);
            
            // Calculate price first
            double[] priceInfo = library.calculateLoanPrice(memberId, bookId);
            
            // Borrow book
            boolean success = library.borrowBook(memberId, bookId);
            
            if (success) {
                // Show price information
                String message;
                if (priceInfo != null && priceInfo[0] > 0) {
                    message = "Livre emprunté avec succès.\n" +
                             "Prix original: " + currencyFormat.format(priceInfo[0]) + "\n" +
                             "Prix final: " + currencyFormat.format(priceInfo[1]);
                    
                    if (priceInfo[1] < priceInfo[0]) {
                        message += "\nÉconomie: " + currencyFormat.format(priceInfo[0] - priceInfo[1]);
                    }
                } else {
                    message = "Livre emprunté avec succès.";
                }
                
                JOptionPane.showMessageDialog(dialog, message);
                dialog.dispose();
                loadLoans();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de l'emprunt du livre.");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add buttons to dialog
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        // Display dialog
        dialog.setVisible(true);
    }
    
    private void returnBook() {
        // Get all members for selection
        List<Member> members = library.getAllMembers();
        
        // Create member selection dialog
        JDialog memberDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sélectionner un membre", true);
        memberDialog.setLayout(new BorderLayout());
        memberDialog.setSize(300, 200);
        memberDialog.setLocationRelativeTo(this);
        
        // Create member list
        DefaultListModel<String> memberListModel = new DefaultListModel<>();
        for (Member member : members) {
            memberListModel.addElement(member.getDbId() + " - " + member.getName());
        }
        
        JList<String> memberList = new JList<>(memberListModel);
        JScrollPane memberScroll = new JScrollPane(memberList);
        memberDialog.add(memberScroll, BorderLayout.CENTER);
        
        JButton selectMemberButton = new JButton("Sélectionner");
        memberDialog.add(selectMemberButton, BorderLayout.SOUTH);
        
        final Member[] selectedMember = new Member[1];
        
        selectMemberButton.addActionListener(e -> {
            String selection = memberList.getSelectedValue();
            if (selection != null) {
                int memberId = Integer.parseInt(selection.split(" - ")[0]);
                selectedMember[0] = library.findMemberById(memberId);
                memberDialog.dispose();
                
                // Now show book selection dialog
                showBookSelectionDialog(selectedMember[0]);
            } else {
                JOptionPane.showMessageDialog(memberDialog, "Veuillez sélectionner un membre.");
            }
        });
        
        memberDialog.setVisible(true);
    }
    
    private void showBookSelectionDialog(Member member) {
        // Get borrowed books for selected member
        List<Book> borrowedBooks = library.getBorrowedBooksByMember(member.getDbId());
        
        if (borrowedBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ce membre n'a pas de livres empruntés.");
            return;
        }
        
        // Create book selection dialog
        JDialog bookDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sélectionner un livre à retourner", true);
        bookDialog.setLayout(new BorderLayout());
        bookDialog.setSize(400, 200);
        bookDialog.setLocationRelativeTo(this);
        
        // Create book list
        DefaultListModel<String> bookListModel = new DefaultListModel<>();
        for (Book book : borrowedBooks) {
            bookListModel.addElement(book.getDbId() + " - " + book.getTitle());
        }
        
        JList<String> bookList = new JList<>(bookListModel);
        JScrollPane bookScroll = new JScrollPane(bookList);
        bookDialog.add(bookScroll, BorderLayout.CENTER);
        
        JButton returnBookButton = new JButton("Retourner le livre");
        bookDialog.add(returnBookButton, BorderLayout.SOUTH);
        
        returnBookButton.addActionListener(e -> {
            String selection = bookList.getSelectedValue();
            if (selection != null) {
                int bookId = Integer.parseInt(selection.split(" - ")[0]);
                
                // Return book
                boolean success = library.returnBook(member.getDbId(), bookId);
                
                if (success) {
                    JOptionPane.showMessageDialog(bookDialog, "Livre retourné avec succès.");
                    bookDialog.dispose();
                    loadLoans();
                } else {
                    JOptionPane.showMessageDialog(bookDialog, "Erreur lors du retour du livre.");
                }
            } else {
                JOptionPane.showMessageDialog(bookDialog, "Veuillez sélectionner un livre.");
            }
        });
        
        bookDialog.setVisible(true);
    }
}