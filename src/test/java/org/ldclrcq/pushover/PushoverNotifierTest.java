package org.ldclrcq.pushover;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldclrcq.betanin.BetaninNotifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class PushoverNotifierTest {
    WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void Given_imported_directory_should_notify_pushover() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
        stubFor(post(urlEqualTo("/1/messages.json")).willReturn(aResponse().withStatus(200)));
        PushoverNotifier pushoverNotifier = new PushoverNotifier(wireMockServer.url(""), "someToken", "someUser", "someDevice");

        // ACT
        pushoverNotifier.notifyImportedDirectories(List.of("[Test] Machin"));

        // ASSERT
        verify(postRequestedFor(
                urlEqualTo("/1/messages.json"))
                .withRequestBody(equalTo("title=New+music+imported%3A+%5BTest%5D+Machin&message=A+music+directory+was+successfully+notified+to+betanin%3A+%5BTest%5D+Machin&user=someUser&device=someDevice&token=someToken"))
        );
    }
}
