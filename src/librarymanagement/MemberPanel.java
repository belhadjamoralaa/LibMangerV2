package librarymanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberPanel extends JPanel {
    private Library library;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JComboBox<String> filterComboBox;

    public MemberPanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout());
        
        // Initialize components
        initComponents();
        loadMembers();
    }

    private void initComponents() {
        // Create table model with column names
        String[] columnNames = {"ID", "Nom", "Type de réduction", "Valeur de réduction"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        memberTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Create filter combobox
        String[] filterOptions = {"Tous les membres", "Membres actifs"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> {
            int selectedIndex = filterComboBox.getSelectedIndex();
            switch (selectedIndex) {
                case 0 -> loadMembers();
                case 1 -> loadActiveMembers();
            }
        });
        
        // Create buttons
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        
        // Add action listeners
        addButton.addActionListener(e -> addMember());
        editButton.addActionListener(e -> editMember());
        deleteButton.addActionListener(e -> deleteMember());
        refreshButton.addActionListener(e -> loadMembers());
        
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
    
    private void loadMembers() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all members from library
        List<Member> members = library.getAllMembers();
        
        // Add members to table
        for (Member member : members) {
            String discountType = "";
            double discountValue = 0.0;
            
            if (member.getPriceStrategy() instanceof CodeDiscountStrategy) {
                discountType = "Code (%)";
                discountValue = ((CodeDiscountStrategy) member.getPriceStrategy()).getPercentageOff() * 100;
            } else if (member.getPriceStrategy() instanceof MemberDiscountStrategy) {
                discountType = "Fixe (€)";
                discountValue = ((MemberDiscountStrategy) member.getPriceStrategy()).getFixedDiscount();
            }
            
            Object[] rowData = {
                member.getDbId(),
                member.getName(),
                discountType,
                discountValue
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void loadActiveMembers() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get active members from library
        List<Member> members = library.getActiveMembers();
        
        // Add members to table
        for (Member member : members) {
            String discountType = "";
            double discountValue = 0.0;
            
            if (member.getPriceStrategy() instanceof CodeDiscountStrategy) {
                discountType = "Code (%)";
                discountValue = ((CodeDiscountStrategy) member.getPriceStrategy()).getPercentageOff() * 100;
            } else if (member.getPriceStrategy() instanceof MemberDiscountStrategy) {
                discountType = "Fixe (€)";
                discountValue = ((MemberDiscountStrategy) member.getPriceStrategy()).getFixedDiscount();
            }
            
            Object[] rowData = {
                member.getDbId(),
                member.getName(),
                discountType,
                discountValue
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void addMember() {
        // Create new dialog for adding member
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un membre", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        // Create form components
        JTextField nameField = new JTextField(20);
        JComboBox<String> discountTypeCombo = new JComboBox<>(new String[]{"code", "fixed"});
        JSpinner discountValueSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        
        // Add components to dialog
        dialog.add(new JLabel("Nom:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Type de réduction:"));
        dialog.add(discountTypeCombo);
        dialog.add(new JLabel("Valeur de réduction:"));
        dialog.add(discountValueSpinner);
        
        // Create OK and Cancel buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        // Add action listeners
        okButton.addActionListener(e -> {
            String name = nameField.getText();
            String discountType = (String) discountTypeCombo.getSelectedItem();
            double discountValue = (double) discountValueSpinner.getValue();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.");
                return;
            }
            
            // Adjust discount value for percentage (0-1 range)
            if ("code".equals(discountType)) {
                discountValue /= 100;
            }
            
            Member member = MemberFactory.createMember(name, discountType, discountValue);
            library.addMember(member);
            
            dialog.dispose();
            loadMembers();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add buttons to dialog
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        // Display dialog
        dialog.setVisible(true);
    }
    
    private void editMember() {
        // Get selected row
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un membre.");
            return;
        }
        
        // Get member ID from selected row
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find member in library
        Member member = library.findMemberById(memberId);
        if (member == null) {
            JOptionPane.showMessageDialog(this, "Membre non trouvé.");
            return;
        }
        
        // Create new dialog for editing member
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier un membre", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        // Determine current discount type and value
        String currentDiscountType = "";
        double currentDiscountValue = 0.0;
        
        if (member.getPriceStrategy() instanceof CodeDiscountStrategy) {
            currentDiscountType = "code";
            currentDiscountValue = ((CodeDiscountStrategy) member.getPriceStrategy()).getPercentageOff() * 100;
        } else if (member.getPriceStrategy() instanceof MemberDiscountStrategy) {
            currentDiscountType = "fixed";
            currentDiscountValue = ((MemberDiscountStrategy) member.getPriceStrategy()).getFixedDiscount();
        }
        
        // Create form components
        JTextField nameField = new JTextField(member.getName(), 20);
        JComboBox<String> discountTypeCombo = new JComboBox<>(new String[]{"code", "fixed"});
        discountTypeCombo.setSelectedItem(currentDiscountType);
        JSpinner discountValueSpinner = new JSpinner(new SpinnerNumberModel(currentDiscountValue, 0.0, 100.0, 0.1));
        
        // Add components to dialog
        dialog.add(new JLabel("Nom:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Type de réduction:"));
        dialog.add(discountTypeCombo);
        dialog.add(new JLabel("Valeur de réduction:"));
        dialog.add(discountValueSpinner);
        
        // Create OK and Cancel buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        // Add action listeners
        okButton.addActionListener(e -> {
            String name = nameField.getText();
            String discountType = (String) discountTypeCombo.getSelectedItem();
            double discountValue = (double) discountValueSpinner.getValue();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.");
                return;
            }
            
            // Adjust discount value for percentage (0-1 range)
            if ("code".equals(discountType)) {
                discountValue /= 100;
            }
            
            // Create a new member with the updated values but same ID
            Member updatedMember = MemberFactory.createMember(name, discountType, discountValue);
            updatedMember.setDbId(member.getDbId());
            
            // Update member in library
            library.updateMember(updatedMember);
            
            dialog.dispose();
            loadMembers();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add buttons to dialog
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        // Display dialog
        dialog.setVisible(true);
    }
    
    private void deleteMember() {
        // Get selected row
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un membre.");
            return;
        }
        
        // Get member ID from selected row
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce membre ?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            // Delete member from library
            library.deleteMember(memberId);
            loadMembers();
        }
    }
}