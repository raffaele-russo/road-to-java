package com.roadtojava.orders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.roadtojava.orders.application.OrderApplicationService;
import com.roadtojava.orders.application.OrderCommands;
import com.roadtojava.orders.domain.IdempotencyConflictException;
import com.roadtojava.orders.domain.Money;
import com.roadtojava.orders.domain.OrderStatus;
import com.roadtojava.orders.messaging.OutboxRepository;
import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class OrderServiceIntegrationTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", postgres::getJdbcUrl);
        properties.add("spring.datasource.username", postgres::getUsername);
        properties.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired OrderApplicationService service;
    @Autowired OutboxRepository outbox;

    @Test void transactionPersistsOrderIdempotencyAndOutbox() {
        var command = new OrderCommands.Create(java.util.List.of(
            new OrderCommands.Item("JAVA-25", 1, new Money(new BigDecimal("25.00"), "EUR"))));
        long outboxBefore = outbox.count();
        String key = "integration-key-" + java.util.UUID.randomUUID();
        var first = service.create("customer-1", key, command);
        var replay = service.create("customer-1", key, command);
        assertEquals(first.id(), replay.id());
        assertEquals(OrderStatus.PENDING, replay.status());
        assertEquals(outboxBefore + 1, outbox.count(), "a replay must not create another event");
    }

    @Test void rejectsIdempotencyKeyReuseWithDifferentCommand() {
        String key = "conflict-key-" + java.util.UUID.randomUUID();
        var first = new OrderCommands.Create(java.util.List.of(
            new OrderCommands.Item("JAVA-25", 1, new Money(new BigDecimal("25.00"), "EUR"))));
        var changed = new OrderCommands.Create(java.util.List.of(
            new OrderCommands.Item("JAVA-25", 2, new Money(new BigDecimal("25.00"), "EUR"))));
        service.create("customer-1", key, first);
        assertThrows(IdempotencyConflictException.class,
            () -> service.create("customer-1", key, changed));
    }

    @Test void enforcesOwnershipButAllowsExplicitAdminAccess() {
        String key = "owner-key-" + java.util.UUID.randomUUID();
        var command = new OrderCommands.Create(java.util.List.of(
            new OrderCommands.Item("JAVA-25", 1, new Money(new BigDecimal("25.00"), "EUR"))));
        var order = service.create("customer-1", key, command);
        assertThrows(AccessDeniedException.class,
            () -> service.get("customer-2", false, order.id()));
        assertEquals(order.id(), service.get("admin", true, order.id()).id());
    }
}
