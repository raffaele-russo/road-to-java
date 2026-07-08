package com.roadtojava.spring;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Scope("prototype"): a NEW instance is created every time this bean is
 * requested from the container, unlike the default "singleton" scope (one
 * shared instance for the whole application). Useful for stateful,
 * per-request/per-use objects.
 */
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.NO)
public class RequestContext {
    private final String requestId = UUID.randomUUID().toString();
    public String requestId() { return requestId; }
}
