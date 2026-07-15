import java.util.Set;

public class SecurityLab {
    record Principal(String subject, Set<String> authorities) { }
    record Order(String customerId) { }

    static boolean mayRead(Principal principal, Order order) {
        return principal != null && principal.authorities().contains("orders:read")
                && (principal.subject().equals(order.customerId()) || principal.authorities().contains("ROLE_ADMIN"));
    }

    static String safeHeader(String name, String value) {
        return name.equalsIgnoreCase("Authorization") ? "Authorization: [REDACTED]" : name + ": " + value;
    }

    public static void main(String[] args) {
        var alice = new Principal("alice", Set.of("orders:read"));
        assert mayRead(alice, new Order("alice"));
        assert !mayRead(alice, new Order("bob"));
        assert !safeHeader("Authorization", "Bearer secret").contains("secret");
        System.out.println("object authorization and redaction passed");
    }
}
