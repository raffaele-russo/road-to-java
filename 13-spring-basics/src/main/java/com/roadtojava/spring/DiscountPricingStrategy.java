package com.roadtojava.spring;

// TODO: add @Component and @Qualifier("discount") (see SmsMessageService.java for the
// same pattern already used elsewhere in this module).
public class DiscountPricingStrategy implements PricingStrategy {
    @Override
    public double price(double base) {
        // TODO: return a 10% discount off base
        throw new UnsupportedOperationException("TODO");
    }
}
