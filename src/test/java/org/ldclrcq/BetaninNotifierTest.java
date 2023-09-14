package org.ldclrcq;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BetaninNotifierTest {
    @Test
    void Given_directory_name_should_notify_betanin() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
        stubFor(post(urlEqualTo("/api/torrents")).willReturn(aResponse().withStatus(200)));

        BetaninNotifier betaninNotifier = new BetaninNotifier("http://localhost:" + wireMockServer.port(), "abcd", "/mnt/complete");

        // ACT
        betaninNotifier.notifyBetanin(List.of("test1"));

        // ASSERT
        verify(postRequestedFor(
                urlEqualTo("/api/torrents"))
                .withHeader("X-API-Key", equalTo("abcd"))
                .withRequestBody(equalTo("path=%2Fmnt%2Fcomplete&name=test1"))
        );
        wireMockServer.stop();
    }
}
