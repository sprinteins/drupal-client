package com.sprinteins.drupalcli.file;

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

		protected FileUploadModel upload(Path path, String uploadBaseUri) throws NoSuchAlgorithmException {
			return upload(path, uploadBaseUri, path.getFileName().toString());
		}

    protected FileUploadModel upload(Path path, String uploadBaseUri, String fileName) throws NoSuchAlgorithmException {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(uploadBaseUri + "?_format=json"), apiKey)
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + fileName + "\"")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            
            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), FileUploadModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }
}
