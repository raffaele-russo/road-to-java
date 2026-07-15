import java.util.Set;

public class Exercise {
    record Principal(String subject, Set<String> authorities) { }
    static int readStatus(Principal principal, String owner, boolean hideExistence) {
        throw new UnsupportedOperationException("TODO: distinguish authentication, authority, and ownership");
    }
    static String redact(String text) {
        throw new UnsupportedOperationException("TODO: remove bearer credentials");
    }
    public static void main(String[] args) {
        assert readStatus(null, "alice", false) == 401;
        assert readStatus(new Principal("alice", Set.of()), "alice", false) == 403;
        assert readStatus(new Principal("alice", Set.of("orders:read")), "bob", true) == 404;
        assert !redact("Authorization: Bearer abc.def.ghi").contains("abc.def.ghi");
    }
}
