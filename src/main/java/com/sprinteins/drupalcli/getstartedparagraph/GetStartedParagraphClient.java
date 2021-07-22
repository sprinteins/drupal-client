package com.sprinteins.drupalcli.getstartedparagraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetStartedParagraphClient {

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String apiKey;

    public GetStartedParagraphClient(ObjectMapper objectMapper, String baseUri, String apiKey) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/entity/paragraph/";
        this.apiKey = apiKey;
    }

    public void patch(long id, GetStartedParagraphModel getStartedParagraph) throws IOException, InterruptedException {

        String patchRequestBody = objectMapper.writeValueAsString(getStartedParagraph);

        HttpRequest request = HttpRequestBuilderFactory
                .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<Void> httpResponse = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.discarding());

        if (httpResponse.statusCode() >= 300) {
            throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode() + "\nBody: "+ httpResponse.body());
        }
    }

    public GetStartedParagraphModel get(long id) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(baseUri + id + "?_format=json"), apiKey)
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> httpResponse = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() >= 300) {
                throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode() + "\nBody: "+ httpResponse.body());
            }

            return objectMapper.readValue(httpResponse.body(), GetStartedParagraphModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Get Paragraph failed", e);
        }
    }
}
