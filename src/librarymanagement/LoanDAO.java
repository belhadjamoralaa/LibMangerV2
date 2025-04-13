package librarymanagement;

import java.util.List;

/**
 * Interface for Loan data access operations
 */
public interface LoanDAO {
    /**
     * Create a new loan
     * @param memberId The ID of the member borrowing the book
     * @param bookId The ID of the book being borrowed
     */
    void createLoan(int memberId, int bookId);
    
    /**
     * Return a borrowed book
     * @param memberId The ID of the member returning the book
     * @param bookId The ID of the book being returned
     */
    void returnBook(int memberId, int bookId);
    
    /**
     * Get all books borrowed by a member
     * @param memberId The ID of the member
     * @return A list of books borrowed by the member
     */
    List<Book> getBorrowedBooksByMember(int memberId);
    
    /**
     * Get the loan history
     * @return A list of loan information objects
     */
    List<Object[]> getLoanHistory();
    
    /**
     * Get overdue loans
     * @param daysOverdue The number of days overdue
     * @return A list of overdue loan information objects
     */
    List<Object[]> getOverdueLoans(int daysOverdue);
}