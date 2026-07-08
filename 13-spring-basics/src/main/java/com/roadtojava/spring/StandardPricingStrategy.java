package com.roadtojava.spring;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// @Primary: the default PricingStrategy whenever there's no @Qualifier disambiguating —
// deliberately NOT what CheckoutService should end up wired to (see the exercise).
@Component
@Primary
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double price(double base) {
        return base;
    }
}
