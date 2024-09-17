package org.ldclrcq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ldclrcq.betanin.BetaninNotifier;
import org.ldclrcq.complete_folder.CompleteFolderManager;
import org.ldclrcq.pushover.PushoverNotifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main {
    protected static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        logger.info("Starting SLSKD to Betanin notifier\n");
        String localCompleteFolderPath = System.getenv("SLSKD_COMPLETE_FOLDER_PATH");
        logger.info("Complete folder path: {}}", localCompleteFolderPath);
        String betaninUrl = System.getenv("SLSKD_BETANIN_URL");
        logger.info("Betanin url: {}}", betaninUrl);
        String betaninApiKey = System.getenv("SLSKD_BETANIN_API_KEY");
        logger.info("Betanin api key: {}}", betaninApiKey);
        String betaninCompleteFolderPath = System.getenv("SLSKD_BETANIN_COMPLETE_FOLDER_PATH");
        logger.info("Betanin complete folder path: {}}", betaninCompleteFolderPath);
        String pushoverToken = System.getenv("SLSKD_PUSHOVER_TOKEN");
        logger.info("Pushover: {}}", pushoverToken);
        String pushOverUser = System.getenv("SLSKD_PUSHOVER_USER");
        logger.info("Pushover user: {}}", pushOverUser);
        String pushoverDevice = System.getenv("SLSKD_PUSHOVER_DEVICE");
        logger.info("Pushhover device id: {}}", pushoverDevice);
        logger.info("----------------\n");

        var completeFolderManager = new CompleteFolderManager(Path.of(localCompleteFolderPath));
        var betaninNotifier = new BetaninNotifier(betaninUrl, betaninApiKey, betaninCompleteFolderPath);
        var pushoverNotifier = new PushoverNotifier("https://api.pushover.net", pushoverToken, pushOverUser, pushoverDevice);

        completeFolderManager.clean();
        var directoriesContainingMusic = completeFolderManager.getSubDirectories();
        var notifiedToBetaninDirectories = betaninNotifier.notifyBetanin(directoriesContainingMusic);
        pushoverNotifier.notifyImportedDirectories(notifiedToBetaninDirectories);

        logger.info("Done. Bye !");
    }
}