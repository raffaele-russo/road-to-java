package discount;

/** Loyalty-based discount pricing — the target of the module 12 practice exercise.
 *  Don't modify this class; write tests against it in DiscountCalculatorTest. */
public class DiscountCalculator {

    public double applyDiscount(double price, int loyaltyYears) {
        if (price <= 0) {
            throw new IllegalArgumentException("price must be positive");
        }
        double rate = rateFor(loyaltyYears);
        return price * (1 - rate);
    }

    private double rateFor(int loyaltyYears) {
        if (loyaltyYears >= 5) return 0.20;
        if (loyaltyYears >= 2) return 0.10;
        return 0.0;
    }
}
