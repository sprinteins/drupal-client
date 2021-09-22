package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ParagraphClient<R extends ParagraphModel> {

    private final ObjectMapper objectMapper;
    private final String postUri;
    private final String baseUri;
    private final String apiKey;
    private final Class<R> modelClass;
    private final HttpClient httpClient;

    public ParagraphClient(ObjectMapper objectMapper, String baseUri, String apiKey, Class<R> modelClass, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.postUri = baseUri + "/entity/paragraph";
        this.baseUri = baseUri + "/entity/paragraph/";
        this.apiKey = apiKey;
        this.modelClass = modelClass;
        this.httpClient = httpClient;
    }

    public R post(R paragraphModel) {
        try {
            String postRequestBody = objectMapper.writeValueAsString(paragraphModel);
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(postUri + "?_format=json"), apiKey)
                    .method("POST", HttpRequest.BodyPublishers.ofString(postRequestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);
            return objectMapper.readValue(httpResponse.body(), modelClass);

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Post Paragraph failed", e);
        }
    }
    
    public R patch(R paragraphModel) {
        return patch(paragraphModel.getOrCreateFirstId().getValue(), paragraphModel);
    }

    public R patch(long id, R paragraphModel) {
        try {
            String patchRequestBody = objectMapper.writeValueAsString(paragraphModel);
    
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                    .header("Content-Type", "application/json")
                    .build();
    
            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
    
            HttpResponseStatusHandler.checkStatusCode(httpResponse);
            
            return objectMapper.readValue(httpResponse.body(), modelClass);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Paragraph failed", e);
        }
    }

    public R get(long id) {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return objectMapper.readValue(httpResponse.body(), modelClass);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Paragraph failed", e);
        }
    }
}
