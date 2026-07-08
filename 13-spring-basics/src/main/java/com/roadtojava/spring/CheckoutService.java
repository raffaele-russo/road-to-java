package com.roadtojava.spring;

// TODO: add @Component. Constructor-inject a PricingStrategy qualified "discount"
// (see Greeter.java for the same @Qualifier-on-constructor-parameter pattern) — NOT
// the @Primary StandardPricingStrategy. That's the whole point of this exercise: prove
// you can override @Primary with an explicit @Qualifier at the injection point.
public class CheckoutService {
    // TODO: private final PricingStrategy field + constructor

    public double total(double base) {
        // TODO: delegate to the injected PricingStrategy
        throw new UnsupportedOperationException("TODO");
    }
}
