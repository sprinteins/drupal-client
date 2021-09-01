package com.sprinteins.drupalcli.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.file.ImageClient;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageClientTest {

    @Test
    public void testGenerateMd5Hash() throws NoSuchAlgorithmException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("json/node-54-exported.json")).getFile());
        String expected = "CF34D507DD2C2BB1FC01EDD675AC3AC0";

        String checksum = new ImageClient(
                new ObjectMapper(),
                ":" ,
                "",
                HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build())
                    .generateMd5Hash(Paths.get(file.getPath()));

        assertThat(checksum.equals(expected)).isTrue();
    }
}
