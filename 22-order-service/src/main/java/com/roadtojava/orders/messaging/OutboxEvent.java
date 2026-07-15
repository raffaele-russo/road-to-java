package com.roadtojava.orders.messaging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id private UUID id;
    @Column(name = "aggregate_id", nullable = false) private UUID aggregateId;
    @Column(name = "event_type", nullable = false, length = 100) private String eventType;
    @Column(nullable = false, columnDefinition = "text") private String payload;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    @Column(name = "published_at") private Instant publishedAt;

    protected OutboxEvent() {}
    public OutboxEvent(UUID aggregateId, String eventType, String payload, Instant occurredAt) {
        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }
    public UUID id() { return id; }
    public UUID aggregateId() { return aggregateId; }
    public String eventType() { return eventType; }
    public String payload() { return payload; }
    public void markPublished(Instant now) { publishedAt = now; }
}
