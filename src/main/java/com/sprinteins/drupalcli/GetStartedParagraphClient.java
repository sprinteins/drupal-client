package com.sprinteins.drupalcli;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GetStartedParagraphClient {

    private static final int TIMEOUT_MS = 30 * 1000;

    private final String baseUri;

    public GetStartedParagraphClient(
            String baseUri
    ) {
        this.baseUri = baseUri;
    }

    public void patch(
        long id,
        GetStartedParagraphModel getStartedParagraph,
        String token
    ) throws IOException, InterruptedException {

        String patchRequestBody = new ObjectMapper()
                .writeValueAsString(getStartedParagraph);

        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(baseUri + id))
                .timeout(Duration.ofMillis(TIMEOUT_MS))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                .headers("Content-Type", "application/json", "Authorization", "Basic "+token)
                .build();

        HttpResponse<Void> httpResponse = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.discarding());

        if (httpResponse.statusCode() >= 300) {
            throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode());
        }

        if (httpResponse.statusCode() == 200) {
            System.out.println("Patch successful!");
        }
    }
}
