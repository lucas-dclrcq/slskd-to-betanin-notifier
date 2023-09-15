package org.ldclrcq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.ldclrcq.complete_folder.CompleteFolderManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompleteFolderManagerTest {
    @Test
    public void Given_empty_directorys_should_delete_all(@TempDir Path tempDir) throws IOException {
        // ARRANGE
        Files.createDirectory(tempDir.resolve("test1"));
        Files.createDirectory(tempDir.resolve("test2"));
        Files.createDirectory(tempDir.resolve("test3"));

        var completeFolderCleaner = new CompleteFolderManager(tempDir);

        // ACT
        completeFolderCleaner.clean();

        // ASSERT
        assertThat(tempDir).isEmptyDirectory();
    }

    @Test
    public void Given_empty_directories_and_containing_mp3_should_delete_only_empty(@TempDir Path tempDir) throws IOException {
        // ARRANGE
        Path test1 = tempDir.resolve("test1");
        Files.createDirectory(test1);
        Path test2 = tempDir.resolve("test2");
        Files.createDirectory(test2);
        Files.createFile(test2.resolve("toto.mp3"));
        Path test3 = tempDir.resolve("test3");
        Files.createDirectory(test3);
        Files.createFile(test3.resolve("tutu.mp3"));

        var completeFolderCleaner = new CompleteFolderManager(tempDir);

        // ACT
        completeFolderCleaner.clean();

        // ASSERT
        assertThat(tempDir).isNotEmptyDirectory()
                .isDirectoryContaining(path -> path.getFileName().toString().endsWith("test3") || path.getFileName().toString().endsWith("test2"))
                .isDirectoryNotContaining(path -> path.getFileName().toString().endsWith("test1"));
    }

    @Test
    public void Given_empty_directories_and_containing_flac_should_delete_only_empty(@TempDir Path tempDir) throws IOException {
        // ARRANGE
        Path test1 = tempDir.resolve("test1");
        Files.createDirectory(test1);
        Path test2 = tempDir.resolve("test2");
        Files.createDirectory(test2);
        Files.createFile(test2.resolve("toto.flac"));
        Path test3 = tempDir.resolve("test3");
        Files.createDirectory(test3);
        Files.createFile(test3.resolve("tutu.flac"));

        var completeFolderCleaner = new CompleteFolderManager(tempDir);

        // ACT
        completeFolderCleaner.clean();

        // ASSERT
        assertThat(tempDir).isNotEmptyDirectory()
                .isDirectoryContaining(path -> path.getFileName().toString().endsWith("test3") || path.getFileName().toString().endsWith("test2"))
                .isDirectoryNotContaining(path -> path.getFileName().toString().endsWith("test1"));
    }

    @Test
    public void Given_directories_should_produce_list_of_filenames(@TempDir Path tempDir) throws IOException {
        // ARRANGE
        Files.createDirectory(tempDir.resolve("test1"));
        Files.createDirectory(tempDir.resolve("test2"));
        Files.createDirectory(tempDir.resolve("test3"));

        var completeFolderCleaner = new CompleteFolderManager(tempDir);

        // ACT
        List<String> subDirectories = completeFolderCleaner.getSubDirectories();

        // ASSERT
        assertThat(subDirectories).containsExactlyInAnyOrder("test1", "test2", "test3");
    }


}

