package com.roadtojava.spring;

import org.springframework.stereotype.Component;

// @Component: registers this class as a Spring bean during component scanning.
// @Primary: when two beans implement MessageService, this one wins by default
// unless a @Qualifier picks the other one explicitly.
@Component
@org.springframework.context.annotation.Primary
public class EmailMessageService implements MessageService {
    @Override
    public String send(String to, String body) {
        return "EMAIL to " + to + ": " + body;
    }
}
