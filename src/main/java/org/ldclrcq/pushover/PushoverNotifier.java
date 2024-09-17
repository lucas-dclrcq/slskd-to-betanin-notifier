package org.ldclrcq.pushover;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public record PushoverNotifier(String pushoverBaseUrl, String pushoverToken, String pushoverUser, String pushoverDevice) {
    private static final Logger logger = LogManager.getLogger();

    public void notifyImportedDirectories(List<String> importedDirectories) throws URISyntaxException, IOException, InterruptedException {
        logger.info("Notifying pushover of imported directories...");
        logger.info("---------------------------------");

        for (String importedDirectory : importedDirectories) {
            notifyImportedDirectory(importedDirectory);
        }

    }

    private void notifyImportedDirectory(String importedDirectory) throws URISyntaxException, IOException, InterruptedException {
        logger.info("Notifying pushover for directory : {}", importedDirectory);

        var parameters = new HashMap<String, String>();
        parameters.put("token", pushoverToken);
        parameters.put("user", pushoverUser);
        parameters.put("device", pushoverDevice);
        parameters.put("title", "New music imported: %s".formatted(importedDirectory));
        parameters.put("message", "A music directory was successfully notified to betanin: %s".formatted(importedDirectory));

        var form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        var endpoint = new URI(pushoverBaseUrl).resolve("/1/messages.json");

        var postRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(endpoint).POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        try (var httpclient = HttpClient.newBuilder().build()) {
            httpclient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        }
    }
}
