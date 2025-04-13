package librarymanagement;

import java.util.List;

/**
 * Library class that serves as a facade for the data access objects.
 * It implements the Singleton and Observer patterns.
 */
public class Library implements Subject {
    // Singleton instance
    private static Library instance;
    
    // Data Access Objects
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private LoanDAO loanDAO;
    
    // Observer pattern fields
    private Observer[] observers;
    private int observerCount;
    private static final int MAX_OBSERVERS = 50;

    /**
     * Private constructor for Singleton pattern
     */
    private Library() {
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.bookDAO = daoFactory.getBookDAO();
        this.memberDAO = daoFactory.getMemberDAO();
        this.loanDAO = daoFactory.getLoanDAO();
        this.observers = new Observer[MAX_OBSERVERS];
        this.observerCount = 0;
    }

    /**
     * Get the singleton instance of Library
     * @return The Library instance
     */
    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    //==========================================================================
    // Book-related methods
    //==========================================================================
    
    /**
     * Add a new book to the library
     * @param book The book to add
     */
    public void addBook(Book book) {
        bookDAO.create(book);
        notifyObservers(book);
    }
    
    /**
     * Update an existing book
     * @param book The book to update
     */
    public void updateBook(Book book) {
        bookDAO.update(book);
    }
    
    /**
     * Delete a book by ID
     * @param id The ID of the book to delete
     */
    public void deleteBook(int id) {
        bookDAO.delete(id);
    }
    
    /**
     * Find a book by ID
     * @param id The ID of the book to find
     * @return The found book or null
     */
    public Book findBookById(int id) {
        return bookDAO.findById(id);
    }
    
    /**
     * Get all books in the library
     * @return A list of all books
     */
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    
    /**
     * Get all available books
     * @return A list of available books
     */
    public List<Book> getAvailableBooks() {
        return bookDAO.findAvailableBooks();
    }
    
    /**
     * Get popular books (borrowed more than once)
     * @return A list of popular books
     */
    public List<Book> getPopularBooks() {
        return bookDAO.findPopularBooks();
    }
    
    /**
     * Get all books as an array (backward compatibility method)
     * @return An array of all books
     */
    public Book[] getBooks() {
        List<Book> bookList = getAllBooks();
        Book[] books = new Book[bookList.size()];
        return bookList.toArray(books);
    }
    
    /**
     * Get the number of books (backward compatibility method)
     * @return The number of books
     */
    public int getBookCount() {
        return getAllBooks().size();
    }
    
    //==========================================================================
    // Member-related methods
    //==========================================================================
    
    /**
     * Add a new member to the library
     * @param member The member to add
     */
    public void addMember(Member member) {
        memberDAO.create(member);
        attach(member);
    }
    
    /**
     * Update an existing member
     * @param member The member to update
     */
    public void updateMember(Member member) {
        memberDAO.update(member);
    }
    
    /**
     * Delete a member by ID
     * @param id The ID of the member to delete
     */
    public void deleteMember(int id) {
        memberDAO.delete(id);
    }
    
    /**
     * Find a member by ID
     * @param id The ID of the member to find
     * @return The found member or null
     */
    public Member findMemberById(int id) {
        return memberDAO.findById(id);
    }
    
    /**
     * Get all members
     * @return A list of all members
     */
    public List<Member> getAllMembers() {
        return memberDAO.findAll();
    }
    
    /**
     * Get active members (who have borrowed at least one book)
     * @return A list of active members
     */
    public List<Member> getActiveMembers() {
        return memberDAO.findActiveMembers();
    }
    
    /**
     * Get all members as an array (backward compatibility method)
     * @return An array of all members
     */
    public Member[] getMembers() {
        List<Member> memberList = getAllMembers();
        Member[] members = new Member[memberList.size()];
        return memberList.toArray(members);
    }
    
    /**
     * Get the number of members (backward compatibility method)
     * @return The number of members
     */
    public int getMemberCount() {
        return getAllMembers().size();
    }
    
    //==========================================================================
    // Loan-related methods
    //==========================================================================
    
    /**
     * Borrow a book
     * @param memberId The ID of the member borrowing the book
     * @param bookId The ID of the book being borrowed
     * @return true if successful, false otherwise
     */
    public boolean borrowBook(int memberId, int bookId) {
        Member member = findMemberById(memberId);
        Book book = findBookById(bookId);
        
        if (member != null && book != null && book.getTotalAvailable() > 0) {
            loanDAO.createLoan(memberId, bookId);
            return true;
        }
        return false;
    }
    
    /**
     * Return a borrowed book
     * @param memberId The ID of the member returning the book
     * @param bookId The ID of the book being returned
     * @return true if successful
     */
    public boolean returnBook(int memberId, int bookId) {
        loanDAO.returnBook(memberId, bookId);
        return true;
    }
    
    /**
     * Get books borrowed by a member
     * @param memberId The ID of the member
     * @return A list of books borrowed by the member
     */
    public List<Book> getBorrowedBooksByMember(int memberId) {
        return loanDAO.getBorrowedBooksByMember(memberId);
    }
    
    /**
     * Get the loan history
     * @return A list of loan information objects
     */
    public List<Object[]> getLoanHistory() {
        return loanDAO.getLoanHistory();
    }
    
    /**
     * Get overdue loans
     * @param daysOverdue The number of days overdue
     * @return A list of overdue loan information objects
     */
    public List<Object[]> getOverdueLoans(int daysOverdue) {
        return loanDAO.getOverdueLoans(daysOverdue);
    }
    
    //==========================================================================
    // Observer pattern implementation
    //==========================================================================
    
    /**
     * Attach an observer to the library
     * @param observer The observer to attach
     */
    @Override
    public void attach(Observer observer) {
        if (observerCount < MAX_OBSERVERS) {
            observers[observerCount++] = observer;
        }
    }
    
    /**
     * Detach an observer from the library
     * @param observer The observer to detach
     */
    @Override
    public void detach(Observer observer) {
        for (int i = 0; i < observerCount; i++) {
            if (observers[i] == observer) {
                for (int j = i; j < observerCount - 1; j++) {
                    observers[j] = observers[j + 1];
                }
                observers[--observerCount] = null;
                break;
            }
        }
    }
    
    /**
     * Notify all observers about a new book
     * @param book The book to notify about
     */
    @Override
    public void notifyObservers(Book book) {
        for (int i = 0; i < observerCount; i++) {
            observers[i].update(book);
        }
    }
    
    /**
     * Calculate the loan price for a member borrowing a book.
     * This applies the member's discount strategy to the book's price.
     * 
     * @param memberId The ID of the member
     * @param bookId The ID of the book
     * @return Double array containing [originalPrice, finalPrice] or null if not applicable
     */
    public double[] calculateLoanPrice(int memberId, int bookId) {
        Member member = findMemberById(memberId);
        Book book = findBookById(bookId);
        
        if (member == null || book == null) {
            return null;
        }
        
        // Get original price
        double originalPrice = book.getPrice();
        
        // E-books are free
        if (book instanceof EBook) {
            return new double[] {0.0, 0.0};
        }
        
        // Calculate final price using member's discount strategy
        double finalPrice = member.getPriceStrategy().calculateFinalPrice(originalPrice);
        
        return new double[] {originalPrice, finalPrice};
    }
}