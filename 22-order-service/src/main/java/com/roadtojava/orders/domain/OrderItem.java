package com.roadtojava.orders.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class OrderItem {
    @Column(nullable = false, length = 200) private String sku;
    @Column(nullable = false) private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    @Column(nullable = false, length = 3) private String currency;

    protected OrderItem() {}

    public OrderItem(String sku, int quantity, Money unitPrice) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku is required");
        if (quantity < 1) throw new IllegalArgumentException("quantity must be positive");
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice.amount();
        this.currency = unitPrice.currency();
    }

    public String sku() { return sku; }
    public int quantity() { return quantity; }
    public Money unitPrice() { return new Money(unitPrice, currency); }
    public Money subtotal() { return unitPrice().multiply(quantity); }
}
