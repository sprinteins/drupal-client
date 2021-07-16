package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ApiReferenceFileModel;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.GetStartedDocsElementModel;
import com.sprinteins.drupalcli.models.ValueFormat;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

@Command(name = "update", description = "Update description")
class Update implements Callable<Integer> {

    public static final String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";
    public static final String API_KEY_ENV_KEY = "DHL_API_DEVELOPER_PORTAL_TOKEN_FILE";
    public static final String MAIN_MARKDOWN_FILE_NAME = "main.markdown";


    @Option(names = { "--api-page" , "-a"}, description = "API page ID", required = true)
    Long nodeId;

    @Option(names = { "--api-page-directory" , "-d"}, description = "Local path to the API page documentation", required = true)
    String directory;

    @Option(names = { "--portal-environment" , "-p"}, description = "Portal environment to update")
    String portalEnv;

    @Option(names = { "--explicitly-disable-checks" , "-e"}, description = "Explicitly disabled checks")
    ArrayList<String> disabledChecks;

    @Override
    public Integer call() throws Exception{
        Path workingDir = Paths.get(directory);
        Path mainFilePath = workingDir.resolve(MAIN_MARKDOWN_FILE_NAME);

        String apiKey = System.getenv(API_KEY_ENV_KEY);
        if (apiKey == null || apiKey.isEmpty() ) {
            throw new Exception("API key is missing!");
        }

        boolean markdownFileExists = Files.exists(mainFilePath);
        if(!markdownFileExists) {
            throw new Exception("No " + MAIN_MARKDOWN_FILE_NAME + " file in given directory (" + directory + ")");
        }

        String baseUri = DEFAULT_BASE_URI;
        if (portalEnv.length() > 1) {
            baseUri = portalEnv;
        }

        OpenAPI apiSpec = new OpenAPI(directory);
        String openAPISpecFileName = apiSpec.getOpenAPISpecFileName();

        //String content = Files.readString(mainFilePath);
        //FrontMatterReader frontMatter = new FrontMatterReader();
        //Map<String, List<String>> data = frontMatter.readFromFile(content);

        Path swaggerPath = workingDir.resolve(openAPISpecFileName);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        GetStartedParagraphClient getStartedParagraphClient = new GetStartedParagraphClient(objectMapper, baseUri, apiKey);


        NodeModel nodeModel = new NodeClient(
                objectMapper,
                baseUri,
                apiKey)
                .get(nodeId);

        for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()){

            GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphClient(
                    objectMapper,
                    baseUri,
                    apiKey)
                    .get(getStartedDocsElement.getTargetId());

            Path docPath = workingDir.resolve(getStartedParagraph.getOrCreateFirstTitle().getValue().toLowerCase(Locale.ENGLISH).replace(" ", "-") + ".markdown");
            String markdown = Files.readString(docPath);

            DescriptionModel fieldDescription = getStartedParagraph
                    .getOrCreateFirstDescription();
            fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
            fieldDescription.setValue(markdown);

            getStartedParagraphClient.patch(getStartedDocsElement.getTargetId(), getStartedParagraph);
        }

        ApiReferenceFileModel model =
                new ApiReferenceFileClient(
                        objectMapper,
                        baseUri,
                        apiKey)
                        .upload(swaggerPath);

        NodeModel patchNodeModel = new NodeModel();
        patchNodeModel.getOrCreateFirstSourceFile().setTargetId(model.getFid().get(0).getValue());
        new NodeClient(
                objectMapper,
                baseUri,
                apiKey)
                .patch(nodeId, patchNodeModel);

       return 0;
    }
}
