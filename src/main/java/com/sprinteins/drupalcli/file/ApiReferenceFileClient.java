package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class ApiReferenceFileClient extends FileClient {

    private final String uploadBaseUri;

    public ApiReferenceFileClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
        super(objectMapper, baseUri, apiKey, httpClient);
        this.uploadBaseUri = baseUri + "/file/upload/node/api_reference/field_source_file";
    }

    public FileUploadModel upload(Path path) throws NoSuchAlgorithmException {
        return super.upload(path, this.uploadBaseUri);
    }

    public String download(String link){
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(link), apiKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            
            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Download failed", e);
        }
    }

}
