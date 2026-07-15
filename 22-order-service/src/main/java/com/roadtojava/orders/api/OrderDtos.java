package com.roadtojava.orders.api;

import com.roadtojava.orders.application.OrderCommands;
import com.roadtojava.orders.domain.Money;
import com.roadtojava.orders.domain.Order;
import com.roadtojava.orders.domain.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class OrderDtos {
    private OrderDtos() {}

    public record CreateOrderRequest(@NotEmpty List<@Valid ItemRequest> items) {
        OrderCommands.Create toCommand() {
            return new OrderCommands.Create(items.stream().map(ItemRequest::toCommand).toList());
        }
    }

    public record ItemRequest(
        @NotBlank String sku,
        @Positive int quantity,
        @DecimalMin("0.00") BigDecimal unitPrice,
        @Pattern(regexp = "[A-Z]{3}") String currency) {
        OrderCommands.Item toCommand() {
            return new OrderCommands.Item(sku, quantity, new Money(unitPrice, currency));
        }
    }

    public record ItemResponse(String sku, int quantity, Money unitPrice, Money subtotal) {}

    public record OrderResponse(UUID id, String customerId, OrderStatus status,
                                List<ItemResponse> items, Money total, long version,
                                Instant createdAt, Instant updatedAt) {
        static OrderResponse from(Order order) {
            return new OrderResponse(order.id(), order.customerId(), order.status(),
                order.items().stream().map(item -> new ItemResponse(
                    item.sku(), item.quantity(), item.unitPrice(), item.subtotal())).toList(),
                order.total(), order.version(), order.createdAt(), order.updatedAt());
        }
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}
}
