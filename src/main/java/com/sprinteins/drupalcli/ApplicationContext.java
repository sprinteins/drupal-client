package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.commands.GlobalOptions;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.paragraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.paragraph.ParagraphClient;
import com.sprinteins.drupalcli.paragraph.ReleaseNoteParagraphModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ApplicationContext {

    private final ObjectMapper objectMapper;
    private final NodeClient nodeClient;
    private final ParagraphClient<GetStartedParagraphModel> getStartedParagraphClient;
    private final ParagraphClient<ReleaseNoteParagraphModel> releaseNoteParagraphClient;
    private final ImageClient imageClient;
    private final ApiReferenceFileClient apiReferenceFileClient;
    private final Converter converter = new Converter();

    public ApplicationContext(String baseUri, GlobalOptions globalOptions) {
        this(baseUri, readApiKey(globalOptions.tokenFile));
    }
    
    public ApplicationContext(String baseUri, String apiKey) {
         objectMapper = new ObjectMapper();
         objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         objectMapper.setSerializationInclusion(Include.NON_NULL);

         nodeClient = new NodeClient(
                 objectMapper,
                 baseUri,
                 apiKey);
         getStartedParagraphClient = new ParagraphClient<>(
                 objectMapper,
                 baseUri,
                 apiKey,
                 GetStartedParagraphModel.class);
         imageClient = new ImageClient(
                 objectMapper,
                 baseUri,
                 apiKey);
         apiReferenceFileClient = new ApiReferenceFileClient(
                 objectMapper,
                 baseUri,
                 apiKey);
        releaseNoteParagraphClient = new ParagraphClient<>(
                objectMapper,
                baseUri,
                apiKey,
                ReleaseNoteParagraphModel.class);
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
            if (apiKey.isEmpty() ) {
                throw new IllegalArgumentException("API key invalid : first line of " + path + " is empty");
            }
            return apiKey;
        } catch (IOException e) {
            throw new IllegalArgumentException("API key file invalid. Could not read " + path, e);
        }
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

    public ParagraphClient<ReleaseNoteParagraphModel> releaseNoteParagraphClient() { return releaseNoteParagraphClient;}

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
