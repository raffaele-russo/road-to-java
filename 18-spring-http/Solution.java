import java.time.Duration;

public class Solution {
    record Problem(String type, int status, String detail) { }
    static Problem problemFor(String failure, String unsafeDetail) {
        return switch (failure) {
            case "invalid-input" -> new Problem("invalid-input", 400, "Request is invalid");
            case "not-found" -> new Problem("not-found", 404, "Resource was not found");
            case "invalid-state" -> new Problem("conflict", 409, "State transition is not allowed");
            default -> new Problem("internal", 500, "Unexpected failure");
        };
    }
    static Duration boundedTimeout(Duration requested, Duration maximum) {
        if (requested.isZero() || requested.isNegative() || maximum.isZero() || maximum.isNegative())
            throw new IllegalArgumentException("timeout");
        return requested.compareTo(maximum) > 0 ? maximum : requested;
    }
    public static void main(String[] args) {
        assert problemFor("invalid-state", "orders SQL failed").status() == 409;
        assert !problemFor("unknown", "password=secret").detail().contains("secret");
        assert boundedTimeout(Duration.ofSeconds(30), Duration.ofSeconds(5)).equals(Duration.ofSeconds(5));
        System.out.println("HTTP solution passed");
    }
}
