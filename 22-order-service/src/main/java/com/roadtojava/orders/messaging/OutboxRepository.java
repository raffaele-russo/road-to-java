package com.roadtojava.orders.messaging;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByPublishedAtIsNullOrderByOccurredAt(Pageable pageable);
}
