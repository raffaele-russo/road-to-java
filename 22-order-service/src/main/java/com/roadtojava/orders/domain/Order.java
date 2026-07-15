package com.roadtojava.orders.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id private UUID id;
    @Column(name = "customer_id", nullable = false, length = 200) private String customerId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private OrderStatus status;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version private long version;

    protected Order() {}

    public Order(UUID id, String customerId, List<OrderItem> items, Instant now) {
        if (customerId == null || customerId.isBlank()) throw new IllegalArgumentException("customer is required");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("at least one item is required");
        String currency = items.getFirst().unitPrice().currency();
        if (items.stream().anyMatch(item -> !item.unitPrice().currency().equals(currency)))
            throw new IllegalArgumentException("all items must use one currency");
        this.id = id;
        this.customerId = customerId;
        this.items.addAll(items);
        this.status = OrderStatus.PENDING;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void confirm(Instant now) {
        if (status != OrderStatus.PENDING) throw new InvalidOrderTransitionException(status, OrderStatus.CONFIRMED);
        status = OrderStatus.CONFIRMED;
        updatedAt = now;
    }

    public void cancel(Instant now) {
        if (status != OrderStatus.PENDING) throw new InvalidOrderTransitionException(status, OrderStatus.CANCELLED);
        status = OrderStatus.CANCELLED;
        updatedAt = now;
    }

    public Money total() {
        Money zero = new Money(java.math.BigDecimal.ZERO, items.getFirst().unitPrice().currency());
        return items.stream().map(OrderItem::subtotal).reduce(zero, Money::add);
    }

    public UUID id() { return id; }
    public String customerId() { return customerId; }
    public OrderStatus status() { return status; }
    public List<OrderItem> items() { return List.copyOf(items); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
