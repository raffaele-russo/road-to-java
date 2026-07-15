import java.time.Duration;

public class Exercise {
    record Problem(String type, int status, String detail) { }
    static Problem problemFor(String failure, String unsafeDetail) {
        throw new UnsupportedOperationException("TODO: stable public mapping without internal leakage");
    }
    static Duration boundedTimeout(Duration requested, Duration maximum) {
        throw new UnsupportedOperationException("TODO: reject non-positive and cap long timeouts");
    }
    public static void main(String[] args) {
        assert problemFor("invalid-state", "orders SQL failed").status() == 409;
        assert !problemFor("unknown", "password=secret").detail().contains("secret");
        assert boundedTimeout(Duration.ofSeconds(30), Duration.ofSeconds(5)).equals(Duration.ofSeconds(5));
    }
}
