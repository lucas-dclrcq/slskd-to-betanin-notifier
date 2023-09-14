package org.ldclrcq;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public record CompleteFolderManager(Path completeFolderPath) {

    public void clean() throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(completeFolderPath)) {
            directoryStream.iterator()
                    .forEachRemaining(CompleteFolderManager::deleteIfNotContainingMusicFiles);
        }
    }

    private static void deleteIfNotContainingMusicFiles(Path path) {
        try (var files = Files.list(path)) {
            boolean containsMusic = files.toList()
                    .stream()
                    .anyMatch(CompleteFolderManager::isMusicFile);

            if (!containsMusic) {
                deleteDirectory(path);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isMusicFile(Path directoryElement) {
        var filename = directoryElement.getFileName().toString();
        return filename.endsWith("mp3") || filename.endsWith("flac");
    }

    private static void deleteDirectory(Path path) throws IOException {
        try (var directoryContent = Files.walk(path)) {
            directoryContent
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public List<String> getSubDirectories() throws IOException {
        try (var files = Files.list(completeFolderPath)) {
            return files.toList()
                    .stream()
                    .map(path -> path.getFileName().toString())
                    .toList();
        }
    }
}
