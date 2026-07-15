package com.roadtojava.orders.domain;

public class InvalidOrderTransitionException extends RuntimeException {
    public InvalidOrderTransitionException(OrderStatus from, OrderStatus to) {
        super("cannot transition order from " + from + " to " + to);
    }
}
