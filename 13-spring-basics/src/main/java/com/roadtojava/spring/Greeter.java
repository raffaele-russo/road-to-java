package com.roadtojava.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Constructor injection (recommended over field @Autowired): Spring calls this
 * constructor to build the bean, resolving MessageService and appName itself.
 * Constructor injection makes dependencies explicit, final, and testable with
 * plain `new Greeter(fake, "x")` — no Spring required to unit test it.
 */
@Component
public class Greeter {
    private final MessageService messageService;
    private final String appName;

    public Greeter(@Qualifier("sms") MessageService messageService,
                    @Value("${app.name:RoadToJava}") String appName) {
        this.messageService = messageService;
        this.appName = appName;
    }

    public String greet(String to) {
        return messageService.send(to, "Welcome to " + appName + "!");
    }
}
