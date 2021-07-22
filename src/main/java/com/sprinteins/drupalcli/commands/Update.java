package com.sprinteins.drupalcli.commands;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.FrontMatterReader;
import com.sprinteins.drupalcli.OpenAPI;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.FileUploadModel;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.GetStartedDocsElementModel;
import com.sprinteins.drupalcli.models.ValueFormat;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Command(name = "update", description = "Update description")
public class Update implements Callable<Integer> {

    public static final String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";
    public static final String API_KEY_ENV_KEY = "DHL_API_DEVELOPER_PORTAL_TOKEN_FILE";
    public static final String MAIN_MARKDOWN_FILE_NAME = "main.markdown";
    public static final String IMAGE_FOLDER_NAME = "images";

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

        String apiKey = readApiKey();

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

        String content = Files.readString(mainFilePath);
        FrontMatterReader frontMatter = new FrontMatterReader();
        Map<String, List<String>> data = frontMatter.readFromFile(content);
        List<String> titleList = data.get("title");
        String title = titleList.get(0);

        Path swaggerPath = workingDir.resolve(openAPISpecFileName);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        GetStartedParagraphClient getStartedParagraphClient = new GetStartedParagraphClient(
                objectMapper,
                baseUri,
                apiKey);


        System.out.println("Updating node: " + title + " - " + nodeId + " ...");
        NodeModel nodeModel = new NodeClient(
                objectMapper,
                baseUri,
                apiKey)
                .get(nodeId);

        for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()){
            System.out.println("Updating paragraph: " + getStartedDocsElement.getTargetId() + " ...");

                        GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphClient(
                    objectMapper,
                    baseUri,
                    apiKey)
                    .get(getStartedDocsElement.getTargetId());

            Path docPath = workingDir.resolve(getStartedParagraph.getOrCreateFirstTitle().getValue().toLowerCase(Locale.ROOT).replace(" ", "-") + ".markdown");
            String markdown = Files.readString(docPath);

            // remove frontmatter
            String cleanedMarkdown = removeFrontmatter(markdown);
            cleanedMarkdown = correctMarkdownStructure(cleanedMarkdown);

            // get all the images
            Path imageFolder = workingDir.resolve(IMAGE_FOLDER_NAME);
            Set<Path> setOfImages = listFilesUsingFilesList(imageFolder);

            // loop over set and search in markdown -> replace with new imagesource
            for (Path imagePath :setOfImages){
                String filename = Optional.ofNullable(imagePath.getFileName()).map(Path::toString).orElseThrow();
                if(cleanedMarkdown.contains(filename)){
                    int status = new ImageClient(
                                    objectMapper,
                                    baseUri,
                                    apiKey)
                                    .head(imagePath);
                    if (status != 200) {
                        System.out.println("Uploading " + imagePath.getFileName() + "...");
                        FileUploadModel imageModel =
                                new ImageClient(
                                        objectMapper,
                                        baseUri,
                                        apiKey)
                                        .upload(imagePath);

                        if(imageModel != null){
                            cleanedMarkdown = cleanedMarkdown.replace(IMAGE_FOLDER_NAME + "/" + imagePath.getFileName(), imageModel.getUri().get(0).getUrl());
                        }
                    } else {
                        System.out.println("Skipping " +imagePath.getFileName() + " (not changed)");
                    }
                }
            }

            DescriptionModel fieldDescription = getStartedParagraph
                    .getOrCreateFirstDescription();
            fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
            fieldDescription.setValue(cleanedMarkdown);

            getStartedParagraphClient.patch(getStartedDocsElement.getTargetId(), getStartedParagraph);
            System.out.println("Finished processing paragraph: " + getStartedDocsElement.getTargetId());
        }

        FileUploadModel apiReferenceModel =
                new ApiReferenceFileClient(
                        objectMapper,
                        baseUri,
                        apiKey)
                        .upload(swaggerPath);

        NodeModel patchNodeModel = new NodeModel();
        patchNodeModel.getOrCreateFirstSourceFile().setTargetId(apiReferenceModel.getFid().get(0).getValue());
        new NodeClient(
                objectMapper,
                baseUri,
                apiKey)
                .patch(nodeId, patchNodeModel);

        System.out.println("Finished processing node: " + nodeId);
        return 0;
    }

    private String readApiKey() {
        String apiKeyFile = System.getenv(API_KEY_ENV_KEY);
        if (apiKeyFile == null || apiKeyFile.isEmpty() ) {
            throw new IllegalArgumentException("API key file not found : DHL_API_DEVELOPER_PORTAL_TOKEN_FILE environment variable not set");
        }
        Path path = Paths.get(apiKeyFile);
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

    private String correctMarkdownStructure(String markdown) {
        List<String> lines = Arrays.asList(markdown.split("\r?\n"));
        return lines.stream().map(line -> {
            // the markdown structure should support "normal" header levels starting with h1
            // but currently the API page requires the uploaded structure to start with h3
            // so we add two levels to the header level
            // this only supports atx-style headers
            if (line.startsWith("#")) {
                return "##" + line;
            }
            return line;
        }).collect(joining("\n"));
    }

    public String removeFrontmatter(String markdown){
        String[] cleanedMarkdown = markdown.split("---");
        return cleanedMarkdown[cleanedMarkdown.length-1];
    }

    public Set<Path> listFilesUsingFilesList(Path dir) throws IOException {
            return Files.list(dir)
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toSet());
    }
}

