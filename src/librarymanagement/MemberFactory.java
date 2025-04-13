package librarymanagement;

public class MemberFactory {
    public static Member createMember(String name, String discountType, double discountValue) {
        PriceStrategy strategy;
        
        switch (discountType.toLowerCase()) {
            case "code":
                strategy = new CodeDiscountStrategy("CODE" + name.toUpperCase(), discountValue);
                break;
            case "fixed":
                strategy = new MemberDiscountStrategy(discountValue);
                break;
            default:
                throw new IllegalArgumentException("Type de réduction inconnu");
        }
        
        return new Member(name, strategy);
    }
}