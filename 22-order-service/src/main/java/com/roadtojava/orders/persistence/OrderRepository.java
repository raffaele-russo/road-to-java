package com.roadtojava.orders.persistence;

import com.roadtojava.orders.domain.Order;
import com.roadtojava.orders.domain.OrderStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    Page<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status, Pageable pageable);
}
