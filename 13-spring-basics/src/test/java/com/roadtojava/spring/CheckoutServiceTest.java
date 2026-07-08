package com.roadtojava.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Practice: fill in both tests. Run: mvn test
 */
class CheckoutServiceTest {

    @Test
    void plainUnitTest_noSpringContainer() {
        // TODO: construct `new CheckoutService(new DiscountPricingStrategy())` directly —
        // no Spring container involved at all — and assert total(100) == 90.0.
        // This is the whole payoff of constructor injection from module 13's README:
        // testable without booting anything.
        fail("TODO");
    }

    @Test
    void springWiresTheQualifiedStrategy_notThePrimaryOne() {
        // TODO: boot `new AnnotationConfigApplicationContext(AppConfig.class)`,
        // ctx.getBean(CheckoutService.class), and assert total(100) == 90.0 — proving
        // the "discount"-qualified strategy was wired in, NOT the @Primary
        // StandardPricingStrategy (which would give 100.0 unchanged).
        fail("TODO");
    }
}
