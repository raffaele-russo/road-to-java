package com.roadtojava.orders.domain;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException() {
        super("idempotency key was already used with a different request");
    }
}
