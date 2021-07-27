package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpClientBuilderFactory;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class ApiReferenceFileClient {

    private final ObjectMapper objectMapper;
    private final String baseUri;
    private final String apiKey;
    private final String uploadBaseUri;

    public ApiReferenceFileClient(ObjectMapper objectMapper, String baseUri, String apiKey) {
        this.objectMapper = objectMapper;
        this.uploadBaseUri = baseUri + "/file/upload/media/file/field_media_file";
        this.baseUri = baseUri;
        this.apiKey = apiKey;
    }

    public FileUploadModel upload(Path path) {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(uploadBaseUri + "?_format=json"), apiKey)
                    .POST(HttpRequest.BodyPublishers.ofFile(path))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "file; filename=\"" + path.getFileName() + "\"")
                    .build();

            HttpResponse<String> httpResponse = HttpClientBuilderFactory.create().build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(httpResponse.body(), FileUploadModel.class);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Upload failed", e);
        }
    }

    public byte[] download(String link){
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(link), apiKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<byte[]> httpResponse = HttpClientBuilderFactory.create().build()
                    .send(request, HttpResponse.BodyHandlers.ofByteArray());

            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Download failed", e);
        }
    }

}
