package com.roadtojava.orders.application;

import com.roadtojava.orders.domain.IdempotencyConflictException;
import com.roadtojava.orders.domain.Order;
import com.roadtojava.orders.domain.OrderItem;
import com.roadtojava.orders.domain.OrderNotFoundException;
import com.roadtojava.orders.domain.OrderStatus;
import com.roadtojava.orders.messaging.OutboxEvent;
import com.roadtojava.orders.messaging.OutboxRepository;
import com.roadtojava.orders.persistence.IdempotencyRecord;
import com.roadtojava.orders.persistence.IdempotencyRepository;
import com.roadtojava.orders.persistence.OrderRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderApplicationService {
    private final OrderRepository orders;
    private final IdempotencyRepository idempotency;
    private final OutboxRepository outbox;
    private final Clock clock;

    public OrderApplicationService(OrderRepository orders, IdempotencyRepository idempotency,
                                   OutboxRepository outbox, Clock clock) {
        this.orders = orders;
        this.idempotency = idempotency;
        this.outbox = outbox;
        this.clock = clock;
    }

    @Transactional
    public Order create(String principal, String key, OrderCommands.Create command) {
        if (key == null || key.isBlank() || key.length() > 100)
            throw new IllegalArgumentException("Idempotency-Key must contain 1..100 characters");
        String hash = hash(command.toString());
        var existing = idempotency.findByPrincipalIdAndRequestKey(principal, key);
        if (existing.isPresent()) {
            if (!existing.get().requestHash().equals(hash)) throw new IdempotencyConflictException();
            return orders.findById(existing.get().orderId())
                .orElseThrow(() -> new OrderNotFoundException(existing.get().orderId()));
        }
        var items = command.items().stream()
            .map(item -> new OrderItem(item.sku(), item.quantity(), item.unitPrice())).toList();
        var now = clock.instant();
        Order order = orders.save(new Order(UUID.randomUUID(), principal, items, now));
        idempotency.save(new IdempotencyRecord(principal, key, hash, order.id(), now));
        outbox.save(new OutboxEvent(order.id(), "OrderCreated", "{\"orderId\":\"" + order.id() + "\"}", now));
        return order;
    }

    @Transactional(readOnly = true)
    public Order get(String principal, boolean admin, UUID id) {
        Order order = orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        authorize(principal, admin, order);
        return order;
    }

    @Transactional(readOnly = true)
    public Page<Order> list(String principal, OrderStatus status, int page, int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("invalid page or size");
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by("id")));
        return status == null ? orders.findByCustomerId(principal, pageable)
            : orders.findByCustomerIdAndStatus(principal, status, pageable);
    }

    @Transactional
    public Order confirm(String principal, boolean admin, UUID id) {
        Order order = get(principal, admin, id);
        order.confirm(clock.instant());
        outbox.save(new OutboxEvent(order.id(), "OrderConfirmed", "{\"orderId\":\"" + id + "\"}", clock.instant()));
        return order;
    }

    @Transactional
    public Order cancel(String principal, boolean admin, UUID id) {
        Order order = get(principal, admin, id);
        order.cancel(clock.instant());
        outbox.save(new OutboxEvent(order.id(), "OrderCancelled", "{\"orderId\":\"" + id + "\"}", clock.instant()));
        return order;
    }

    private static void authorize(String principal, boolean admin, Order order) {
        if (!admin && !order.customerId().equals(principal)) throw new AccessDeniedException("order is not owned by caller");
    }

    private static String hash(String input) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }
}
