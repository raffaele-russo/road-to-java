package com.roadtojava.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("discount")
public class DiscountPricingStrategy implements PricingStrategy {
    @Override
    public double price(double base) {
        return base * 0.9;
    }
}
