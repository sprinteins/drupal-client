package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.HttpRequestBuilderFactory;
import com.sprinteins.drupalcli.HttpResponseStatusHandler;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class ImageClient extends FileClient{

    private final String uploadBaseUri;
    private final String apiDocsDirectory;


    public ImageClient(ObjectMapper objectMapper, String baseUri, String apiKey, HttpClient httpClient) {
        super(objectMapper, baseUri, apiKey, httpClient);
        this.uploadBaseUri = baseUri + "/file/upload/media/file/field_media_file";
        this.apiDocsDirectory = baseUri + "/sites/default/files/api-docs/";
    }

    public FileUploadModel upload(Path path) throws IOException {
        String md5 = generateMd5Hash(path);
        return super.upload(path, this.uploadBaseUri, md5 + path.getFileName());
    }

    public int head(Path path) throws NoSuchAlgorithmException {
        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(URI.create(apiDocsDirectory + generateMd5Hash(path) + path.getFileName()), apiKey)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return httpResponse.statusCode();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Head failed", e);
        }
    }

    public byte[] download(String link) throws MalformedURLException, URISyntaxException {

        URI linkUrl = URI.create(link);
        URI baseUrl = URI.create(baseUri);

        URI requestUri;

        if(!linkUrl.isAbsolute()){
            URIBuilder builder = new URIBuilder()
                .setScheme(baseUrl.getScheme())
                .setHost(baseUrl.getHost())
                .setPath(linkUrl.getPath());
            requestUri = builder.build();
        } else {
            requestUri = linkUrl;
        }

        try {
            HttpRequest request = HttpRequestBuilderFactory
                    .create(requestUri, apiKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<byte[]> httpResponse = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofByteArray());
            
            HttpResponseStatusHandler.checkStatusCode(httpResponse);

            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Download failed", e);
        }
    }
    
    public String generateMd5Hash(Path path) {
        try {
            return generateMd5Hash(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String generateMd5Hash(byte[] bytes) {
        try {
            char[] hexCode = "0123456789ABCDEF".toCharArray();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte[] digest = md.digest();
            StringBuilder hash = new StringBuilder(digest.length*2);
            for ( byte b : digest) {
                hash.append(hexCode[b >> 4 & 0xF]);
                hash.append(hexCode[b & 0xF]);
            }
            return hash.toString().toUpperCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
