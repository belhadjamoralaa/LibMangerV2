package librarymanagement;

public class PhysicalBook extends Book {
    private double borrowingPrice;

    public PhysicalBook(String title, String author, int totalAvailable, double borrowingPrice) {
        super(title, author, totalAvailable);
        this.borrowingPrice = borrowingPrice;
    }

    @Override
    public double getPrice() {
        return borrowingPrice;
    }
    
    public void setBorrowingPrice(double borrowingPrice) {
        this.borrowingPrice = borrowingPrice;
    }

    @Override
    public String toString() {
        return super.toString() + " | Prix: " + borrowingPrice + "€";
    }
}