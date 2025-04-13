package librarymanagement;

public class CodeDiscountStrategy implements PriceStrategy {
    private String code;
    private double percentageOff;

    public CodeDiscountStrategy(String code, double percentageOff) {
        this.code = code;
        this.percentageOff = percentageOff;
    }

    @Override
    public double calculateFinalPrice(double basePrice) {
        return basePrice * (1 - percentageOff);
    }
    
    public String getCode() { return code; }
    public double getPercentageOff() { return percentageOff; }
}