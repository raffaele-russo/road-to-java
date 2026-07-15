package services;

public interface GreetingProvider {
    String language();
    String greet(String name);
}
