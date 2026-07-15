import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Proxy;

public class Metadata {
    @Retention(RetentionPolicy.RUNTIME)
    @interface Audited { String action(); }

    interface Greeting { String hello(String name); }

    @Audited(action = "greet")
    static class GreetingService implements Greeting {
        public String hello(String name) { return "Hello " + name; }
    }

    public static void main(String[] args) {
        Audited metadata = GreetingService.class.getAnnotation(Audited.class);
        assert metadata.action().equals("greet");
        Greeting target = new GreetingService();
        Greeting proxy = (Greeting) Proxy.newProxyInstance(
            Greeting.class.getClassLoader(), new Class<?>[]{Greeting.class},
            (ignored, method, arguments) -> method.invoke(target, arguments));
        assert proxy.hello("Ada").equals("Hello Ada");
        System.out.println("Runtime metadata and proxy contracts passed.");
    }
}
