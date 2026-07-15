package com.roadtojava.orders.messaging;

import java.time.Clock;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "orders.consumer.enabled", havingValue = "true")
public class IdempotentOrderEventConsumer {
    private final ProcessedEventRepository processed;
    private final Clock clock;

    public IdempotentOrderEventConsumer(ProcessedEventRepository processed, Clock clock) {
        this.processed = processed;
        this.clock = clock;
    }

    @KafkaListener(topics = "order-events", groupId = "order-audit")
    @Transactional
    public void consume(String message) {
        UUID eventId = UUID.fromString(message.substring(0, message.indexOf('|')));
        if (processed.existsById(eventId)) return;
        try {
            // Apply the idempotent business effect in this same transaction.
            processed.saveAndFlush(new ProcessedEvent(eventId, clock.instant()));
        } catch (DataIntegrityViolationException duplicate) {
            // Another delivery/consumer completed the effect first.
        }
    }
}
