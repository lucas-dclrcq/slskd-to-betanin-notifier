package org.ldclrcq;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Starting SLSKD to Betanin notifier\n");
        String localCompleteFolderPath = args[0];
        System.out.println("Complete folder path: " + localCompleteFolderPath);
        String betaninUrl = args[1];
        System.out.println("Betanin url: " + betaninUrl);
        String betaninApiKey = args[2];
        System.out.println("Betanin api key: " + betaninApiKey);
        String betaninCompleteFolderPath = args[3];
        System.out.println("Betanin complete folder path: " + betaninCompleteFolderPath);

        System.out.println("----------------\n");

        var completeFolderManager = new CompleteFolderManager(Path.of(localCompleteFolderPath));
        completeFolderManager.clean();

        BetaninNotifier betaninNotifier = new BetaninNotifier(betaninUrl, betaninApiKey, betaninCompleteFolderPath);

        var directoriesContainingMusic = completeFolderManager.getSubDirectories();

        for (var directory : directoriesContainingMusic) {
            System.out.println("Notifying betanin for directory : " + directory);
            betaninNotifier.notifyBetanin(directory);
        }

        System.out.println("Done. Bye !");
    }
}