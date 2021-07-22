package com.sprinteins.drupalcli;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Redirect;

public abstract class HttpClientBuilderFactory {

    private HttpClientBuilderFactory() {
    }

    public static HttpClient.Builder create() {
        Builder httpClientBuilder = HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL);
        ProxySelector proxy = ProxySelector.getDefault();
        if (proxy != null) {
            httpClientBuilder.proxy(proxy);
        }
        return httpClientBuilder;
    }

}
