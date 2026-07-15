package client;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusClientTest {
    private WireMockServer server;
    private StatusClient client;
    @BeforeEach void start() {
        server = new WireMockServer(0);
        server.start();
        client = new StatusClient(URI.create(server.baseUrl()), HttpClient.newHttpClient());
    }
    @AfterEach void stop() { server.stop(); }
    @Test void mapsSuccessfulResponse() throws Exception {
        server.stubFor(get(urlEqualTo("/status")).willReturn(ok("ready")));
        assertEquals("ready", client.status());
    }
    @Test void rejectsServerFailure() {
        server.stubFor(get(urlEqualTo("/status")).willReturn(serverError()));
        assertThrows(IOException.class, client::status);
    }
}
