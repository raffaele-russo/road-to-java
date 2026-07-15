package com.roadtojava.orders.api;

import com.roadtojava.orders.domain.IdempotencyConflictException;
import com.roadtojava.orders.domain.InvalidOrderTransitionException;
import com.roadtojava.orders.domain.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail notFound(OrderNotFoundException error, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Order not found", error, request);
    }

    @ExceptionHandler({InvalidOrderTransitionException.class, IdempotencyConflictException.class,
                       OptimisticLockingFailureException.class})
    ProblemDetail conflict(RuntimeException error, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "Order conflict", error, request);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    ProblemDetail badRequest(RuntimeException error, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", error, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException error, HttpServletRequest request) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Validation failed", error, request);
        detail.setProperty("violations", error.getBindingResult().getFieldErrors().stream()
            .map(f -> Map.of("field", f.getField(), "message", String.valueOf(f.getDefaultMessage()))).toList());
        return detail;
    }

    private static ProblemDetail problem(HttpStatus status, String title, Exception error,
                                         HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, error.getMessage());
        detail.setTitle(title);
        detail.setType(URI.create("https://road-to-java.dev/problems/" + status.value()));
        detail.setInstance(URI.create(request.getRequestURI()));
        return detail;
    }
}
