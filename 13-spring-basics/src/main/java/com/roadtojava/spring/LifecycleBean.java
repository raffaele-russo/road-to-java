package com.roadtojava.spring;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Bean lifecycle hooks: @PostConstruct runs once after all dependencies are
 * injected (init logic); @PreDestroy runs on graceful container shutdown
 * (cleanup logic) — the closest Spring equivalent to a C++ destructor, except
 * it only fires for singleton beans on an orderly context.close().
 */
@Component
public class LifecycleBean {
    private boolean initialized = false;

    @PostConstruct
    void init() {
        initialized = true;
        System.out.println("  [LifecycleBean] @PostConstruct: resources acquired");
    }

    @PreDestroy
    void cleanup() {
        System.out.println("  [LifecycleBean] @PreDestroy: resources released");
    }

    public boolean isInitialized() { return initialized; }
}
