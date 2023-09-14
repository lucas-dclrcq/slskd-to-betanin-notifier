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
import java.util.stream.Collectors;

public record BetaninNotifier(String betaninUrl, String betaninApiKey, String betaninCompleteFolderPath) {
    public void notifyBetanin(String musicDirectory) throws URISyntaxException, IOException, InterruptedException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("path", betaninCompleteFolderPath);
        parameters.put("name", musicDirectory);

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        URI endpoint = new URI(betaninUrl).resolve("/api/torrents");

        HttpRequest postRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-API-Key", betaninApiKey)
                .uri(endpoint).POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(postRequest, HttpResponse.BodyHandlers.ofString());
    }
}
