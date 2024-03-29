package com.sprinteins.drupalcli.file;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileFinder {

    // See https://github.com/spotbugs/spotbugs/issues/1694
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
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

    // See https://github.com/spotbugs/spotbugs/issues/1694
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    public static Path findJsonFile(Path path) {
        try (var stream = Files.newDirectoryStream(path, "*.json")) {
            for (Path file : stream) {
                return file;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new IllegalStateException("No json file for OpenAPI spec in given directory (" + path + ")");
    }

}
