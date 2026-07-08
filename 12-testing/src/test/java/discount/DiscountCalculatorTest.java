package discount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Practice: write these tests from scratch against the already-implemented
 * DiscountCalculator. Replace every fail("TODO") with a real assertion. Run: mvn test
 */
class DiscountCalculatorTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    @Test
    void noDiscountUnderTwoYears() {
        // TODO: for loyaltyYears 0 and 1, applyDiscount(100, years) should return 100.0 unchanged
        fail("TODO");
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void tenPercentDiscountForTwoToFourYears(int years) {
        // TODO: applyDiscount(100, years) should return 90.0 for every value in {2, 3, 4}
        fail("TODO");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 50})
    void twentyPercentDiscountAtFiveYearsAndAbove(int years) {
        // TODO: applyDiscount(100, years) should return 80.0 for every value in {5, 10, 50}
        fail("TODO");
    }

    @Test
    void nonPositivePriceThrows() {
        // TODO: use assertThrows to verify applyDiscount(0, 3) and applyDiscount(-10, 3)
        // both throw IllegalArgumentException
        fail("TODO");
    }
}
