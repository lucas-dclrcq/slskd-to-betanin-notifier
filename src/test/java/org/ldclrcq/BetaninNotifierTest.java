package org.ldclrcq;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldclrcq.betanin.BetaninNotifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BetaninNotifierTest {

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
    void Given_directory_name_should_notify_betanin() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
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
    }

    @Test
    void Given_already_completed_import_should_not_notify_betanin() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
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
    }

    @Test
    void Given_waiting_for_input_import_should_remove_and_renotify() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
        stubFor(post(urlEqualTo("/api/torrents")).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlEqualTo("/api/torrents?page=1&per_page=25")).willReturn(aResponse().withStatus(200).withBody("""
                {
                    "total": 55,
                    "torrents": [
                        {
                            "id": "56",
                            "path": "/downloads",
                            "status": "NEEDS_INPUT",
                            "name": "test1",
                            "has_lines": true,
                            "tooltip": null,
                            "updated": "2023-09-15T07:06:17",
                            "created": "2023-09-15T07:00:03"
                        }
                    ]
                }          
                """)));
        stubFor(delete(urlEqualTo("/api/torrents/56")).willReturn(aResponse().withStatus(200)));

        BetaninNotifier betaninNotifier = new BetaninNotifier("http://localhost:%d".formatted(wireMockServer.port()), "abcd", "/mnt/complete");

        // ACT
        betaninNotifier.notifyBetanin(List.of("test1"));

        // ASSERT
        verify(deleteRequestedFor(
                        urlEqualTo("/api/torrents/56"))
                        .withHeader("X-API-Key", equalTo("abcd")));
        verify(postRequestedFor(
                urlEqualTo("/api/torrents"))
                .withHeader("X-API-Key", equalTo("abcd"))
                .withRequestBody(equalTo("path=%2Fmnt%2Fcomplete&name=test1"))
        );
    }

    @Test
    void Given_two_directories_should_return_notified_one() throws URISyntaxException, IOException, InterruptedException {
        // ARRANGE
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
        List<String> notifiedDirectories = betaninNotifier.notifyBetanin(List.of("test1", "test2"));

        // ASSERT
        assertThat(notifiedDirectories).containsExactlyInAnyOrder("test2");
    }
}
