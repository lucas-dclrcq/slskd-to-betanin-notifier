package org.ldclrcq;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.ldclrcq.betanin.BetaninNotifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BetaninNotifierTest {
    @Test
    void Given_directory_name_should_notify_betanin() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
        stubFor(post(urlEqualTo("/api/torrents")).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlEqualTo("/api/torrents?page=1&per_page=25")).willReturn(aResponse().withStatus(200).withBody("""
                {
                    "total": 55,
                    "torrents": []
                }           
                """)));
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

    @Test
    void Given_already_notified_directory_should_not_notify_betanin() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
        stubFor(post(urlEqualTo("/api/torrents")).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlEqualTo("/api/torrents?page=1&per_page=25")).willReturn(aResponse().withStatus(200).withBody("""
                {
                    "total": 55,
                    "torrents": [
                        {
                            "id": "56",
                            "path": "/downloads",
                            "status": "COMPLETED",
                            "name": "test1",
                            "has_lines": true,
                            "tooltip": null,
                            "updated": "2023-09-15T07:06:17",
                            "created": "2023-09-15T07:00:03"
                        }
                    ]
                }           
                """)));

        BetaninNotifier betaninNotifier = new BetaninNotifier("http://localhost:%d".formatted(wireMockServer.port()), "abcd", "/mnt/complete");

        // ACT
        betaninNotifier.notifyBetanin(List.of("test1"));

        // ASSERT
        verify(0, postRequestedFor(urlEqualTo("/api/torrents")));
        wireMockServer.stop();
    }
}
