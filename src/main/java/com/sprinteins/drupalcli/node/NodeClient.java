package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NodeClient {

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String apiKey;
    private final HttpClient httpClient;

    public NodeClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/node/";
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

    public NodeModel patch(long id, NodeModel nodeModel) {
        try {
            String patchRequestBody = objectMapper
                    .writeValueAsString(nodeModel);

            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);
            
            return objectMapper.readValue(httpResponse.body(), NodeModel.class);
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

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), NodeModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Node failed", e);
        }
    }

    public NodeModel getByUri(String link) {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(cleanLink(link) + "?_format=json"), apiKey)
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), NodeModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Node failed", e);
        }
    }

    public String cleanLink(String link){
        URI uri = URI.create(link);
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }

    public NodeModel getTranslatedNode(long nid, String langcode) {
      try {
        HttpRequest request = HttpRequestBuilderFactory
                .create(URI.create(baseUri + nid + "?_format=json" + "&lang=" + langcode), apiKey)
                .GET()
                .header("Content-Type", "application/json")
                .build();
  
        HttpResponse<String> httpResponse = httpClient
                .send(request, HttpResponse.BodyHandlers.ofString());
  
        HttpResponseStatusHandler.checkStatusCode(httpResponse);
  
        return objectMapper.readValue(httpResponse.body(), NodeModel.class);
      } catch (IOException | InterruptedException e) {
        throw new IllegalStateException("Get translated node failed", e);
      }
    }
}
