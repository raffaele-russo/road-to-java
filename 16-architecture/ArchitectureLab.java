import java.util.List;

public class ArchitectureLab {
    enum Status { PENDING, CONFIRMED }

    static final class Order {
        private final List<String> items;
        private Status status = Status.PENDING;

        Order(List<String> items) {
            if (items.isEmpty()) throw new IllegalArgumentException("items");
            this.items = List.copyOf(items);
        }

        List<String> items() { return items; }
        Status status() { return status; }
        void confirm() {
            if (status != Status.PENDING) throw new IllegalStateException("terminal order");
            status = Status.CONFIRMED;
        }
    }

    public static void main(String[] args) {
        var source = new java.util.ArrayList<>(List.of("book"));
        var order = new Order(source);
        source.add("hidden mutation");
        assert order.items().equals(List.of("book"));
        order.confirm();
        assert order.status() == Status.CONFIRMED;
        try { order.confirm(); assert false; } catch (IllegalStateException expected) { }
        System.out.println("invariants and ownership checks passed");
    }
}
