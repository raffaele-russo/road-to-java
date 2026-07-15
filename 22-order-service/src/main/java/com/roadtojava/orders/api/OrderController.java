package com.roadtojava.orders.api;

import com.roadtojava.orders.api.OrderDtos.CreateOrderRequest;
import com.roadtojava.orders.api.OrderDtos.OrderResponse;
import com.roadtojava.orders.api.OrderDtos.PageResponse;
import com.roadtojava.orders.application.OrderApplicationService;
import com.roadtojava.orders.domain.OrderStatus;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderApplicationService service;
    public OrderController(OrderApplicationService service) { this.service = service; }

    @PostMapping
    ResponseEntity<OrderResponse> create(Authentication auth,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = OrderResponse.from(service.create(auth.getName(), idempotencyKey, request.toCommand()));
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    OrderResponse get(Authentication auth, @PathVariable UUID id) {
        return OrderResponse.from(service.get(auth.getName(), isAdmin(auth), id));
    }

    @GetMapping
    PageResponse<OrderResponse> list(Authentication auth,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = service.list(auth.getName(), status, page, size);
        return new PageResponse<>(result.map(OrderResponse::from).getContent(),
            result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @PostMapping("/{id}/confirm")
    OrderResponse confirm(Authentication auth, @PathVariable UUID id) {
        return OrderResponse.from(service.confirm(auth.getName(), isAdmin(auth), id));
    }

    @PostMapping("/{id}/cancel")
    OrderResponse cancel(Authentication auth, @PathVariable UUID id) {
        return OrderResponse.from(service.cancel(auth.getName(), isAdmin(auth), id));
    }

    private static boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
