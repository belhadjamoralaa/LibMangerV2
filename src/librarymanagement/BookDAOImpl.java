package librarymanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {
    private Connection connection;

    public BookDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Book book) {
        String query;
        if (book instanceof PhysicalBook) {
            query = "INSERT INTO books (title, author, total_available, type, price) VALUES (?, ?, ?, 'physical', ?)";
        } else {
            query = "INSERT INTO books (title, author, total_available, type, price) VALUES (?, ?, ?, 'ebook', 0)";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getTotalAvailable());
            
            if (book instanceof PhysicalBook) {
                stmt.setDouble(4, book.getPrice());
            }
            
            stmt.executeUpdate();
            
            // Set the database ID for the book
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setDbId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating book: " + e.getMessage());
        }
    }

    @Override
    public Book findById(Integer id) {
        String query = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String type = rs.getString("type");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int totalAvailable = rs.getInt("total_available");
                double price = rs.getDouble("price");
                
                Book book = BookFactory.createBook(type, title, author, totalAvailable, price);
                book.setDbId(rs.getInt("id"));
                return book;
            }
        } catch (SQLException e) {
            System.err.println("Error finding book: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
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
            System.err.println("Error retrieving books: " + e.getMessage());
        }
        
        return books;
    }

    @Override
    public List<Book> findAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE total_available > 0 OR type = 'ebook'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
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
            System.err.println("Error retrieving available books: " + e.getMessage());
        }
        
        return books;
    }

    @Override
    public List<Book> findPopularBooks() {
        List<Book> books = new ArrayList<>();
        // Subquery to find popular books (borrowed more than once)
        String query = "SELECT b.* FROM books b WHERE b.id IN " +
                      "(SELECT book_id FROM loans GROUP BY book_id HAVING COUNT(*) > 1)";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
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
            System.err.println("Error retrieving popular books: " + e.getMessage());
        }
        
        return books;
    }

    @Override
    public void update(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, total_available = ?, price = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getTotalAvailable());
            stmt.setDouble(4, book.getPrice());
            stmt.setInt(5, book.getDbId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM books WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }
}