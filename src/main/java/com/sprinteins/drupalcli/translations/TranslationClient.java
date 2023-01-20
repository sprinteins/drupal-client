package com.sprinteins.drupalcli.translations;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

  public TranslationModel getTranslations(long nid) {
    try {
      HttpRequest request = HttpRequestBuilderFactory
              .create(URI.create(baseUri + "/get-translations/" + nid + "?_format=json"), apiKey)
              .GET()
              .header("Content-Type", "application/json")
              .build();

      HttpResponse<String> httpResponse = httpClient
              .send(request, HttpResponse.BodyHandlers.ofString());

      HttpResponseStatusHandler.checkStatusCode(httpResponse);

      return objectMapper.readValue(httpResponse.body(), TranslationModel.class);
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Get translations failed", e);
    }
  }

  public TranslationModel getTranslatedNode(long nid, String langcode) {
    try {
      HttpRequest request = HttpRequestBuilderFactory
              .create(URI.create(baseUri + nid + "?_format=json" + "&lang=" + langcode), apiKey)
              .GET()
              .header("Content-Type", "application/json")
              .build();

      HttpResponse<String> httpResponse = httpClient
              .send(request, HttpResponse.BodyHandlers.ofString());

      HttpResponseStatusHandler.checkStatusCode(httpResponse);

      return objectMapper.readValue(httpResponse.body(), TranslationModel.class);
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Get translated node failed", e);
    }
  }
}
