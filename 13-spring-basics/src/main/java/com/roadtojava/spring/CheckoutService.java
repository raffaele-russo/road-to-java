package com.roadtojava.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CheckoutService {
    private final PricingStrategy pricing;

    public CheckoutService(@Qualifier("discount") PricingStrategy pricing) {
        this.pricing = pricing;
    }

    public double total(double base) {
        return pricing.price(base);
    }
}
