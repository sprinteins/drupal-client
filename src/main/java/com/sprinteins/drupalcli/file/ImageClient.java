package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpClientBuilderFactory;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class ImageClient {

    private final ObjectMapper objectMapper;
    private final String uploadBaseUri;
    private final String baseUri;
    private final String apiKey;
    private final String apiDocsDirectory;

    public ImageClient(ObjectMapper objectMapper, String baseUri, String apiKey) {
        this.objectMapper = objectMapper;
        this.uploadBaseUri = baseUri + "/file/upload/media/file/field_media_file";
        this.baseUri = baseUri;
        this.apiKey = apiKey;
        this.apiDocsDirectory = baseUri + "/sites/default/files/api-docs/";
    }

    public FileUploadModel upload(Path path) throws NoSuchAlgorithmException {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(uploadBaseUri + "?_format=json"), apiKey)
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + generateMd5Hash(path) + path.getFileName() + "\"")
                    .build();

            HttpResponse<String> httpResponse = HttpClientBuilderFactory.create()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            
            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), FileUploadModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }

    public int head(Path path) throws NoSuchAlgorithmException {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(apiDocsDirectory + generateMd5Hash(path) + path.getFileName()), apiKey)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = HttpClientBuilderFactory.create().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return httpResponse.statusCode();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Head failed", e);
        }
    }

    public byte[] download(String link){
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + link), apiKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<byte[]> httpResponse = HttpClientBuilderFactory.create().build()
                    .send(request, HttpResponse.BodyHandlers.ofByteArray());

            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Download failed", e);
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
        return hash.toString().toUpperCase(Locale.ROOT);
    }
}
