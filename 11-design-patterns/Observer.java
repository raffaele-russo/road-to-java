import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Observer: one subject, many observers notified on state change (pub/sub).
 * java.util.Observable is deprecated — this is the idiomatic hand-rolled form,
 * the same shape Spring's ApplicationEventPublisher builds on.
 * Run:  java 11-design-patterns/Observer.java
 */
public class Observer {

    interface StockObserver {
        void onPriceChanged(String ticker, double price);
    }

    static class StockTicker {
        private final List<StockObserver> observers = new ArrayList<>();
        private double price;

        void subscribe(StockObserver o) { observers.add(o); }
        void unsubscribe(StockObserver o) { observers.remove(o); }

        void setPrice(String ticker, double newPrice) {
            this.price = newPrice;
            for (StockObserver o : observers) o.onPriceChanged(ticker, newPrice); // notify all
        }
    }

    public static void main(String[] args) {
        System.out.println("== observer / pub-sub ==");
        StockTicker aapl = new StockTicker();

        StockObserver logger = (ticker, price) -> System.out.println("  [log] " + ticker + " = $" + price);
        StockObserver alert = (ticker, price) -> {
            if (price > 200) System.out.println("  [ALERT] " + ticker + " crossed $200!");
        };

        aapl.subscribe(logger);
        aapl.subscribe(alert);
        aapl.setPrice("AAPL", 195.0);
        aapl.setPrice("AAPL", 205.0);   // triggers the alert too

        aapl.unsubscribe(logger);
        System.out.println("  -- unsubscribed logger --");
        aapl.setPrice("AAPL", 210.0);   // only alert fires now

        System.out.println("\n  functional-interface observers (Consumer) work the same way:");
        List<Consumer<String>> subs = List.of(
            s -> System.out.println("  sub1 got: " + s),
            s -> System.out.println("  sub2 got: " + s));
        subs.forEach(sub -> sub.accept("event"));
    }
}
