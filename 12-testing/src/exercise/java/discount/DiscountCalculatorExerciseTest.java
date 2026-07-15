package discount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.fail;

/** Learner exercise. Run with: mvn -Pexercise test */
class DiscountCalculatorExerciseTest {
    private DiscountCalculator calculator;
    @BeforeEach void setUp() { calculator = new DiscountCalculator(); }
    @Test void noDiscountUnderTwoYears() { fail("TODO: assert years 0 and 1 keep the full price"); }
    @ParameterizedTest @ValueSource(ints = {2, 3, 4})
    void tenPercentDiscountForTwoToFourYears(int years) { fail("TODO: assert 90.0"); }
    @ParameterizedTest @ValueSource(ints = {5, 10, 50})
    void twentyPercentDiscountAtFiveYearsAndAbove(int years) { fail("TODO: assert 80.0"); }
    @Test void nonPositivePriceThrows() { fail("TODO: assert both invalid prices throw"); }
}
