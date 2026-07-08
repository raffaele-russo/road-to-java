/**
 * Factory Method: defer object creation to a dedicated method instead of the
 * client calling `new` on concrete classes directly.
 * Run:  java 11-design-patterns/FactoryMethod.java
 */
public class FactoryMethod {

    sealed interface Notification permits EmailNotification, SmsNotification {
        void send(String message);
    }
    record EmailNotification(String address) implements Notification {
        public void send(String message) { System.out.println("  Email to " + address + ": " + message); }
    }
    record SmsNotification(String phone) implements Notification {
        public void send(String message) { System.out.println("  SMS to " + phone + ": " + message); }
    }

    enum NotificationType { EMAIL, SMS }

    // The factory method: callers never see EmailNotification/SmsNotification directly.
    static Notification create(NotificationType type, String destination) {
        return switch (type) {                                   // exhaustive, sealed
            case EMAIL -> new EmailNotification(destination);
            case SMS -> new SmsNotification(destination);
        };
    }

    public static void main(String[] args) {
        System.out.println("== factory method ==");
        Notification n1 = create(NotificationType.EMAIL, "ada@example.com");
        Notification n2 = create(NotificationType.SMS, "+1-555-0100");
        n1.send("Your interview is confirmed.");
        n2.send("Your interview is confirmed.");
        System.out.println("  client code never called `new EmailNotification(...)` directly");
    }
}
