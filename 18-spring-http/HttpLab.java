import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class HttpLab {
    record Problem(String type, String title, int status, String detail) { }

    static Problem mapFailure(RuntimeException failure) {
        if (failure instanceof IllegalArgumentException) return new Problem("invalid-input", "Bad request", 400, failure.getMessage());
        if (failure instanceof IllegalStateException) return new Problem("conflict", "Conflict", 409, failure.getMessage());
        return new Problem("internal", "Internal error", 500, "Unexpected failure");
    }

    public static void main(String[] args) {
        var request = HttpRequest.newBuilder(URI.create("https://example.invalid/orders"))
                .timeout(Duration.ofSeconds(2)).GET().build();
        assert request.timeout().orElseThrow().equals(Duration.ofSeconds(2));
        assert mapFailure(new IllegalStateException("already confirmed")).status() == 409;
        assert !mapFailure(new RuntimeException("secret SQL")).detail().contains("SQL");
        System.out.println("bounded HTTP request and stable problem mapping passed");
    }
}
