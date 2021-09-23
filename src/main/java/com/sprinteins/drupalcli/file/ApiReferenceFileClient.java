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

public class ApiReferenceFileClient {

    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String uploadBaseUri;
    private final HttpClient httpClient;

    public ApiReferenceFileClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.uploadBaseUri = baseUri + "/file/upload/node/api_reference/field_source_file";
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    public FileUploadModel upload(Path path) {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(uploadBaseUri + "?_format=json"), apiKey)
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + path.getFileName() + "\"")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            
            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), FileUploadModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }

    public String download(String link){
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(link), apiKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            
            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Download failed", e);
        }
    }

}
