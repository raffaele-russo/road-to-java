package com.roadtojava.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class CheckoutServiceSolutionTest {
    @Test void plainUnitTest_noSpringContainer() {
        assertEquals(90, new CheckoutService(new DiscountPricingStrategy()).total(100));
    }
    @Test void springWiresTheQualifiedStrategy_notThePrimaryOne() {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            assertEquals(90, context.getBean(CheckoutService.class).total(100));
        }
    }
}
