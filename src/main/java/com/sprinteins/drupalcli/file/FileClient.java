package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class FileClient {

    protected final ObjectMapper objectMapper;
    protected final String baseUri;
    protected final String apiKey;
    protected final HttpClient httpClient;

    public FileClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    protected FileUploadModel upload(Path path, String uploadBaseUri) throws IOException {
        try {
            var fileName = "" + path.getFileName();
            var response = post(path, uploadBaseUri, fileName);
            return objectMapper.readValue(response.body(), FileUploadModel.class);
        } catch (IOException e) {
            throw new IOException("File is not available", e);
        }

    }

    protected FileUploadModel upload(Path path, String uploadBaseUri, String fileName) throws IOException {
        var response = post(path, uploadBaseUri, fileName);
        return objectMapper.readValue(response.body(), FileUploadModel.class);
    }

    private HttpResponse<String> post(Path path, String uri, String fileName) {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(uri + "?_format=json"), apiKey)
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + fileName + "\"")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return httpResponse;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }
}
