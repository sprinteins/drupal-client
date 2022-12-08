package com.sprinteins.drupalcli.commands;

import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.fields.*;
import com.sprinteins.drupalcli.fieldtypes.DateValueModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.sprinteins.drupalcli.paragraph.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
            names = {"--link"},
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
        var additionalInformationParagraphClient = applicationContext.additionalInformationParagraphClient();
        var faqItemsParagraphClient = applicationContext.faqItemsParagraphClient();
        var faqItemParagraphClient = applicationContext.faqItemParagraphClient();
        var releaseNoteParagraphClient = applicationContext.releaseNoteParagraphClient();
        ImageClient imageClient = applicationContext.imageClient();
        ApiReferenceFileClient apiReferenceFileClient = applicationContext.apiReferenceFileClient();
        Converter converter = applicationContext.converter();

        System.out.println("Creating directories...");
        Files.createDirectories(apiPageDirectory.resolve(API_DOCS_IMAGE_DIRECTORY));

        System.out.println("Download node information...");
        NodeModel nodeModel = nodeClient.getByUri(link);

        // create main.markdown
        List<String> mainMarkdown = new ArrayList<>();
        mainMarkdown.add("---");
        mainMarkdown.add("title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());

        mainMarkdown.add("get-started-menu:");
        for (GetStartedDocsElementModel getStartedDocsElement : nodeModel.getGetStartedDocsElements()) {
            GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient.get(getStartedDocsElement.getTargetId());
            FormattedTextModel formattedTextModel = getStartedParagraph.getOrCreateFirstDescription();

            String paragraphTitle = getStartedParagraph.title();
            System.out.println("Download information from " + paragraphTitle + " ...");

            mainMarkdown.add("  - " + paragraphTitle);

            Document doc = Jsoup.parse(formattedTextModel.getProcessed());

            System.out.println("Downloading images...");
            downloadImages(imageClient, doc);

            List<String> markdown = new ArrayList<>();
            markdown.add("---");
            markdown.add("title: " + paragraphTitle);
            markdown.add("type: get-started-element");
            markdown.add("---");

            markdown.add(converter.convertHtmlToMarkdown(doc.html(), link));

            System.out.println("Create markdown file...");
            Files.write(apiPageDirectory.resolve(paragraphTitle
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + ".markdown"), markdown);

        }

        mainMarkdown.add("additional-information-menu:");
        for (AdditionalInformationElementModel additionalInformationElement : nodeModel.getAdditionalInformationElements()) {
            AdditionalInformationParagraphModel additionalInformationParagraph = additionalInformationParagraphClient.get(additionalInformationElement.getTargetId());
            FormattedTextModel formattedTextModel = additionalInformationParagraph.getOrCreateFirstDescription();

            String paragraphTitle = additionalInformationParagraph.title();
            System.out.println("Download information from " + paragraphTitle + " ...");

            mainMarkdown.add("  - " + paragraphTitle);

            Document doc = Jsoup.parse(formattedTextModel.getProcessed());

            System.out.println("Downloading images...");
            downloadImages(imageClient, doc);

            List<String> markdown = new ArrayList<>();
            markdown.add("---");
            markdown.add("title: " + paragraphTitle);
            markdown.add("type: additional-information-element");
            markdown.add("---");

            markdown.add(converter.convertHtmlToMarkdown(doc.html(), link));

            System.out.println("Create markdown file...");
            Files.write(apiPageDirectory.resolve(paragraphTitle
                    .toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + ".markdown"), markdown);

        }

        mainMarkdown.add("faqs:");
        for(FaqItemsModel faqItemsModel : nodeModel.getFaqItems()) {
          FaqItemsParagraphModel faqItemsParagraph = faqItemsParagraphClient.get(faqItemsModel.getTargetId());

          String paragraphTitle = faqItemsParagraph.title();
          System.out.println(("Download information from "+ paragraphTitle+"..."));

          mainMarkdown.add("  - " + paragraphTitle);

          List<String> markdown = new ArrayList<>();
          markdown.add("---");
          markdown.add("subsection-title: " + paragraphTitle);
          markdown.add("type: faqs");

          var counter = 0;

          for(FaqItemModel faqItemModel : faqItemsParagraph.getFaqItem()){
            FaqItemParagraphModel faqItemParagraph = faqItemParagraphClient.get(faqItemModel.getTargetId());

            FaqQuestionModel faqQuestion = faqItemParagraph.getOrCreateFirstQuestion();
            FaqAnswerModel faqAnswer = faqItemParagraph.getOrCreateFirstAnswer();

            String faqQuestionValue = faqQuestion.getValue();
            Document faqAnswerValue = Jsoup.parse(faqAnswer.getProcessed());


            markdown.add("question-" + counter + ": " + faqQuestionValue);
            markdown.add("answer-" + counter + ": |");
            markdown.add(converter.convertHtmlToMarkdown(faqAnswerValue.html(), link));
            counter ++;
          }

          markdown.add("---");

          System.out.println("Create markdown file...");
          Files.write(apiPageDirectory.resolve(paragraphTitle.toLowerCase(Locale.ROOT).replace(" ","-")+".markdown"),markdown);
        }

        System.out.println("Download release notes ...");
        StringBuilder stringBuilder = new StringBuilder();
        for (ReleaseNoteElementModel releaseNoteElementModel : nodeModel.getReleaseNotesElement()) {
            ReleaseNoteParagraphModel releaseNoteParagraphModel = releaseNoteParagraphClient.get(releaseNoteElementModel.getTargetId());
            FormattedTextModel formattedTextModel = releaseNoteParagraphModel.getOrCreateFirstDescription();
            StringValueModel releaseNoteTitle = releaseNoteParagraphModel.getOrCreateFirstTitle();
            DateValueModel dateValueModel = releaseNoteParagraphModel.getOrCreateFirstDate();

            Document releaseNote = new Document("");
            releaseNote.append("<h3>" + releaseNoteTitle.getValue() + "</h3>");
            releaseNote.append("<h4>" + dateValueModel.getValue() + "</h4>");
            releaseNote.append(formattedTextModel.getProcessed());

            stringBuilder.append(converter.convertHtmlToMarkdown(releaseNote.html(), link));
        }

        System.out.println("Create markdown file...");
        Files.writeString(apiPageDirectory.resolve("release-notes.markdown"), stringBuilder.toString());


        System.out.println("Download OpenAPI spec file ...");
        String sourceFileLink = nodeModel.getOrCreateFirstSourceFile().getUrl();
        String fileName = Optional.of(sourceFileLink)
                .map(URI::create)
                .map(URI::getPath)
                .map(FilenameUtils::getName)
                .orElseThrow();
        String apiReference = apiReferenceFileClient.download(sourceFileLink);
        Files.writeString(apiPageDirectory.resolve(fileName), apiReference);

        // finish up main markdown and add description list
        mainMarkdown.add("---");
        String description = nodeModel.getOrCreateFirstListDescription().getValue();
        String markdown = converter.convertHtmlToMarkdown(description, link);
        mainMarkdown.add(markdown);

        System.out.println("Creating main.markdown file...");
        Files.write(apiPageDirectory.resolve("main.markdown"), mainMarkdown);

        return 0;
    }

    private void downloadImages(ImageClient imageClient, Document doc) throws IOException, URISyntaxException {
        Elements images = doc.select("img");
        for (Element image : images) {
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
