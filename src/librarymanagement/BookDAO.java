package librarymanagement;

import java.util.List;

/**
 * Interface for Book data access operations
 */
public interface BookDAO extends DAO<Book, Integer> {
    /**
     * Find all available books
     * @return A list of available books
     */
    List<Book> findAvailableBooks();
    
    /**
     * Find popular books (borrowed more than once)
     * @return A list of popular books
     */
    List<Book> findPopularBooks();
}