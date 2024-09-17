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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public record BetaninNotifier(String betaninUrl, String betaninApiKey, String betaninCompleteFolderPath) {
    private final static String XApiKeyHeaderKey = "X-API-Key";

    public List<String> notifyBetanin(List<String> musicDirectories) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Notifying betanin of new files...");
        System.out.println("---------------------------------");

        List<BetaninTorrentDTO> alreadyImportedDirectories = this.getAlreadyImportedDirectories();

        List<String> importedDirectoriesWithoutNeedingInput = this.removeNeedInputImports(alreadyImportedDirectories)
                .stream()
                .map(BetaninTorrentDTO::name)
                .toList();

        List<String> notifiedDirectories = new ArrayList<>();

        for (var musicDirectory : musicDirectories) {
            if (importedDirectoriesWithoutNeedingInput.contains(musicDirectory)) {
                System.out.printf("Directory %s was already imported in betanin: skipping%n", musicDirectory);
                continue;
            }

            notifyBetanin(musicDirectory);
            notifiedDirectories.add(musicDirectory);
        }

        System.out.println();
        return notifiedDirectories;
    }

    private List<BetaninTorrentDTO> removeNeedInputImports(List<BetaninTorrentDTO> alreadyImportedDirectories) throws IOException, URISyntaxException, InterruptedException {
        List<BetaninTorrentDTO> importedDirectories = new ArrayList<>();

        for (BetaninTorrentDTO alreadyImportedDirectory : alreadyImportedDirectories) {
            if (alreadyImportedDirectory.status().equals("NEEDS_INPUT")) {
                this.deleteImport(alreadyImportedDirectory.id());
            } else {
                importedDirectories.add(alreadyImportedDirectory);
            }
        }

        return importedDirectories;
    }

    public void deleteImport(String id) throws IOException, InterruptedException, URISyntaxException {
        var endpoint = new URI(betaninUrl).resolve("/api/torrents/").resolve(id);

        var deleteRequest = HttpRequest.newBuilder()
                .header(XApiKeyHeaderKey, betaninApiKey)
                .uri(endpoint)
                .DELETE()
                .build();

        try (var httpclient = HttpClient.newBuilder().build()) {
            httpclient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        }
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

        try (var httpclient = HttpClient.newBuilder().build()) {
            httpclient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        }
    }

    private List<BetaninTorrentDTO> getAlreadyImportedDirectories() throws URISyntaxException, IOException, InterruptedException {
        var endpoint = new URI(betaninUrl).resolve("/api/torrents?page=1&per_page=25");

        var getRequest = HttpRequest.newBuilder()
                .header(XApiKeyHeaderKey, betaninApiKey)
                .uri(endpoint).GET()
                .build();

        try (var httpclient = HttpClient.newBuilder().build()) {
            String body = httpclient.send(getRequest, HttpResponse.BodyHandlers.ofString()).body();
            var betaninTorrentResponse = new ObjectMapper().readValue(body, BetaninTorrentResponse.class);

            System.out.printf("Fetched %d imported directories from betanin%n", betaninTorrentResponse.torrents().size());

            return betaninTorrentResponse.torrents();
        }
    }
}
