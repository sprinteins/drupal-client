package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphClient;
import com.sprinteins.drupalcli.node.NodeClient;

public class ApplicationContext {

    private final ObjectMapper objectMapper;
    private final NodeClient nodeClient;
    private final GetStartedParagraphClient getStartedParagraphClient;
    private final ImageClient imageClient;
    private final ApiReferenceFileClient apiReferenceFileClient;

    public ApplicationContext(String baseUri, String apiKey) {
         objectMapper = new ObjectMapper();
         objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         objectMapper.setSerializationInclusion(Include.NON_NULL);

         nodeClient = new NodeClient(
                 objectMapper,
                 baseUri,
                 apiKey);
         getStartedParagraphClient = new GetStartedParagraphClient(
                 objectMapper,
                 baseUri,
                 apiKey);
         imageClient = new ImageClient(
                 objectMapper,
                 baseUri,
                 apiKey);
         apiReferenceFileClient = new ApiReferenceFileClient(
                 objectMapper,
                 baseUri,
                 apiKey);
    }
    
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public NodeClient nodeClient() {
        return nodeClient;
    }

    public GetStartedParagraphClient getStartedParagraphClient() {
        return getStartedParagraphClient;
    }

    public ImageClient imageClient() {
        return imageClient;
    }

    public ApiReferenceFileClient apiReferenceFileClient() {
        return apiReferenceFileClient;
    }
    
    
}
