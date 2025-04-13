package librarymanagement;

/**
 * Factory for creating DAO objects
 * This implements the Factory design pattern for DAOs
 */
public class DAOFactory {
    
    private static DAOFactory instance;
    
    private DAOFactory() {
        // Private constructor to enforce singleton pattern
    }
    
    /**
     * Get the singleton instance of DAOFactory
     * @return The DAOFactory instance
     */
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }
    
    /**
     * Get a BookDAO implementation
     * @return A BookDAO implementation
     */
    public BookDAO getBookDAO() {
        return new BookDAOImpl();
    }
    
    /**
     * Get a MemberDAO implementation
     * @return A MemberDAO implementation
     */
    public MemberDAO getMemberDAO() {
        return new MemberDAOImpl();
    }
    
    /**
     * Get a LoanDAO implementation
     * @return A LoanDAO implementation
     */
    public LoanDAO getLoanDAO() {
        return new LoanDAOImpl();
    }
}