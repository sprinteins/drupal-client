package com.sprinteins.drupalcli;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class TestFiles {

    private TestFiles() {
    }

    public static byte[] readAllBytes(String path) throws Exception {
        return new ClassPathResource(path).getInputStream().readAllBytes();
    }

    public static String readAllBytesToString(String path) throws Exception {
        return new String(readAllBytes(path), StandardCharsets.UTF_8);
    }
}
