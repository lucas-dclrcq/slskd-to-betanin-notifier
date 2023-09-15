package org.ldclrcq.betanin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ldclrcq.betanin.DTOs.BetaninTorrentDTO;
import org.ldclrcq.betanin.DTOs.BetaninTorrentResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public record BetaninNotifier(String betaninUrl, String betaninApiKey, String betaninCompleteFolderPath) {
    private final static String XApiKeyHeaderKey = "X-API-Key";

    public void notifyBetanin(List<String> musicDirectories) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Notifying betanin of new files...");
        System.out.println("---------------------------------");

        var alreadyImportedDirectories = this.getAlreadyImportedDirectories();

        for (var musicDirectory : musicDirectories) {
            if (alreadyImportedDirectories.contains(musicDirectory)) {
                System.out.printf("Directory %s was already imported in betanin: skipping%n", musicDirectory);
                continue;
            }

            notifyBetanin(musicDirectory);
        }

        System.out.println();
    }

    public void notifyBetanin(String musicDirectory) throws URISyntaxException, IOException, InterruptedException {
        System.out.printf("Notifying betanin for directory : %s%n", musicDirectory);

        var parameters = new HashMap<String, String>();
        parameters.put("path", betaninCompleteFolderPath);
        parameters.put("name", musicDirectory);

        var form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        var endpoint = new URI(betaninUrl).resolve("/api/torrents");

        var postRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header(XApiKeyHeaderKey, betaninApiKey)
                .uri(endpoint).POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    private List<String> getAlreadyImportedDirectories() throws URISyntaxException, IOException, InterruptedException {
        var endpoint = new URI(betaninUrl).resolve("/api/torrents?page=1&per_page=25");

        var getRequest = HttpRequest.newBuilder()
                .header(XApiKeyHeaderKey, betaninApiKey)
                .uri(endpoint).GET()
                .build();

        var body = HttpClient.newBuilder()
                .build()
                .send(getRequest, HttpResponse.BodyHandlers.ofString())
                .body();

        var betaninTorrentResponse = new ObjectMapper().readValue(body, BetaninTorrentResponse.class);

        System.out.printf("Fetched %d imported directories from betanin%n", betaninTorrentResponse.torrents().size());

        return betaninTorrentResponse.torrents().stream().map(BetaninTorrentDTO::name).toList();
    }
}
