package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImageClient {

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String apiKey;

    public ImageClient(ObjectMapper objectMapper, String baseUri, String apiKey) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/entity/file";
        this.apiKey = apiKey;
    }

    public void post(ImageModel imageModel) throws IOException, InterruptedException {

        String patchRequestBody = objectMapper.writeValueAsString(imageModel);

        HttpRequest request = HttpRequestBuilderFactory
                .create(URI.create(baseUri + "?_format=hal_json"), apiKey)
                .method("POST", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                .header("Content-Type", "application/hal+json")
                .build();

        HttpResponse<Void> httpResponse = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.discarding());

        if (httpResponse.statusCode() >= 300) {
            throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode() + "\nBody: "+ httpResponse.body());
        }
    }
}
