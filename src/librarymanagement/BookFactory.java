package librarymanagement;

public class BookFactory {
    public static Book createBook(String type, String title, String author, int totalAvailable, double price) {
        return switch (type.toLowerCase()) {
            case "physical" -> new PhysicalBook(title, author, totalAvailable, price);
            case "ebook" -> new EBook(title, author);
            default -> throw new IllegalArgumentException("Type de livre inconnu");
        };
    }
}