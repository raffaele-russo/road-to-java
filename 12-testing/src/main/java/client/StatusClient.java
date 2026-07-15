package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class StatusClient {
    private final URI baseUri;
    private final HttpClient client;
    public StatusClient(URI baseUri, HttpClient client) { this.baseUri = baseUri; this.client = client; }
    public String status() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(baseUri.resolve("/status"))
            .timeout(Duration.ofSeconds(1)).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("unexpected HTTP " + response.statusCode());
        return response.body();
    }
}
