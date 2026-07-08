package com.roadtojava.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Proves the wiring actually works: the context boots, constructor injection
 * resolves the @Qualifier("sms") bean, singleton scope returns one shared
 * instance, and prototype scope returns a fresh instance each call.
 * Run: mvn test
 */
class SpringContextTest {

    @Test
    void contextLoadsAndWiresGreeter() {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            Greeter greeter = ctx.getBean(Greeter.class);
            String result = greeter.greet("ada@example.com");
            assertTrue(result.startsWith("SMS to"), "constructor @Qualifier(\"sms\") should win over @Primary");
        }
    }

    @Test
    void singletonScopeReturnsSameInstance() {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            MessageService a = ctx.getBean(MessageService.class);
            MessageService b = ctx.getBean(MessageService.class);
            assertSame(a, b);
            assertInstanceOf(EmailMessageService.class, a, "@Primary should resolve the ambiguous type request");
        }
    }

    @Test
    void prototypeScopeReturnsNewInstanceEachTime() {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            RequestContext r1 = ctx.getBean(RequestContext.class);
            RequestContext r2 = ctx.getBean(RequestContext.class);
            assertNotSame(r1, r2);
            assertNotEquals(r1.requestId(), r2.requestId());
        }
    }

    @Test
    void postConstructRunsBeforeBeanIsUsable() {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            LifecycleBean bean = ctx.getBean(LifecycleBean.class);
            assertTrue(bean.isInitialized());
        }
    }
}
