package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpClientBuilderFactory;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ParagraphClient<R extends ParagraphModel> {

    private final ObjectMapper objectMapper;
    private final String postUri;
    private final String baseUri;
    private final String apiKey;
    private final Class<R> modelClass;

    public ParagraphClient(ObjectMapper objectMapper, String baseUri, String apiKey, Class<R> modelClass) {
        this.objectMapper = objectMapper;
        this.postUri = baseUri + "/entity/paragraph";
        this.baseUri = baseUri + "/entity/paragraph/";
        this.apiKey = apiKey;
        this.modelClass = modelClass;
    }

    public R post(R paragraphModel) throws IOException, InterruptedException {
        String postRequestBody = objectMapper.writeValueAsString(paragraphModel);
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(postUri + "?_format=json"), apiKey)
                    .method("POST", HttpRequest.BodyPublishers.ofString(postRequestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = HttpClientBuilderFactory.create().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            HttpResponseStatusHandler.checkStatusCode(httpResponse);
            return objectMapper.readValue(httpResponse.body(), modelClass);

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Post Paragraph failed", e);
        }
    }

    public void patch(long id, R paragraphModel) throws IOException, InterruptedException {

        String patchRequestBody = objectMapper.writeValueAsString(paragraphModel);

        HttpRequest request = HttpRequestBuilderFactory
                .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<Void> httpResponse = HttpClientBuilderFactory.create().build()
                .send(request, HttpResponse.BodyHandlers.discarding());

        HttpResponseStatusHandler.checkStatusCode(httpResponse);
    }

    public R get(long id) throws IOException, InterruptedException {
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

            return objectMapper.readValue(httpResponse.body(), modelClass);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Paragraph failed", e);
        }
    }
}
