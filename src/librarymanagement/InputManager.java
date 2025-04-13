package librarymanagement;

public class InputManager {
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);

    public static int getMenuChoice() {
        System.out.print("Votre choix: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public static Book createBook() {
        scanner.nextLine(); // Clear buffer
        System.out.println("Type (physical/ebook): ");
        String type = scanner.nextLine();
        System.out.println("Titre: ");
        String title = scanner.nextLine();
        System.out.println("Auteur: ");
        String author = scanner.nextLine();

        int totalAvailable = 0;
        double price = 0;
        if (type.equalsIgnoreCase("physical")) {
            System.out.println("Nombre d'exemplaires: ");
            totalAvailable = scanner.nextInt();
            System.out.println("Prix: ");
            price = scanner.nextDouble();
        }

        return BookFactory.createBook(type, title, author, totalAvailable, price);
    }

    public static Member createMember() {
        scanner.nextLine(); // Clear buffer
        System.out.println("Nom: ");
        String name = scanner.nextLine();
        System.out.println("Type de réduction (code/fixed): ");
        String discountType = scanner.nextLine();
        System.out.println("Valeur de la réduction (pourcentage si code, montant si fixed): ");
        double discountValue = scanner.nextDouble();

        return MemberFactory.createMember(name, discountType, discountValue);
    }
}