package com.roadtojava.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("sms")   // explicit name so this bean can be requested over the @Primary one
public class SmsMessageService implements MessageService {
    @Override
    public String send(String to, String body) {
        return "SMS to " + to + ": " + body;
    }
}
