package com.roadtojava.orders.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, Long> {
    Optional<IdempotencyRecord> findByPrincipalIdAndRequestKey(String principalId, String requestKey);
}
