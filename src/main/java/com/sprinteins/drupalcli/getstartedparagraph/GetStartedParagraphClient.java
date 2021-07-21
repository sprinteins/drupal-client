package com.sprinteins.drupalcli.getstartedparagraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GetStartedParagraphClient {

    private static final int TIMEOUT_MS = 30 * 1000;

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String authenticationHeader;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public GetStartedParagraphClient(ObjectMapper objectMapper, String baseUri, String authenticationHeader) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/entity/paragraph/";
        this.authenticationHeader = authenticationHeader;
    }

    public void patch(long id, GetStartedParagraphModel getStartedParagraph) throws IOException, InterruptedException {

        String patchRequestBody = objectMapper.writeValueAsString(getStartedParagraph);

        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(baseUri + id + "?_format=json"))
                .timeout(Duration.ofMillis(TIMEOUT_MS))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                .header("Content-Type", "application/json")
                .header("api-key", authenticationHeader)
                .build();

        HttpResponse<Void> httpResponse = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.discarding());

        if (httpResponse.statusCode() >= 300) {
            throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode() + "\nBody: "+ httpResponse.body());
        }
    }

    public GetStartedParagraphModel get(long id) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .uri(URI.create(baseUri + id + "?_format=json"))
                    .timeout(Duration.ofMillis(TIMEOUT_MS))
                    .header("Content-Type", "application/json")
                    .header("api-key", authenticationHeader)
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
