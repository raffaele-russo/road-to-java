package com.roadtojava.orders.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {
    private Order pending() {
        return new Order(UUID.randomUUID(), "customer-1",
            List.of(new OrderItem("JAVA-25", 2, new Money(new BigDecimal("19.95"), "EUR"))),
            Instant.parse("2026-07-15T10:00:00Z"));
    }

    @Test void computesDecimalTotalWithoutBinaryFloatingPoint() {
        assertEquals(new BigDecimal("39.90"), pending().total().amount());
    }

    @Test void onlyPendingOrderCanBeConfirmed() {
        Order order = pending();
        order.confirm(Instant.parse("2026-07-15T11:00:00Z"));
        assertEquals(OrderStatus.CONFIRMED, order.status());
        assertThrows(InvalidOrderTransitionException.class,
            () -> order.cancel(Instant.parse("2026-07-15T12:00:00Z")));
    }

    @Test void rejectsMixedCurrencies() {
        assertThrows(IllegalArgumentException.class, () -> new Order(UUID.randomUUID(), "customer-1",
            List.of(new OrderItem("A", 1, new Money(BigDecimal.ONE, "EUR")),
                    new OrderItem("B", 1, new Money(BigDecimal.ONE, "USD"))), Instant.now()));
    }
}
