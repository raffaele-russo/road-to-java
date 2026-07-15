package com.roadtojava.orders.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_records", uniqueConstraints =
    @UniqueConstraint(name = "uk_idempotency_principal_key", columnNames = {"principal_id", "request_key"}))
public class IdempotencyRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "principal_id", nullable = false, length = 200) private String principalId;
    @Column(name = "request_key", nullable = false, length = 100) private String requestKey;
    @Column(name = "request_hash", nullable = false, length = 64) private String requestHash;
    @Column(name = "order_id", nullable = false) private UUID orderId;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected IdempotencyRecord() {}
    public IdempotencyRecord(String principalId, String requestKey, String requestHash, UUID orderId, Instant createdAt) {
        this.principalId = principalId;
        this.requestKey = requestKey;
        this.requestHash = requestHash;
        this.orderId = orderId;
        this.createdAt = createdAt;
    }
    public String requestHash() { return requestHash; }
    public UUID orderId() { return orderId; }
}
