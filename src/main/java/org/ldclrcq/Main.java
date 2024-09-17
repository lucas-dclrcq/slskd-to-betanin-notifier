package org.ldclrcq;

import org.ldclrcq.betanin.BetaninNotifier;
import org.ldclrcq.complete_folder.CompleteFolderManager;
import org.ldclrcq.pushover.PushoverNotifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Starting SLSKD to Betanin notifier\n");
        String localCompleteFolderPath = System.getenv("SLSKD_COMPLETE_FOLDER_PATH");
        System.out.printf("Complete folder path: %s%n", localCompleteFolderPath);
        String betaninUrl = System.getenv("SLSKD_BETANIN_URL");
        System.out.printf("Betanin url: %s%n", betaninUrl);
        String betaninApiKey = System.getenv("SLSKD_BETANIN_API_KEY");
        System.out.printf("Betanin api key: %s%n", betaninApiKey);
        String betaninCompleteFolderPath = System.getenv("SLSKD_BETANIN_COMPLETE_FOLDER_PATH");
        System.out.printf("Betanin complete folder path: %s%n", betaninCompleteFolderPath);
        String pushoverToken = System.getenv("SLSKD_PUSHOVER_TOKEN");
        System.out.printf("Pushover: %s%n", pushoverToken);
        String pushOverUser = System.getenv("SLSKD_PUSHOVER_USER");
        System.out.printf("Pushover user: %s%n", pushOverUser);
        String pushoverDevice = System.getenv("SLSKD_PUSHOVER_DEVICE");
        System.out.printf("Pushhover device id: %s%n", pushoverDevice);
        System.out.println("----------------\n");

        var completeFolderManager = new CompleteFolderManager(Path.of(localCompleteFolderPath));
        var betaninNotifier = new BetaninNotifier(betaninUrl, betaninApiKey, betaninCompleteFolderPath);
        var pushoverNotifier = new PushoverNotifier("https://api.pushover.net", pushoverToken, pushOverUser, pushoverDevice);

        completeFolderManager.clean();
        var directoriesContainingMusic = completeFolderManager.getSubDirectories();
        var notifiedToBetaninDirectories = betaninNotifier.notifyBetanin(directoriesContainingMusic);
        pushoverNotifier.notifyImportedDirectories(notifiedToBetaninDirectories);

        System.out.println("Done. Bye !");
    }
}