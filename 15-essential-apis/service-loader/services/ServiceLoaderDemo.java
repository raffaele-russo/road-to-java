package services;

import java.util.ServiceLoader;

public class ServiceLoaderDemo {
    public static void main(String[] args) {
        GreetingProvider provider = ServiceLoader.load(GreetingProvider.class)
                .findFirst().orElseThrow();
        assert provider.language().equals("en");
        assert provider.greet("Ada").equals("Hello Ada");
        System.out.println("ServiceLoader provider discovery passed.");
    }
}
