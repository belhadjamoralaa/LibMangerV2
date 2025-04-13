package librarymanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {
    private Connection connection;

    public LoanDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void createLoan(int memberId, int bookId) {
        String query = "INSERT INTO loans (member_id, book_id, loan_date, return_date) VALUES (?, ?, ?, NULL)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, bookId);
            stmt.setTimestamp(3, new Timestamp(new Date().getTime()));
            
            stmt.executeUpdate();
            
            // Update book availability
            updateBookAvailability(bookId, -1);
        } catch (SQLException e) {
            System.err.println("Error creating loan: " + e.getMessage());
        }
    }

    @Override
    public void returnBook(int memberId, int bookId) {
        String query = "UPDATE loans SET return_date = ? WHERE member_id = ? AND book_id = ? AND return_date IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            stmt.setInt(2, memberId);
            stmt.setInt(3, bookId);
            
            int updated = stmt.executeUpdate();
            
            if (updated > 0) {
                // Update book availability
                updateBookAvailability(bookId, 1);
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
        }
    }

    private void updateBookAvailability(int bookId, int change) {
        String query = "UPDATE books SET total_available = total_available + ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, change);
            stmt.setInt(2, bookId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
        }
    }

    @Override
    public List<Book> getBorrowedBooksByMember(int memberId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.* FROM books b JOIN loans l ON b.id = l.book_id " +
                       "WHERE l.member_id = ? AND l.return_date IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("type");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int totalAvailable = rs.getInt("total_available");
                double price = rs.getDouble("price");
                
                Book book = BookFactory.createBook(type, title, author, totalAvailable, price);
                book.setDbId(rs.getInt("id"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving borrowed books: " + e.getMessage());
        }
        
        return books;
    }
    
    @Override
    public List<Object[]> getLoanHistory() {
        List<Object[]> loanHistory = new ArrayList<>();
        String query = "SELECT m.name, b.title, l.loan_date, l.return_date " +
                       "FROM loans l " +
                       "JOIN members m ON l.member_id = m.id " +
                       "JOIN books b ON l.book_id = b.id " +
                       "ORDER BY l.loan_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] loanInfo = new Object[4];
                loanInfo[0] = rs.getString("name");
                loanInfo[1] = rs.getString("title");
                loanInfo[2] = rs.getTimestamp("loan_date");
                loanInfo[3] = rs.getTimestamp("return_date");
                
                loanHistory.add(loanInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving loan history: " + e.getMessage());
        }
        
        return loanHistory;
    }
    
    @Override
    public List<Object[]> getOverdueLoans(int daysOverdue) {
        List<Object[]> overdueLoans = new ArrayList<>();
        // Subquery to find loans that are overdue by more than specified days
        String query = "SELECT m.name, b.title, l.loan_date " +
                       "FROM loans l " +
                       "JOIN members m ON l.member_id = m.id " +
                       "JOIN books b ON l.book_id = b.id " +
                       "WHERE l.return_date IS NULL " +
                       "AND l.loan_date < (NOW() - INTERVAL ? DAY)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, daysOverdue);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] loanInfo = new Object[3];
                loanInfo[0] = rs.getString("name");
                loanInfo[1] = rs.getString("title");
                loanInfo[2] = rs.getTimestamp("loan_date");
                
                overdueLoans.add(loanInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving overdue loans: " + e.getMessage());
        }
        
        return overdueLoans;
    }
}