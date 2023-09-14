package org.ldclrcq;

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
    public void notifyBetanin(List<String> musicDirectories) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Notifying betanin of new files...");
        System.out.println("---------------------------------");

        for (var musicDirectory : musicDirectories) {
            notifyBetanin(musicDirectory);
        }

        System.out.println();
    }

    public void notifyBetanin(String musicDirectory) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Notifying betanin for directory : " + musicDirectory);

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
                .header("X-API-Key", betaninApiKey)
                .uri(endpoint).POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());
    }
}
