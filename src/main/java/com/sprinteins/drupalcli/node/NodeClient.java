package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpClientBuilderFactory;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NodeClient {

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String apiKey;

    public NodeClient(ObjectMapper objectMapper, String baseUri, String apiKey) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/node/";
        this.apiKey = apiKey;
    }

    public void patch(long id, NodeModel nodeModel) {
        try {
            String patchRequestBody = objectMapper
                    .writeValueAsString(nodeModel);

            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<Void> httpResponse = HttpClientBuilderFactory.create().build()
                    .send(request, HttpResponse.BodyHandlers.discarding());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Patch failed", e);
        }
    }

    public NodeModel get(long id) {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = HttpClientBuilderFactory.create()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), NodeModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Node failed", e);
        }
    }

}
