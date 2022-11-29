package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.commands.GlobalOptions;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.paragraph.AdditionalInformationParagraphModel;
import com.sprinteins.drupalcli.paragraph.FaqItemParagraphModel;
import com.sprinteins.drupalcli.paragraph.FaqItemsParagraphModel;
import com.sprinteins.drupalcli.paragraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.paragraph.ParagraphClient;
import com.sprinteins.drupalcli.paragraph.ReleaseNoteParagraphModel;
import com.sprinteins.drupalcli.proxy.CustomProxySearchStrategy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

public class ApplicationContext {

    private final ObjectMapper objectMapper;
    private final NodeClient nodeClient;
    private final ParagraphClient<GetStartedParagraphModel> getStartedParagraphClient;
    private final ParagraphClient<AdditionalInformationParagraphModel> additionalInformationParagraphClient;
    private final ParagraphClient<FaqItemsParagraphModel> faqItemsParagraphClient;
    private final ParagraphClient<FaqItemParagraphModel> faqItemParagraphClient;
    private final ParagraphClient<ReleaseNoteParagraphModel> releaseNoteParagraphClient;
    private final ImageClient imageClient;
    private final ApiReferenceFileClient apiReferenceFileClient;
    private final Converter converter;

    public ApplicationContext(String baseUri, GlobalOptions globalOptions) {
        this(baseUri, readApiKey(globalOptions.tokenFile), buildHttpClient(globalOptions), initialiseConverter(globalOptions));
    }

    public ApplicationContext(String baseUri, String apiKey) {
        this(baseUri, apiKey, buildHttpClient(new GlobalOptions()), initialiseConverter(new GlobalOptions()));
    }

    public ApplicationContext(String baseUri, String apiKey, HttpClient httpClient, Converter initialisedConverter) {
        converter = initialisedConverter;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        nodeClient = new NodeClient(
                objectMapper,
                baseUri,
                apiKey,
                httpClient);
        getStartedParagraphClient = new ParagraphClient<>(
                objectMapper,
                baseUri,
                apiKey,
                GetStartedParagraphModel.class,
                httpClient);
        imageClient = new ImageClient(
                objectMapper,
                baseUri,
                apiKey,
                httpClient);
        apiReferenceFileClient = new ApiReferenceFileClient(
                objectMapper,
                baseUri,
                apiKey,
                httpClient);
        additionalInformationParagraphClient = new ParagraphClient<>(
                objectMapper,
                baseUri,
                apiKey,
                AdditionalInformationParagraphModel.class,
                httpClient);
        faqItemsParagraphClient = new ParagraphClient<>(
                objectMapper,
                baseUri,
                apiKey,
                FaqItemsParagraphModel.class,
                httpClient);
        faqItemParagraphClient = new ParagraphClient<>(
                objectMapper,
                baseUri,
                apiKey,
                FaqItemParagraphModel.class,
                httpClient);
        releaseNoteParagraphClient = new ParagraphClient<>(
                objectMapper,
                baseUri,
                apiKey,
                ReleaseNoteParagraphModel.class,
                httpClient);
    }

    public static Converter initialiseConverter(GlobalOptions globalOptions) {
        return new Converter(globalOptions.customHtml);
    }

    private static String readApiKey(Path path) {
        if (Files.notExists(path)) {
            throw new IllegalArgumentException("API key file not found: " + path + " does not exist");
        }
        try {
            List<String> apiKeyLines = Files.readAllLines(path);
            if (apiKeyLines.isEmpty()) {
                throw new IllegalArgumentException("API key invalid: " + path + " is empty");
            }
            String apiKey = apiKeyLines.get(0);
            if (apiKey.isEmpty()) {
                throw new IllegalArgumentException("API key invalid : first line of " + path + " is empty");
            }
            return apiKey;
        } catch (IOException e) {
            throw new IllegalArgumentException("API key file invalid. Could not read " + path, e);
        }
    }

    private static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };

    private static HttpClient buildHttpClient(GlobalOptions globalOptions) {
        Builder httpClientBuilder = HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL);
        CustomProxySearchStrategy searchStrategy =
                new CustomProxySearchStrategy(globalOptions, System.getenv());
        ProxySelector proxySelector = searchStrategy.getProxySelector();
        if (proxySelector != null) {
            httpClientBuilder.proxy(proxySelector);
        }
        if (globalOptions.insecureHttps) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                httpClientBuilder.sslContext(sslContext);
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException(e);
            }
        }
        return httpClientBuilder.build();
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public NodeClient nodeClient() {
        return nodeClient;
    }

    public ParagraphClient<GetStartedParagraphModel> getStartedParagraphClient() {
        return getStartedParagraphClient;
    }

    public ParagraphClient<AdditionalInformationParagraphModel> additionalInformationParagraphClient() {
        return additionalInformationParagraphClient;
    }

    public ParagraphClient<FaqItemsParagraphModel> faqItemsParagraphClient() {
      return faqItemsParagraphClient;
  }
  
    public ParagraphClient<FaqItemParagraphModel> faqItemParagraphClient() {
      return faqItemParagraphClient;
  }

    public ParagraphClient<ReleaseNoteParagraphModel> releaseNoteParagraphClient() {
        return releaseNoteParagraphClient;
    }

    public ImageClient imageClient() {
        return imageClient;
    }

    public ApiReferenceFileClient apiReferenceFileClient() {
        return apiReferenceFileClient;
    }

    public Converter converter() {
        return converter;
    }


}
