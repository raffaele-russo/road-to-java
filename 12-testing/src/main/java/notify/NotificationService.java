package notify;

/** Depends on the EmailSender interface, not a concrete implementation — this
 * is exactly what makes it mockable in isolation from the real email system. */
public class NotificationService {
    private final EmailSender sender;

    public NotificationService(EmailSender sender) {
        this.sender = sender;
    }

    public boolean notifyUser(String email, String eventName) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("invalid email: " + email);
        }
        return sender.send(email, "Notification", "Event occurred: " + eventName);
    }
}
