package discount;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DiscountCalculatorSolutionTest {
    private DiscountCalculator calculator;
    @BeforeEach void setUp() { calculator = new DiscountCalculator(); }
    @Test void noDiscountUnderTwoYears() {
        assertAll(() -> assertEquals(100, calculator.applyDiscount(100, 0)),
                  () -> assertEquals(100, calculator.applyDiscount(100, 1)));
    }
    @ParameterizedTest @ValueSource(ints = {2, 3, 4})
    void tenPercentDiscountForTwoToFourYears(int years) {
        assertEquals(90, calculator.applyDiscount(100, years));
    }
    @ParameterizedTest @ValueSource(ints = {5, 10, 50})
    void twentyPercentDiscountAtFiveYearsAndAbove(int years) {
        assertEquals(80, calculator.applyDiscount(100, years));
    }
    @Test void nonPositivePriceThrows() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> calculator.applyDiscount(0, 3)),
                  () -> assertThrows(IllegalArgumentException.class, () -> calculator.applyDiscount(-10, 3)));
    }
}
