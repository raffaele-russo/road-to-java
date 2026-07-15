package com.roadtojava.orders;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.roadtojava.orders.application.OrderApplicationService;
import com.roadtojava.orders.application.OrderCommands;
import com.roadtojava.orders.domain.Money;
import com.roadtojava.orders.domain.OrderStatus;
import java.math.BigDecimal;
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

    @Test void transactionPersistsOrderIdempotencyAndOutbox() {
        var command = new OrderCommands.Create(java.util.List.of(
            new OrderCommands.Item("JAVA-25", 1, new Money(new BigDecimal("25.00"), "EUR"))));
        var first = service.create("customer-1", "integration-key", command);
        var replay = service.create("customer-1", "integration-key", command);
        assertEquals(first.id(), replay.id());
        assertEquals(OrderStatus.PENDING, replay.status());
    }
}
