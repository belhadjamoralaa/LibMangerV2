package librarymanagement;

public class EBook extends Book {
    public EBook(String title, String author) {
        super(title, author, Integer.MAX_VALUE); // E-books are always available
    }

    @Override
    public double getPrice() {
        return 0.0; // E-books are free
    }

    @Override
    public String toString() {
        return super.toString() + " | Prix: Gratuit (E-book)";
    }
}
