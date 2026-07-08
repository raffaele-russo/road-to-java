/**
 * Facade: hide a set of interacting subsystems behind one simplified entry point.
 * The subsystems remain fully usable on their own — the facade just spares most
 * clients from having to know about all of them.
 * Run:  java 11-design-patterns/Facade.java
 */
public class Facade {

    // Three subsystems a client would otherwise have to coordinate by hand.
    static class InventoryService {
        boolean reserve(String sku, int qty) {
            System.out.println("  [inventory] reserved " + qty + "x " + sku);
            return true;
        }
    }

    static class PaymentService {
        boolean charge(String customerId, double amount) {
            System.out.println("  [payment] charged $" + amount + " to " + customerId);
            return true;
        }
    }

    static class ShippingService {
        void schedule(String sku, String address) {
            System.out.println("  [shipping] scheduled " + sku + " -> " + address);
        }
    }

    // The facade: one call, three subsystems coordinated behind it.
    static class OrderFacade {
        private final InventoryService inventory = new InventoryService();
        private final PaymentService payment = new PaymentService();
        private final ShippingService shipping = new ShippingService();

        boolean placeOrder(String customerId, String sku, int qty, double amount, String address) {
            if (!inventory.reserve(sku, qty)) return false;
            if (!payment.charge(customerId, amount)) return false;
            shipping.schedule(sku, address);
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("== facade ==");
        OrderFacade orders = new OrderFacade();
        boolean ok = orders.placeOrder("cust-42", "SKU-100", 2, 39.98, "1 Main St");
        System.out.println("  order placed: " + ok);
        System.out.println("  client only ever talked to OrderFacade, never to the three subsystems");
    }
}
