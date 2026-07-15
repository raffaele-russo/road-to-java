package com.roadtojava.orders.messaging;

import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "orders.outbox.enabled", havingValue = "true")
public class OutboxPublisher {
    private final OutboxRepository events;
    private final KafkaTemplate<String, String> kafka;
    private final Clock clock;

    public OutboxPublisher(OutboxRepository events, KafkaTemplate<String, String> kafka, Clock clock) {
        this.events = events;
        this.kafka = kafka;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${orders.outbox.delay:1000}")
    public void publishBatch() {
        for (OutboxEvent event : events.findByPublishedAtIsNullOrderByOccurredAt(PageRequest.of(0, 100))) {
            kafka.send("order-events", event.aggregateId().toString(),
                event.id() + "|" + event.eventType() + "|" + event.payload()).join();
            event.markPublished(clock.instant());
            events.save(event);
        }
    }
}
