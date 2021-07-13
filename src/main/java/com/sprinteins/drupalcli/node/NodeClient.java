package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NodeClient {

    private static final int TIMEOUT_MS = 30 * 1000;

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String authenticationHeader;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public NodeClient(ObjectMapper objectMapper, String baseUri, String authenticationHeader) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri + "/node/";
        this.authenticationHeader = authenticationHeader;
    }

    public void patch(long id, NodeModel nodeModel) {
        try {
            String patchRequestBody = objectMapper
                    .writeValueAsString(nodeModel);

            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(baseUri + id))
                    .timeout(Duration.ofMillis(TIMEOUT_MS))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(patchRequestBody))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authenticationHeader)
                    .build();

            HttpResponse<Void> httpResponse = HttpClient.newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.discarding());

            if (httpResponse.statusCode() >= 300) {
                throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Patch failed", e);
        }
    }

}
