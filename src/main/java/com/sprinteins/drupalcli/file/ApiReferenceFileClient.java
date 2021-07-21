package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

public class ApiReferenceFileClient {

    private static final int TIMEOUT_MS = 30 * 1000;

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String authenticationHeader;

    public ApiReferenceFileClient(ObjectMapper objectMapper, String baseUri, String authenticationHeader) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/file/upload/node/api_reference/field_source_file";
        this.authenticationHeader = authenticationHeader;
    }

    public ApiReferenceFileModel upload(Path path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(baseUri + "?_format=json"))
                    .timeout(Duration.ofMillis(TIMEOUT_MS))
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + path.getFileName() + "\"")
                    .header("api-key", authenticationHeader)
                    .build();

            HttpResponse<String> httpResponse = HttpClient.newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(httpResponse.body(), ApiReferenceFileModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }

}
