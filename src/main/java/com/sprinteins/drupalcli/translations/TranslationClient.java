package com.sprinteins.drupalcli.translations;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;

public class TranslationClient {
  private ObjectMapper objectMapper;
  private String baseUri;
  private String apiKey;
  private HttpClient httpClient;

  public TranslationClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.baseUri = baseUri;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
    }

  public List<TranslationModel> getTranslations(long nid) {
    try {
      HttpRequest request = HttpRequestBuilderFactory
              .create(URI.create(baseUri + "/api-reference/" + nid + "/available-translations" + "?_format=json"), apiKey)
              .GET()
              .header("Content-Type", "application/json")
              .build();

      HttpResponse<String> httpResponse = httpClient
              .send(request, HttpResponse.BodyHandlers.ofString());

      HttpResponseStatusHandler.checkStatusCode(httpResponse);    
      return objectMapper.readValue(httpResponse.body(), new TypeReference<List<TranslationModel>>(){});
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Get translations failed", e);
    }
  }

}
