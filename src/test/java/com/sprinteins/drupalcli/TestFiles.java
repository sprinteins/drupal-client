package com.sprinteins.drupalcli;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class TestFiles {

    private TestFiles() {
    }

    public static byte[] readAllBytes(String path) throws IOException {
        return new ClassPathResource(path).getInputStream().readAllBytes();
    }

    public static String readAllBytesToString(String path) throws IOException {
        return new String(readAllBytes(path), StandardCharsets.UTF_8);
    }

}
