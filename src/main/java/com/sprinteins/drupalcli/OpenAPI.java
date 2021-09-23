package com.sprinteins.drupalcli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenAPI {

    public static Path findYamlFile(Path path) {
        try (var stream = Files.newDirectoryStream(path, "*.{yaml,yml}")) {
            for (Path file : stream) {
                return file;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new IllegalStateException("No yaml file for OpenAPI spec in given directory (" + path + ")");
    }

}
