package notify;

public interface EmailSender {
    boolean send(String to, String subject, String body);
}
