import java.util.Set;
import java.util.regex.Pattern;

public class Solution {
    record Principal(String subject, Set<String> authorities) { }
    static int readStatus(Principal principal, String owner, boolean hideExistence) {
        if (principal == null) return 401;
        if (!principal.authorities().contains("orders:read")) return 403;
        if (!principal.subject().equals(owner) && !principal.authorities().contains("ROLE_ADMIN"))
            return hideExistence ? 404 : 403;
        return 200;
    }
    static String redact(String text) {
        return Pattern.compile("(?i)Bearer\\s+[^\\s]+").matcher(text).replaceAll("Bearer [REDACTED]");
    }
    public static void main(String[] args) {
        assert readStatus(null, "alice", false) == 401;
        assert readStatus(new Principal("alice", Set.of()), "alice", false) == 403;
        assert readStatus(new Principal("alice", Set.of("orders:read")), "bob", true) == 404;
        assert !redact("Authorization: Bearer abc.def.ghi").contains("abc.def.ghi");
        System.out.println("security solution passed");
    }
}
