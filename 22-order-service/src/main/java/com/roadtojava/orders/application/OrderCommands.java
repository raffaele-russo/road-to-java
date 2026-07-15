package com.roadtojava.orders.application;

import com.roadtojava.orders.domain.Money;
import java.util.List;

public final class OrderCommands {
    private OrderCommands() {}
    public record Item(String sku, int quantity, Money unitPrice) {}
    public record Create(List<Item> items) {
        public Create { items = List.copyOf(items); }
    }
}
