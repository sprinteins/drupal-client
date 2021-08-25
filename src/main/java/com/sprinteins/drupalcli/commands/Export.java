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
import com.sprinteins.drupalcli.paragraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.paragraph.ReleaseNoteParagraphModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
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

@Command(
        name = "export",
        description = "Export page"
        )
public class Export implements Callable<Integer> {

    public static final String API_DOCS_IMAGE_DIRECTORY = "images";
    public static final String API_DOCS_RELEASE_NOTES_DIRECTORY = "release-notes";
    
    @Mixin
    private GlobalOptions globalOptions;

     @Option(
             names = { "--link" },
             description = "Link to the api page that needs to be exported",
             required = true
             )
     String link;
     
    @Override
    public Integer call() throws Exception {
        URI uri = URI.create(link);
        String baseUri = uri.getScheme() + "://" + uri.getHost();
        Path apiPageDirectory = globalOptions.apiPageDirectory;

        ApplicationContext applicationContext = new ApplicationContext(baseUri, globalOptions);
        NodeClient nodeClient = applicationContext.nodeClient();
        var getStartedParagraphClient = applicationContext.getStartedParagraphClient();
        var releaseNoteParagraphClient = applicationContext.releaseNoteParagraphClient();
        ImageClient imageClient = applicationContext.imageClient();
        ApiReferenceFileClient apiReferenceFileClient = applicationContext.apiReferenceFileClient();
        Converter converter = applicationContext.converter();

        System.out.println("Creating directories...");
        Files.createDirectories(apiPageDirectory.resolve(API_DOCS_IMAGE_DIRECTORY));
        Files.createDirectories(apiPageDirectory.resolve(API_DOCS_RELEASE_NOTES_DIRECTORY));

        System.out.println("Download node information...");
        NodeModel nodeModel = nodeClient.getByUri(link);

        // create main.markdown
        List<String> mainMarkdown = new ArrayList<>();
        mainMarkdown.add("---");
        mainMarkdown.add("title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());
        mainMarkdown.add("menu:");

        for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()) {
            GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient.get(getStartedDocsElement.getTargetId());
            DescriptionModel descriptionModel = getStartedParagraph.getOrCreateFirstDescription();

            String paragraphTitle = getStartedParagraph.getOrCreateFirstTitle().getValue();
            System.out.println("Download information from " + paragraphTitle + " ...");

            mainMarkdown.add("  - " + paragraphTitle);

            Document doc = Jsoup.parse(descriptionModel.getProcessed());

            System.out.println("Downloading images...");
            downloadImages(imageClient, doc);
            
            List<String> markdown = new ArrayList<>();
            markdown.add("---");
            markdown.add("title: " + paragraphTitle);
            markdown.add("---");

            markdown.add(converter.convertHtmlToMarkdown(doc.html(), link));

            System.out.println("Create markdown file...");
            Files.write(apiPageDirectory.resolve(paragraphTitle
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + ".markdown"), markdown);

        }

        System.out.println("Download release notes ...");
        for(ReleaseNoteElementModel releaseNoteElementModel: nodeModel.getReleaseNotesElement()){
            ReleaseNoteParagraphModel releaseNoteParagraphModel = releaseNoteParagraphClient.get(releaseNoteElementModel.getTargetId());
            DescriptionModel descriptionModel = releaseNoteParagraphModel.getOrCreateFirstDescription();

            Document doc = Jsoup.parse(descriptionModel.getProcessed());

            String markdown = converter.convertHtmlToMarkdown(doc.html(), link);

            System.out.println("Create markdown file...");
            Files.writeString(apiPageDirectory.resolve(API_DOCS_RELEASE_NOTES_DIRECTORY).resolve(releaseNoteParagraphModel
                    .getOrCreateFirstTitle()
                    .getValue()
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + ".markdown"), markdown);
        }


        System.out.println("Download OpenAPI spec file ...");
        String sourceFileLink = nodeModel.getOrCreateFirstSourceFile().getUrl();
        String fileName = Optional.of(sourceFileLink)
                .map(URI::create)
                .map(URI::getPath)
                .map(FilenameUtils::getName)
                .orElseThrow();
        byte[] apiReferenceBytes = apiReferenceFileClient.download(sourceFileLink);
        Files.write(apiPageDirectory.resolve(fileName), apiReferenceBytes);


        // finish up main markdown and add description list
        mainMarkdown.add("---");
        String description = nodeModel.getOrCreateFirstListDescription().getValue();
        String markdown = converter.convertHtmlToMarkdown(description, link);
        mainMarkdown.add(markdown);

        System.out.println("Creating main.markdown file...");
        Files.write(apiPageDirectory.resolve("main.markdown"), mainMarkdown);

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
                    .map(FilenameUtils::getName)
                    .orElseThrow();
            byte[] imageByte = imageClient.download(srcAttribute);
            String md5Hash = imageClient.generateMd5Hash(imageByte);
            imageName = StringUtils.removeStart(imageName, md5Hash);
            Files.write(globalOptions.apiPageDirectory.resolve(API_DOCS_IMAGE_DIRECTORY).resolve(imageName), imageByte);
            // change source attribute
            image.attr("src", Paths.get(API_DOCS_IMAGE_DIRECTORY).resolve(imageName).toString());
        }
    }

}
