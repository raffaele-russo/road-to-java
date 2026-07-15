package com.roadtojava.orders.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.signum() < 0) throw new IllegalArgumentException("amount must not be negative");
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be ISO-4217 style");
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        currency = currency.toUpperCase(java.util.Locale.ROOT);
    }

    public Money multiply(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("quantity must be positive");
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public Money add(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException("currency mismatch");
        return new Money(amount.add(other.amount), currency);
    }
}
