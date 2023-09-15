package org.ldclrcq;

import org.ldclrcq.betanin.BetaninNotifier;
import org.ldclrcq.complete_folder.CompleteFolderManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Starting SLSKD to Betanin notifier\n");
        String localCompleteFolderPath = args[0];
        System.out.printf("Complete folder path: %s%n", localCompleteFolderPath);
        String betaninUrl = args[1];
        System.out.printf("Betanin url: %s%n", betaninUrl);
        String betaninApiKey = args[2];
        System.out.printf("Betanin api key: %s%n", betaninApiKey);
        String betaninCompleteFolderPath = args[3];
        System.out.printf("Betanin complete folder path: %s%n", betaninCompleteFolderPath);

        System.out.println("----------------\n");

        var completeFolderManager = new CompleteFolderManager(Path.of(localCompleteFolderPath));
        completeFolderManager.clean();

        var betaninNotifier = new BetaninNotifier(betaninUrl, betaninApiKey, betaninCompleteFolderPath);
        var directoriesContainingMusic = completeFolderManager.getSubDirectories();
        betaninNotifier.notifyBetanin(directoriesContainingMusic);

        System.out.println("Done. Bye !");
    }
}