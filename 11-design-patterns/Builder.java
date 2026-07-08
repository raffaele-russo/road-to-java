/**
 * Builder: construct a complex immutable object step by step, avoiding
 * telescoping constructors (Java has no named/default parameters).
 * Run:  java 11-design-patterns/Builder.java
 */
public class Builder {

    static final class HttpRequest {
        private final String url;            // required
        private final String method;         // defaulted
        private final int timeoutMs;         // defaulted
        private final boolean followRedirects; // defaulted

        private HttpRequest(RequestBuilder b) {
            this.url = b.url;
            this.method = b.method;
            this.timeoutMs = b.timeoutMs;
            this.followRedirects = b.followRedirects;
        }

        @Override public String toString() {
            return "HttpRequest{" + method + " " + url + ", timeout=" + timeoutMs
                 + "ms, followRedirects=" + followRedirects + "}";
        }

        static RequestBuilder builder(String url) { return new RequestBuilder(url); }

        static final class RequestBuilder {
            private final String url;
            private String method = "GET";           // sensible defaults
            private int timeoutMs = 5000;
            private boolean followRedirects = true;

            private RequestBuilder(String url) {
                if (url == null || url.isBlank())
                    throw new IllegalArgumentException("url is required");  // validate at build time
                this.url = url;
            }

            RequestBuilder method(String method) { this.method = method; return this; }   // fluent
            RequestBuilder timeoutMs(int ms) { this.timeoutMs = ms; return this; }
            RequestBuilder followRedirects(boolean follow) { this.followRedirects = follow; return this; }

            HttpRequest build() { return new HttpRequest(this); }
        }
    }

    public static void main(String[] args) {
        System.out.println("== builder ==");
        HttpRequest req = HttpRequest.builder("https://api.example.com/users")
            .method("POST")
            .timeoutMs(2000)
            .build();                          // followRedirects left at default: true
        System.out.println("  " + req);

        try {
            HttpRequest.builder(" ").build();
        } catch (IllegalArgumentException e) {
            System.out.println("  validation on build(): " + e.getMessage());
        }
    }
}
