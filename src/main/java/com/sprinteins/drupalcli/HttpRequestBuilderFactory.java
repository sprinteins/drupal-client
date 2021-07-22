package com.sprinteins.drupalcli;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.time.Duration;

public abstract class HttpRequestBuilderFactory {
    
    private static final int TIMEOUT_MS = 30 * 1000;
    
    private HttpRequestBuilderFactory() {
    }
    
    public static HttpRequest.Builder create(URI uri, String apiKey) {
        Builder builder = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.ofMillis(TIMEOUT_MS))
                .uri(uri)
                .header("api-key", apiKey);
        if (uri.getHost().endsWith("dhlapi-dev.metadeploy.com")
                || uri.getHost().endsWith("dapi.sprinteins.com")) {
            builder.header("Authorization", "Basic ZGhsX2Rldjp4dk1jM1JKcVhzcVI=");
        }
        return builder;
    }

}
