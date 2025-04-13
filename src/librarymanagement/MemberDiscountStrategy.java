package librarymanagement;

public class MemberDiscountStrategy implements PriceStrategy {
    private double fixedDiscount;

    public MemberDiscountStrategy(double fixedDiscount) {
        this.fixedDiscount = fixedDiscount;
    }

    @Override
    public double calculateFinalPrice(double basePrice) {
        return Math.max(0, basePrice - fixedDiscount);
    }
    
    public double getFixedDiscount() { return fixedDiscount; }
}