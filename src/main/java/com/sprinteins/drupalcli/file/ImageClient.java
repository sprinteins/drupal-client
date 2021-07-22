package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageClient {

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String apiKey;

    public ImageClient(ObjectMapper objectMapper, String baseUri, String apiKey) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/file/upload/media/file/field_media_file";
        this.apiKey = apiKey;
    }

    public FileUploadModel upload(Path path) throws NoSuchAlgorithmException {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + "?_format=json"), apiKey)
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + generateMd5Hash(path) + path.getFileName() + "\"")
                    .build();

            HttpResponse<String> httpResponse = HttpClient.newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(httpResponse.body(), FileUploadModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }

    public String generateMd5Hash(Path path) throws IOException, NoSuchAlgorithmException {
        char[] hexCode = "0123456789ABCDEF".toCharArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(path));
        byte[] digest = md.digest();
        StringBuilder hash = new StringBuilder(digest.length*2);

        for ( byte b : digest) {
            hash.append(hexCode[(b >> 4) & 0xF]);
            hash.append(hexCode[(b & 0xF)]);
        }
        return hash.toString().toUpperCase();
    }
}
