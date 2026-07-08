package com.roadtojava.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Clock;

public class Main {
    public static void main(String[] args) {
        // IoC container: builds the whole object graph from annotations, resolving
        // constructor dependencies, scopes, and @Value placeholders automatically.
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("== DI: constructor injection + @Qualifier ==");
            Greeter greeter = ctx.getBean(Greeter.class);
            System.out.println("  " + greeter.greet("ada@example.com"));

            System.out.println("\n== singleton scope (default): same instance every time ==");
            MessageService a = ctx.getBean(MessageService.class);  // @Primary -> EmailMessageService
            MessageService b = ctx.getBean(MessageService.class);
            System.out.println("  same instance: " + (a == b));

            System.out.println("\n== prototype scope: new instance every time ==");
            RequestContext r1 = ctx.getBean(RequestContext.class);
            RequestContext r2 = ctx.getBean(RequestContext.class);
            System.out.println("  r1 == r2 : " + (r1 == r2) + "  (" + r1.requestId() + " vs " + r2.requestId() + ")");

            System.out.println("\n== @Bean method (manually registered) ==");
            Clock clock = ctx.getBean(Clock.class);
            System.out.println("  clock bean instant: " + clock.instant());

            System.out.println("\n== lifecycle ==");
            LifecycleBean lifecycle = ctx.getBean(LifecycleBean.class);
            System.out.println("  initialized via @PostConstruct: " + lifecycle.isInitialized());
        } // ctx.close() here triggers @PreDestroy on all singleton beans
    }
}
