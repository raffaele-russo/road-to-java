package com.roadtojava.orders.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.roadtojava.orders.application.OrderApplicationService;
import com.roadtojava.orders.domain.Money;
import com.roadtojava.orders.domain.Order;
import com.roadtojava.orders.domain.OrderItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.roadtojava.orders.config.SecurityConfig;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean OrderApplicationService service;

    @Test void mapsDomainOrderToStableJsonDto() throws Exception {
        UUID id = UUID.randomUUID();
        Order order = new Order(id, "customer-1",
            List.of(new OrderItem("JAVA-25", 2, new Money(new BigDecimal("19.95"), "EUR"))),
            Instant.parse("2026-07-15T10:00:00Z"));
        when(service.get("customer-1", false, id)).thenReturn(order);
        mvc.perform(get("/api/orders/{id}", id).with(jwt().jwt(token -> token.subject("customer-1"))
                .authorities(new SimpleGrantedAuthority("SCOPE_orders:read"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.total.amount").value(39.90));
    }

    @Test void rejectsMissingBearerToken() throws Exception {
        mvc.perform(get("/api/orders/{id}", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    @Test void rejectsTokenWithoutReadScope() throws Exception {
        mvc.perform(get("/api/orders/{id}", UUID.randomUUID()).with(jwt()))
            .andExpect(status().isForbidden());
    }
}
