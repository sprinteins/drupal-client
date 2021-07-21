package com.sprinteins.drupalcli.commands;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.FrontMatterReader;
import com.sprinteins.drupalcli.OpenAPI;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ApiReferenceFileModel;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.file.ImageModel;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.models.*;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "update", description = "Update description")
public class Update implements Callable<Integer> {

    public static final String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";
    public static final String API_KEY_ENV_KEY = "DHL_API_DEVELOPER_PORTAL_TOKEN_FILE";
    public static final String MAIN_MARKDOWN_FILE_NAME = "main.markdown";
    public static final String IMAGE_FOLDER_NAME = "images";
    public static final String APIDOCS_FOLDER_NAME = "api-docs";


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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestampString = String.valueOf(timestamp.getTime());

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

        String content = Files.readString(mainFilePath);
        FrontMatterReader frontMatter = new FrontMatterReader();
        Map<String, List<String>> data = frontMatter.readFromFile(content);
        List<String> titleList = data.get("title");
        String title = titleList.get(0).replace(" ", "").toLowerCase(Locale.ROOT);


        Path swaggerPath = workingDir.resolve(openAPISpecFileName);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        GetStartedParagraphClient getStartedParagraphClient = new GetStartedParagraphClient(
                objectMapper,
                baseUri,
                apiKey);


        NodeModel nodeModel = new NodeClient(
                objectMapper,
                baseUri,
                apiKey)
                .get(nodeId);

        for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()){
            System.out.println("Updating paragraphs...");

            GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphClient(
                    objectMapper,
                    baseUri,
                    apiKey)
                    .get(getStartedDocsElement.getTargetId());

            Path docPath = workingDir.resolve(getStartedParagraph.getOrCreateFirstTitle().getValue().toLowerCase(Locale.ROOT).replace(" ", "-") + ".markdown");
            String markdown = Files.readString(docPath);

            // remove frontmatter
            String cleanedMarkdown = removeFrontmatter(markdown);

            // get all the images
            Path imageFolder = workingDir.resolve(IMAGE_FOLDER_NAME);
            Set<Path> setOfImages = listFilesUsingFilesList(imageFolder);

            // loop over set and search in markdown -> replace with new imagesource
            for (Path image :setOfImages){
                System.out.println("Uploading images...:");
                System.out.println("Working on: " + image.getFileName());

                // create new image model
                ImageModel imageModel = new ImageModel();

                // init ImageModel
                LinksModel links = new LinksModel();
                TypeModel linkType = new TypeModel();
                List<UriValueModel> listOfUri = new ArrayList<>();
                UriValueModel uri = new UriValueModel();
                StringValueModel filename = new StringValueModel();
                StringValueModel filemime = new StringValueModel();
                List<StringValueModel> listOfData = new ArrayList<>();
                StringValueModel imageData = new StringValueModel();



                // get base64 string from file
                String base64Image = encodeImage(image);

                // create data list
                imageData.setValue(base64Image);
                listOfData.add(imageData);


                // modify model with data
                uri.setValue("public://" + APIDOCS_FOLDER_NAME + "/" + title + "/" + timestampString + "/" + image);
                listOfUri.add(uri);
                linkType.setHref(baseUri + "/rest/type/file/image");
                links.setType(linkType);
                Optional.ofNullable(image.getFileName()).map(Path::toString).ifPresent(filename::setValue);
                filemime.setValue("image/png");


                imageModel.setLinks(links);
                imageModel.setUri(listOfUri);
                imageModel.setFilename(filename);
                imageModel.setFilemime(filemime);
                imageModel.setData(listOfData);



                new ImageClient(
                        objectMapper,
                        baseUri,
                        apiKey)
                        .post(imageModel);


                cleanedMarkdown = cleanedMarkdown.replace(IMAGE_FOLDER_NAME + "/" + image.getFileName(),   "/sites/default/files/" + APIDOCS_FOLDER_NAME + "/" + title + "/" + timestampString + "/" + image);
                System.out.println("Finished");
            }

            DescriptionModel fieldDescription = getStartedParagraph
                    .getOrCreateFirstDescription();
            fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
            fieldDescription.setValue(cleanedMarkdown);

            getStartedParagraphClient.patch(getStartedDocsElement.getTargetId(), getStartedParagraph);
            System.out.println("Finished paragraphs");
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

    public String removeFrontmatter(String markdown){
        String[] cleanedMarkdown = markdown.split("---");
        return cleanedMarkdown[cleanedMarkdown.length-1];
    }

    public Set<Path> listFilesUsingFilesList(Path dir) throws IOException {
            return Files.list(dir)
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toSet());
    }

    public static String encodeImage(Path file) {
        try {
            return Base64.getEncoder().encodeToString(Files.readAllBytes(file));
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        return "";
    }
}

