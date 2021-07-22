package com.sprinteins.drupalcli;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;

public abstract class HttpClientBuilderFactory {
    
    private HttpClientBuilderFactory() {
    }
    
    public static HttpClient.Builder create() {
        return HttpClient.newBuilder().followRedirects(Redirect.NORMAL);
    }

}
