package com.sprinteins.drupalcli.commands;

import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.GetStartedDocsElementModel;
import com.sprinteins.drupalcli.models.ReleaseNoteElementModel;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.sprinteins.drupalcli.paragraph.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.function.Predicate.not;

@Command(name = "export", description = "Export page")
public class Export implements Callable<Integer> {

    public static final String API_KEY_ENV_KEY = "DHL_API_DEVELOPER_PORTAL_TOKEN_FILE";
    public static final String API_DOCS_DIRECTORY = "api-docs";
    public static final String API_DOCS_IMAGE_DIRECTORY = "images";
    public static final String API_DOCS_RELEASE_NOTES_DIRECTORY = "release-notes";

     @Option(names = { "--link" }, description = "Link to the api page that needs to be exported", required = true)
     String link;
     
     @Option(names = { "--debug" }, description = "Enable debug mode")
     boolean debug;

    @Override
    public Integer call() throws Exception {
        URI uri = URI.create(link);
        String baseUri = uri.getScheme() + "://" + uri.getHost();
        String apiKey = readApiKey();

        ApplicationContext applicationContext = new ApplicationContext(baseUri, apiKey);
        NodeClient nodeClient = applicationContext.nodeClient();
        var getStartedParagraphClient = applicationContext.getStartedParagraphClient();
        var releaseNoteParagraphClient = applicationContext.releaseNoteParagraphClient();
        ImageClient imageClient = applicationContext.imageClient();
        ApiReferenceFileClient apiReferenceFileClient = applicationContext.apiReferenceFileClient();
        Converter converter = applicationContext.converter();

        Files.createDirectories(Paths.get(API_DOCS_DIRECTORY, API_DOCS_IMAGE_DIRECTORY));
        Files.createDirectories(Paths.get(API_DOCS_DIRECTORY, API_DOCS_RELEASE_NOTES_DIRECTORY));

        NodeModel nodeModel = nodeClient.getByUri(link);

        // create main.markdown
        List<String> mainMarkdown = new ArrayList<>();
        mainMarkdown.add("---");
        mainMarkdown.add("title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());
        mainMarkdown.add("menu:");


        // fetch get started elements
        for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()) {
            GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient.get(getStartedDocsElement.getTargetId());
            DescriptionModel descriptionModel = getStartedParagraph.getOrCreateFirstDescription();

            mainMarkdown.add("  - " + getStartedParagraph.getOrCreateFirstTitle().getValue());

            Document doc = Jsoup.parse(descriptionModel.getProcessed());

            // download all images
            downloadImages(imageClient, doc);

            //convert to md
            String markdown = converter.convertHtmlToMarkdown(doc.html(), link);

            //save markdown
            Files.writeString(Paths.get(API_DOCS_DIRECTORY, getStartedParagraph
                    .getOrCreateFirstTitle()
                    .getValue()
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + ".markdown"), markdown);

        }

        // fetch release notes
        for(ReleaseNoteElementModel releaseNoteElementModel: nodeModel.getReleaseNotesElement()){
            ReleaseNoteParagraphModel releaseNoteParagraphModel = releaseNoteParagraphClient.get(releaseNoteElementModel.getTargetId());
            DescriptionModel descriptionModel = releaseNoteParagraphModel.getOrCreateFirstDescription();

            Document doc = Jsoup.parse(descriptionModel.getProcessed());

            //convert to md
            String markdown = converter.convertHtmlToMarkdown(doc.html(), link);

            //save markdown
            Files.writeString(Paths.get(API_DOCS_DIRECTORY, API_DOCS_RELEASE_NOTES_DIRECTORY, releaseNoteParagraphModel
                    .getOrCreateFirstTitle()
                    .getValue()
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + ".markdown"), markdown);
        }


        // fetch api reference docs file
        String sourceFileLink = nodeModel.getOrCreateFirstSourceFile().getUrl();
        String fileName = Optional.of(Paths.get(sourceFileLink))
                .map(Path::getFileName)
                .map(Path::toString)
                .orElseThrow();
        byte[] apiReferenceBytes = apiReferenceFileClient.download(sourceFileLink);
        Files.write(Paths.get(API_DOCS_DIRECTORY, fileName), apiReferenceBytes);


        // finish up main markdown and add description list
        mainMarkdown.add("---");
        String description = nodeModel.getOrCreateFirstListDescription().getValue();
        String markdown = converter.convertHtmlToMarkdown(description, link);
        mainMarkdown.add(markdown);


        Files.write(Paths.get(API_DOCS_DIRECTORY, "main.markdown"), mainMarkdown);

        return 0;
    }

    private void downloadImages(ImageClient imageClient, Document doc) throws IOException {
        Elements images = doc.select("img");
        for(Element image: images){
            String srcAttribute = image.attr("src");
            String imageName = Optional.of(srcAttribute)
                    .filter(not(String::isBlank))
                    .map(URI::create)
                    .map(URI::getPath)
                    .map(Paths::get)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .orElseThrow();
            byte[] imageByte = imageClient.download(srcAttribute);
            Files.write(Paths.get(API_DOCS_DIRECTORY, API_DOCS_IMAGE_DIRECTORY, imageName), imageByte);
            // change source attribute
            image.attr("src", Paths.get(API_DOCS_IMAGE_DIRECTORY).resolve(imageName).toString());
        }
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
}
