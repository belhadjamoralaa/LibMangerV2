package librarymanagement;

public class Member implements Observer {
    private static int counter = 0;
    private int id;
    private int dbId; // Database ID
    private String name;
    private PriceStrategy priceStrategy;
    private Book[] borrowedBooks;
    private int borrowedCount;
    private static final int MAX_BORROWED = 5;

    public Member(String name, PriceStrategy priceStrategy) {
        this.id = ++counter;
        this.name = name;
        this.priceStrategy = priceStrategy;
        this.borrowedBooks = new Book[MAX_BORROWED];
        this.borrowedCount = 0;
    }

    @Override
    public void update(Book newBook) {
        System.out.println("Notification pour " + name + ": Nouveau livre disponible - " + newBook.getTitle());
    }

    public boolean borrowBook(Book book) {
        if (borrowedCount >= MAX_BORROWED) {
            System.out.println("Nombre maximum de livres empruntés atteint!");
            return false;
        }
        if (book.getTotalAvailable() <= 0) {
            System.out.println("Livre non disponible!");
            return false;
        }

        double finalPrice = priceStrategy.calculateFinalPrice(book.getPrice());
        book.updateAvailability(true);
        borrowedBooks[borrowedCount++] = book;
        System.out.println("Prix final après réduction: " + finalPrice + "€");
        return true;
    }

    public boolean returnBook(Book book) {
        for (int i = 0; i < borrowedCount; i++) {
            if (borrowedBooks[i].getId() == book.getId()) {
                book.updateAvailability(false);
                for (int j = i; j < borrowedCount - 1; j++) {
                    borrowedBooks[j] = borrowedBooks[j + 1];
                }
                borrowedBooks[--borrowedCount] = null;
                return true;
            }
        }
        return false;
    }

    public int getId() { return id; }
    public int getDbId() { return dbId; }
    public String getName() { return name; }
    public PriceStrategy getPriceStrategy() { return priceStrategy; }
    public Book[] getBorrowedBooks() { return borrowedBooks; }
    public int getBorrowedCount() { return borrowedCount; }

    public void setDbId(int dbId) { this.dbId = dbId; }
    public void setName(String name) { this.name = name; }
    public void setPriceStrategy(PriceStrategy priceStrategy) { this.priceStrategy = priceStrategy; }

    @Override
    public String toString() {
        return "ID: " + id + " | Nom: " + name + " | Livres empruntés: " + borrowedCount;
    }
}