package services;

public final class EnglishGreeting implements GreetingProvider {
    public String language() { return "en"; }
    public String greet(String name) { return "Hello " + name; }
}
