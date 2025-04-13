package librarymanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Database initializer that creates tables and populates them with sample data.
 * This class uses batch processing and transactions for efficient database operations.
 */
public class DBInitializer {
    private Connection connection;
    
    public DBInitializer() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public void initializeDatabase() {
        try {
            // Create database if it doesn't exist
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS library_db");
                stmt.executeUpdate("USE library_db");
            }
            
            // Create tables
            createTables();
            
            // Check if data already exists
            boolean hasData = false;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasData = true;
                }
            }
            
            // Populate tables with sample data if needed
            if (!hasData) {
                insertSampleData();
                System.out.println("Sample data inserted successfully!");
            }
            
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    private void createTables() throws SQLException {
        String[] tableCreationStatements = {
            // Books table
            """
            CREATE TABLE IF NOT EXISTS books (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL,
                total_available INT NOT NULL,
                type VARCHAR(50) NOT NULL,
                price DOUBLE NOT NULL
            )
            """,
            
            // Members table
            """
            CREATE TABLE IF NOT EXISTS members (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                discount_type VARCHAR(50) NOT NULL,
                discount_value DOUBLE NOT NULL
            )
            """,
            
            // Loans table
            """
            CREATE TABLE IF NOT EXISTS loans (
                id INT AUTO_INCREMENT PRIMARY KEY,
                member_id INT NOT NULL,
                book_id INT NOT NULL,
                loan_date TIMESTAMP NOT NULL,
                return_date TIMESTAMP NULL,
                FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
                FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
            )
            """
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String sqlStatement : tableCreationStatements) {
                stmt.executeUpdate(sqlStatement);
            }
        }
    }
    
    private void insertSampleData() throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Insert books using bulk insert
            insertSampleBooks();
            
            // Insert members using bulk insert
            insertSampleMembers();
            
            // Insert loans using bulk insert
            insertSampleLoans();
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    private void insertSampleBooks() throws SQLException {
        String insertBooksSQL = 
            "INSERT INTO books (title, author, total_available, type, price) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertBooksSQL)) {
            // Sample book data: title, author, available copies, type, price
            Object[][] bookData = {
                // Physical Books
                {"Le Petit Prince", "Antoine de Saint-Exupéry", 3, "physical", 15.99},
                {"Les Misérables", "Victor Hugo", 2, "physical", 19.99},
                {"L'Étranger", "Albert Camus", 4, "physical", 12.99},
                {"Madame Bovary", "Gustave Flaubert", 2, "physical", 17.99},
                {"Notre-Dame de Paris", "Victor Hugo", 3, "physical", 18.99},
                {"Bel-Ami", "Guy de Maupassant", 1, "physical", 14.50},
                {"Le Comte de Monte-Cristo", "Alexandre Dumas", 2, "physical", 22.99},
                {"Germinal", "Émile Zola", 3, "physical", 16.75},
                {"Le Rouge et le Noir", "Stendhal", 1, "physical", 15.25},
                {"Voyage au bout de la nuit", "Louis-Ferdinand Céline", 2, "physical", 18.50},
                {"1984", "George Orwell", 3, "physical", 14.99},
                {"To Kill a Mockingbird", "Harper Lee", 4, "physical", 13.99},
                {"The Great Gatsby", "F. Scott Fitzgerald", 2, "physical", 12.50},
                {"Pride and Prejudice", "Jane Austen", 3, "physical", 11.99},
                {"The Catcher in the Rye", "J.D. Salinger", 2, "physical", 13.75},
                {"The Lord of the Rings", "J.R.R. Tolkien", 2, "physical", 29.99},
                {"Harry Potter and the Philosopher's Stone", "J.K. Rowling", 5, "physical", 24.99},
                {"Brave New World", "Aldous Huxley", 3, "physical", 15.50},
                {"Animal Farm", "George Orwell", 4, "physical", 11.99},
                {"The Hobbit", "J.R.R. Tolkien", 3, "physical", 19.99},
                {"Sapiens: A Brief History of Humankind", "Yuval Noah Harari", 3, "physical", 23.99},
                {"A Brief History of Time", "Stephen Hawking", 2, "physical", 18.99},
                {"The Art of War", "Sun Tzu", 4, "physical", 10.99},
                {"Thinking, Fast and Slow", "Daniel Kahneman", 2, "physical", 17.50},
                {"The Selfish Gene", "Richard Dawkins", 1, "physical", 16.99},
                {"A Short History of Nearly Everything", "Bill Bryson", 2, "physical", 19.99},
                {"The Origin of Species", "Charles Darwin", 1, "physical", 14.99},
                {"Astérix et Obélix", "René Goscinny & Albert Uderzo", 5, "physical", 9.99},
                {"Tintin au Tibet", "Hergé", 3, "physical", 11.99},
                {"Les Aventures de Lucky Luke", "Morris & Goscinny", 4, "physical", 10.50},
                {"Persepolis", "Marjane Satrapi", 2, "physical", 15.99},
                {"Watchmen", "Alan Moore & Dave Gibbons", 2, "physical", 18.99},
                
                // E-Books
                {"Dom Juan", "Molière", Integer.MAX_VALUE, "ebook", 0.0},
                {"La Peste", "Albert Camus", Integer.MAX_VALUE, "ebook", 0.0},
                {"Les Fleurs du Mal", "Charles Baudelaire", Integer.MAX_VALUE, "ebook", 0.0},
                {"Candide", "Voltaire", Integer.MAX_VALUE, "ebook", 0.0},
                {"Le Père Goriot", "Honoré de Balzac", Integer.MAX_VALUE, "ebook", 0.0},
                {"Phèdre", "Jean Racine", Integer.MAX_VALUE, "ebook", 0.0},
                {"Les Fables", "Jean de La Fontaine", Integer.MAX_VALUE, "ebook", 0.0},
                {"Cyrano de Bergerac", "Edmond Rostand", Integer.MAX_VALUE, "ebook", 0.0},
                {"Meditations", "Marcus Aurelius", Integer.MAX_VALUE, "ebook", 0.0},
                {"The Republic", "Plato", Integer.MAX_VALUE, "ebook", 0.0},
                {"The Prince", "Niccolò Machiavelli", Integer.MAX_VALUE, "ebook", 0.0},
                {"Beyond Good and Evil", "Friedrich Nietzsche", Integer.MAX_VALUE, "ebook", 0.0},
                {"Critique of Pure Reason", "Immanuel Kant", Integer.MAX_VALUE, "ebook", 0.0},
                
            };
            
            for (Object[] book : bookData) {
                pstmt.setString(1, (String) book[0]);
                pstmt.setString(2, (String) book[1]);
                pstmt.setInt(3, (Integer) book[2]);
                pstmt.setString(4, (String) book[3]);
                pstmt.setDouble(5, (Double) book[4]);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        }
    }
    
    private void insertSampleMembers() throws SQLException {
        String insertMembersSQL = 
            "INSERT INTO members (name, discount_type, discount_value) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertMembersSQL)) {
            // Sample member data: name, discount type, discount value
            Object[][] memberData = {
                // Regular members with code discount (percentage as decimal)
                {"Jean Dupont", "code", 0.2},     // 20% discount
                {"Marie Martin", "code", 0.1},     // 10% discount
                {"Pierre Durand", "code", 0.15},   // 15% discount
                {"Sophie Petit", "code", 0.25},    // 25% discount
                {"Thomas Leroy", "code", 0.05},    // 5% discount
                {"Emma Dubois", "code", 0.30},     // 30%  discount
                {"Maxime Girard", "code", 0.30},   // 30%  discount
                {"Chloé Lefebvre", "code", 0.30},  // 30%  discount

                // members with fixed discount (in euro)
                {"Amélie Bernard", "fixed", 7.0},  // 7€ fixed discount
                {"François Moreau", "fixed", 5.0}, // 5€ fixed discount
                {"Julie Lambert", "fixed", 10.0},  // 10€ fixed discount
                {"Lucas Martin", "fixed", 3.0},    // 3€ fixed discount
                {"Robert Morel", "fixed", 8.0},    // 8€ senior discount
                {"Jeanne Roux", "fixed", 8.0},     // 8€ senior discount
                
                // Other members with varying discounts
                {"Nathalie Simon", "code", 0.12},   // 12% discount
                {"Daniel Fournier", "fixed", 4.5},  // 4.5€ fixed discount
                {"Aurélie Mercier", "code", 0.18},  // 18% discount
                {"Philippe Blanc", "fixed", 6.0},   // 6€ fixed discount
                {"Isabelle Garnier", "code", 0.08}  // 8% discount
            };
            
            for (Object[] member : memberData) {
                pstmt.setString(1, (String) member[0]);
                pstmt.setString(2, (String) member[1]);
                pstmt.setDouble(3, (Double) member[2]);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        }
    }
    
    private void insertSampleLoans() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        String insertLoansSQL = 
            "INSERT INTO loans (member_id, book_id, loan_date, return_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertLoansSQL)) {
            // Sample loan data: member_id, book_id, days ago loaned, days ago returned (null if not returned)
            Object[][] loanData = {

                // Returned loans (completed loans)
                {1, 1, 30, 20},    // Jean borrowed Le Petit Prince and returned it
                {2, 3, 25, 15},    // Marie borrowed L'Étranger and returned it
                {3, 5, 20, 10},    // Pierre borrowed Notre-Dame de Paris and returned it
                {1, 7, 40, 30},    // Jean borrowed Le Comte de Monte-Cristo and returned it
                {4, 9, 35, 25},    // Sophie borrowed Le Rouge et le Noir and returned it
                {5, 19, 30, 20},   // Thomas borrowed 1984 and returned it
                {6, 20, 25, 15},   // Amélie borrowed To Kill a Mockingbird and returned it
                {7, 21, 20, 10},   // François borrowed The Great Gatsby and returned it
                {2, 22, 45, 35},   // Marie borrowed Pride and Prejudice and returned it
                {8, 23, 15, 5},    // Julie borrowed The Catcher in the Rye and returned it
                {3, 25, 10, 3},    // Pierre borrowed Harry Potter and returned it
                {9, 28, 50, 42},   // Lucas borrowed The Hobbit and returned it
                
                // Borrowed multiple times (popular books)
                {1, 1, 90, 80},    // Le Petit Prince borrowed again (first time)
                {4, 1, 70, 65},    // Le Petit Prince borrowed again (second time)
                {8, 1, 40, 35},    // Le Petit Prince borrowed again (third time)
                {3, 19, 85, 80},   // 1984 borrowed again (first time)
                {7, 19, 65, 60},   // 1984 borrowed again (second time)
                {5, 25, 55, 50},   // Harry Potter borrowed again (first time)
                {6, 25, 30, 25},   // Harry Potter borrowed again (second time)
                
                // Current loans (not yet returned)
                {10, 2, 15, null},  // Emma has Les Misérables
                {11, 4, 10, null},  // Maxime has Madame Bovary
                {12, 6, 7, null},   // Chloé has Bel-Ami
                {13, 8, 12, null},  // Robert has Germinal
                {14, 24, 9, null},  // Jeanne has Lord of the Rings
                {15, 26, 6, null},  // Nathalie has Brave New World
                {16, 27, 4, null},  // Daniel has Animal Farm
                {17, 29, 8, null},  // Aurélie has Sapiens
                {18, 30, 3, null},  // Philippe has A Brief History of Time
                
                // Overdue loans (more than 14 days and not returned)
                {19, 31, 20, null}, // Isabelle has The Art of War (overdue)
                {10, 33, 25, null}, // Emma has The Selfish Gene (overdue)
                {11, 36, 30, null}, // Maxime has The Origin of Species (overdue)
                {12, 41, 19, null}, // Chloé has Astérix et Obélix (overdue)
                {13, 43, 22, null}, // Robert has Les Aventures de Lucky Luke (overdue)
                
                // Special cases
                // Same member borrowing multiple books
                {1, 20, 5, null},   // Jean also has To Kill a Mockingbird currently
                {1, 29, 8, null},   // Jean also has Sapiens currently
                {1, 41, 12, null},  // Jean also has Astérix et Obélix currently
                
                // Same book borrowed multiple times
                {1, 25, 120, 110},  // Jean borrowed Harry Potter in the past
                {2, 25, 100, 90},   // Marie borrowed Harry Potter in the past
                {3, 25, 80, 75},    // Pierre borrowed Harry Potter in the past
                {5, 25, 55, 50},    // Thomas borrowed Harry Potter in the past
                {6, 25, 30, 25}     // Amélie borrowed Harry Potter in the past
            };
            
            for (Object[] loan : loanData) {
                int memberId = (Integer) loan[0];
                int bookId = (Integer) loan[1];
                int daysAgoLoaned = (Integer) loan[2];
                
                LocalDateTime loanDate = now.minus(daysAgoLoaned, ChronoUnit.DAYS);
                Timestamp loanTimestamp = Timestamp.valueOf(loanDate);
                
                pstmt.setInt(1, memberId);
                pstmt.setInt(2, bookId);
                pstmt.setTimestamp(3, loanTimestamp);
                
                if (loan[3] != null) {
                    int daysAgoReturned = (Integer) loan[3];
                    LocalDateTime returnDate = now.minus(daysAgoReturned, ChronoUnit.DAYS);
                    Timestamp returnTimestamp = Timestamp.valueOf(returnDate);
                    pstmt.setTimestamp(4, returnTimestamp);
                } else {
                    pstmt.setNull(4, java.sql.Types.TIMESTAMP);
                }
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        }
    }
}