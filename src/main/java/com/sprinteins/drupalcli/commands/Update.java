package com.sprinteins.drupalcli.commands;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.FrontMatterReader;
import com.sprinteins.drupalcli.OpenAPI;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.FileUploadModel;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.GetStartedDocsElementModel;
import com.sprinteins.drupalcli.models.ValueFormat;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.sprinteins.drupalcli.paragraph.GetStartedParagraphModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.ProxySelector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Command(name = "update", description = "Update description")
public class Update implements Callable<Integer> {

    public static final String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";
    public static final String MAIN_MARKDOWN_FILE_NAME = "main.markdown";
    public static final String IMAGE_FOLDER_NAME = "images";
    
    @Mixin
    private GlobalOptions globalOptions;

    @Option(names = { "--api-page" , "-a"}, description = "API page ID", required = true)
    Long nodeId;

    @Option(names = { "--portal-environment" , "-p"}, description = "Portal environment to update")
    String portalEnv;

    @Option(names = { "--explicitly-disable-checks" , "-e"}, description = "Explicitly disabled checks")
    ArrayList<String> disabledChecks;

    @Override
    public Integer call() throws Exception{
        ProxySearch proxySearch = new ProxySearch();
        proxySearch.addStrategy(Strategy.ENV_VAR);
        ProxySelector proxySelector = proxySearch.getProxySelector();
        if (proxySelector != null) {
            ProxySelector.setDefault(proxySelector);
        }

        Path workingDir = globalOptions.apiPageDirectory;
        Path mainFilePath = workingDir.resolve(MAIN_MARKDOWN_FILE_NAME);

        String apiKey = readApiKey();

        boolean markdownFileExists = Files.exists(mainFilePath);
        if(!markdownFileExists) {
            throw new Exception("No " + MAIN_MARKDOWN_FILE_NAME + " file in given directory (" + workingDir + ")");
        }

        String baseUri = DEFAULT_BASE_URI;
        if (portalEnv.length() > 1) {
            baseUri = portalEnv;
        }
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 1);
        }

        OpenAPI apiSpec = new OpenAPI(workingDir);
        String openAPISpecFileName = apiSpec.getOpenAPISpecFileName();

        String content = Files.readString(mainFilePath);
        FrontMatterReader frontMatter = new FrontMatterReader();
        Map<String, List<String>> data = frontMatter.readFromString(content);
        List<String> titleList = data.get("title");
        String title = titleList.get(0);

        Path swaggerPath = workingDir.resolve(openAPISpecFileName);
        
        ApplicationContext applicationContext = new ApplicationContext(baseUri, apiKey);
        NodeClient nodeClient = applicationContext.nodeClient();
        var getStartedParagraphClient = applicationContext.getStartedParagraphClient();
        ImageClient imageClient = applicationContext.imageClient();
        ApiReferenceFileClient apiReferenceFileClient = applicationContext.apiReferenceFileClient();

        System.out.println("Updating node: " + title + " - " + nodeId + " ...");
        
        NodeModel nodeModel = nodeClient.get(nodeId);
        
        if (!title.equals(nodeModel.getOrCreateFirstDisplayTitle().getValue())) {
            throw new Exception("The page titles do not match. Are you updating the wrong API page?\n" +
                    " Supplied title: " + title + "\n" +
                    " Target title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());
        }

        for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()){
            System.out.println("Updating paragraph: " + getStartedDocsElement.getTargetId() + " ...");

            GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient.get(getStartedDocsElement.getTargetId());

            Path docPath = workingDir.resolve(getStartedParagraph.getOrCreateFirstTitle().getValue().toLowerCase(Locale.ROOT).replace(" ", "-") + ".markdown");

            if(Files.exists(docPath)){
                String markdown = Files.readString(docPath);

                // get all the images
                Path imageFolder = workingDir.resolve(IMAGE_FOLDER_NAME);
                Set<Path> setOfImages = listFilesUsingFilesList(imageFolder);

                // loop over set and search in markdown -> replace with new imagesource
                for (Path imagePath :setOfImages){
                    String filename = Optional.ofNullable(imagePath.getFileName()).map(Path::toString).orElseThrow();
                    if(markdown.contains(filename)){
                        System.out.println("Uploading " + imagePath.getFileName() + "...");
                        FileUploadModel imageModel = imageClient.upload(imagePath);
                        markdown = replaceImageTag(markdown, imagePath, imageModel);
                    }
                }

                DescriptionModel fieldDescription = getStartedParagraph
                        .getOrCreateFirstDescription();
                fieldDescription.setFormat(ValueFormat.BASIC_HTML);
                fieldDescription.setValue(applicationContext.converter().convertMarkdownToHtml(markdown));

                getStartedParagraphClient.patch(getStartedDocsElement.getTargetId(), getStartedParagraph);
                System.out.println("Finished processing paragraph: " + getStartedDocsElement.getTargetId());
            }
            else {
                System.out.println("Skipping " + docPath + " (file is not present)");
            }
        }

        FileUploadModel apiReferenceModel =
                apiReferenceFileClient
                        .upload(swaggerPath);

        NodeModel patchNodeModel = new NodeModel();
        patchNodeModel.getOrCreateFirstSourceFile().setTargetId(apiReferenceModel.getFid().get(0).getValue());
        nodeClient
                .patch(nodeId, patchNodeModel);

        System.out.println("Finished processing node: " + nodeId);
        return 0;
    }

    private String readApiKey() {
        Path path = globalOptions.tokenFile;
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

    public Set<Path> listFilesUsingFilesList(Path dir) throws IOException {
            return Files.list(dir)
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toSet());
    }

    public List<String> findString(String input, String regex){

        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while(matcher.find()){
            matches.add(input.substring(matcher.start(), matcher.end()));
        }
        return matches;
    }

    public List<String> extractAltTexts(List<String> inputList, String regex) {

        List<String> altTexts = new ArrayList<>();

        for(String input: inputList) {
            List<String> result = findString(input, regex);
            altTexts.add(result.get(0).substring(1, result.get(0).length() - 1));
        }
        return altTexts;
    }


    public String replaceImageTag(String markdown, Path imagePath, FileUploadModel imageModel ){

        // find image string
        String imageRegexPattern = "!\\[[^]]*]\\("+ IMAGE_FOLDER_NAME + "/" + imagePath.getFileName() +"\\)";
        List<String> markdownImages = findString(markdown, imageRegexPattern);

        // get alt text
        String altTextRegexPattern = "\\[[^]]*]";
        List<String> altTexts = extractAltTexts(markdownImages, altTextRegexPattern);

        // replace image tag
        String uuid = imageModel.getUuid().get(0).getValue();
        String src = imageModel.getUri().get(0).getUrl();

        for(int index = 0; index < markdownImages.size(); index++){
            String imageTag = "<img alt=\"" + altTexts.get(index) + "\" data-align=\"center\" data-entity-type=\"file\" data-entity-uuid=\"" + uuid + "\" src=\""+ src +"\" />";
            markdown = markdown.replace(markdownImages.get(index), imageTag);
        }

        return markdown;
    }
}

