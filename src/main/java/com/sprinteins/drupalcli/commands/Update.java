package com.sprinteins.drupalcli.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.FrontMatterReader;
import com.sprinteins.drupalcli.YamlFinder;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.fields.*;
import com.sprinteins.drupalcli.fieldtypes.DateValueModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;
import com.sprinteins.drupalcli.fieldtypes.TextFormat;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.FileUploadModel;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.sprinteins.drupalcli.paragraph.*;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

@Command(
        name = "update",
        description = "Update description"
)
public class Update implements Callable<Integer> {

    public static final String MAIN_MARKDOWN_FILE_NAME = "main.markdown";
    public static final String RELEASE_NOTES_MARKDOWN_FILE_NAME = "release-notes.markdown";
    public static final String IMAGE_FOLDER_NAME = "images";



    @Mixin
    private GlobalOptions globalOptions;

    @Option(
            names = {"--link"},
            description = "Link to the api page that needs to be exported",
            required = true
    )
    String link;

    @Option(
            names = {"--explicitly-disable-checks", "-e"},
            description = "Explicitly disabled checks"
    )
    ArrayList<String> disabledChecks;

    @Override
    public Integer call() throws Exception {
        URI uri = URI.create(link);
        String baseUri = uri.getScheme() + "://" + uri.getHost();
        Path workingDir = globalOptions.apiPageDirectory;
        Path mainFilePath = workingDir.resolve(MAIN_MARKDOWN_FILE_NAME);
        Path releaseNoteFilePath = workingDir.resolve(RELEASE_NOTES_MARKDOWN_FILE_NAME);

        if (!Files.exists(mainFilePath)) {
            throw new Exception("No " + MAIN_MARKDOWN_FILE_NAME + " file in given directory (" + workingDir + ")");
        }

        if (!Files.exists(releaseNoteFilePath)) {
            throw new IllegalStateException("File " + releaseNoteFilePath + " not found");
        }

        Path swaggerPath = YamlFinder.findYamlFile(workingDir);

        boolean valid = validate(String.valueOf(swaggerPath));
        if(!valid) {
            throw new Exception("Swagger " + swaggerPath + " is invalid");
        }

        String mainFileContent = readFile(mainFilePath);

        Map<String, List<String>> frontmatter = new FrontMatterReader().readFromString(mainFileContent);
        String title = frontmatter.get("title").get(0);
        List<String> getStartedMenuItems = frontmatter.get("get-started-menu");
        List<String> additionalInformationMenuItems = frontmatter.get("additional-information-menu");
        List<String> faqItemsSection = frontmatter.get("faqs");

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

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        NodeModel nodeModel = nodeClient.getByUri(link);
        Long nodeId = nodeModel.getOrCreateFirstNid().getValue();

        System.out.println("Updating node: " + title + " - " + nodeId + " ...");

        if (!title.equals(nodeModel.getOrCreateFirstDisplayTitle().getValue())) {
            throw new Exception(
                    "The page titles do not match. Are you updating the wrong API page?\n" + " Supplied title: " + title
                            + "\n" + " Target title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());
        }

        var getStartedDocsElements = new ArrayList<>(nodeModel.getGetStartedDocsElements());

        for (int i = 0; i < getStartedMenuItems.size(); i++) {

            String menuItem = getStartedMenuItems.get(i);
            System.out.println("Updating paragraph: " + menuItem + " ...");

            GetStartedParagraphModel getStartedParagraph;
            if (i > getStartedDocsElements.size() - 1) {
                getStartedParagraph = GetStartedParagraphModel.create(menuItem, FormattedTextModel.basicHtml("..."));
                getStartedParagraph = getStartedParagraphClient.post(getStartedParagraph);
                getStartedDocsElements.add(new GetStartedDocsElementModel(getStartedParagraph));
            } else {
                getStartedParagraph = getStartedParagraphClient
                        .get(getStartedDocsElements.get(i).getTargetId());
            }

            System.out.println("Updating paragraph: " + getStartedParagraph.id() + " ...");

            Path docPath = workingDir.resolve(
                    menuItem.toLowerCase(Locale.ROOT).replace(" ", "-")
                            + ".markdown");

            if (!Files.exists(docPath)) {
                throw new IllegalStateException("File " + docPath + " not found");
            }

            getStartedParagraph.getOrCreateFirstTitle().setValue(menuItem);

            Document currentParagraphDocument = Jsoup
                    .parse(getStartedParagraph.getOrCreateFirstDescription().getProcessed());
            Document newParagraphDocument = Jsoup
                    .parse(converter.convertMarkdownToHtml(Files.readString(docPath)));


            for (Element imageElement : newParagraphDocument.select("img")) {

                if (imageElement.hasAttr("data-entity-type")) {
                    continue;
                }

                String imageSrc = imageElement.attr("src");
                Path imagePath = workingDir.resolve(imageSrc);

                if (!Files.exists(imagePath)) {
                    throw new IllegalStateException("File " + imagePath + " not found");
                }

                String filename = Optional.of(imagePath)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElseThrow();
                System.out.println("Uploading " + filename + "...");

                Elements images = newParagraphDocument.select("img[src=\"" + imageSrc + "\"]");
                images.attr("data-entity-type", "file");

                String md5 = imageClient.generateMd5Hash(imagePath);

                Element currentImage = currentParagraphDocument.selectFirst("img[src^=\"/sites/default/files/api-docs/" + md5 + "\"]");
                if (currentImage != null && currentImage.hasAttr("data-entity-uuid")) {
                    images.attr("src", currentImage.attr("src"));
                    images.attr("data-entity-uuid", currentImage.attr("data-entity-uuid"));
                } else {
                    FileUploadModel imageModel = imageClient.upload(imagePath, md5 + imagePath.getFileName());
                    images.attr("src", imageModel.getOrCreateFirstUri().getUrl());
                    images.attr("data-entity-uuid", imageModel.getOrCreateFirstUuid().getValue());
                }
            }

            FormattedTextModel fieldDescription = getStartedParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(TextFormat.BASIC_HTML);
            fieldDescription.setValue(newParagraphDocument.body().html());

            getStartedParagraphClient.patch(getStartedParagraph);
            System.out.println("Finished processing paragraph: " + getStartedParagraph.id());
        }

        NodeModel patchNodeModel = new NodeModel();
        patchNodeModel.setGetStartedDocsElements(getStartedDocsElements);

        var additionalInformationElements = new ArrayList<>(nodeModel.getAdditionalInformationElements());

        for (int i = 0; i < additionalInformationMenuItems.size(); i++) {

            String menuItem = additionalInformationMenuItems.get(i);
            System.out.println("Updating paragraph: " + menuItem + " ...");

            AdditionalInformationParagraphModel additionalInformationParagraph;
            if (i > additionalInformationElements.size() - 1) {
                additionalInformationParagraph = AdditionalInformationParagraphModel.create(menuItem, FormattedTextModel.basicHtml("..."));
                additionalInformationParagraph = additionalInformationParagraphClient.post(additionalInformationParagraph);
                additionalInformationElements.add(new AdditionalInformationElementModel(additionalInformationParagraph));
            } else {
                additionalInformationParagraph = additionalInformationParagraphClient
                        .get(additionalInformationElements.get(i).getTargetId());
            }

            System.out.println("Updating paragraph: " + additionalInformationParagraph.id() + " ...");

            Path docPath = workingDir.resolve(
                    menuItem.toLowerCase(Locale.ROOT).replace(" ", "-")
                            + ".markdown");

            if (!Files.exists(docPath)) {
                throw new IllegalStateException("File " + docPath + " not found");
            }

            additionalInformationParagraph.getOrCreateFirstTitle().setValue(menuItem);

            Document currentParagraphDocument = Jsoup
                    .parse(additionalInformationParagraph.getOrCreateFirstDescription().getProcessed());
            Document newParagraphDocument = Jsoup
                    .parse(converter.convertMarkdownToHtml(Files.readString(docPath)));

            for (Element imageElement : newParagraphDocument.select("img")) {

                if (imageElement.hasAttr("data-entity-type")) {
                    continue;
                }

                String imageSrc = imageElement.attr("src");
                Path imagePath = workingDir.resolve(imageSrc);

                if (!Files.exists(imagePath)) {
                    throw new IllegalStateException("File " + imagePath + " not found");
                }

                String filename = Optional.of(imagePath)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElseThrow();
                System.out.println("Uploading " + filename + "...");

                Elements images = newParagraphDocument.select("img[src=\"" + imageSrc + "\"]");
                images.attr("data-entity-type", "file");

                String md5 = imageClient.generateMd5Hash(imagePath);

                Element currentImage = currentParagraphDocument.selectFirst("img[src^=\"/sites/default/files/api-docs/" + md5 + "\"]");
                if (currentImage != null && currentImage.hasAttr("data-entity-uuid")) {
                    images.attr("src", currentImage.attr("src"));
                    images.attr("data-entity-uuid", currentImage.attr("data-entity-uuid"));
                } else {
                    FileUploadModel imageModel = imageClient.upload(imagePath, md5 + imagePath.getFileName());
                    images.attr("src", imageModel.getOrCreateFirstUri().getUrl());
                    images.attr("data-entity-uuid", imageModel.getOrCreateFirstUuid().getValue());
                }
            }

            FormattedTextModel fieldDescription = additionalInformationParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(TextFormat.BASIC_HTML);
            fieldDescription.setValue(newParagraphDocument.body().html());

            additionalInformationParagraphClient.patch(additionalInformationParagraph);
            System.out.println("Finished processing paragraph: " + additionalInformationParagraph.id());
        }

        patchNodeModel.setAdditionalInformationElementsElements(additionalInformationElements);


        var faqSectionElements = new ArrayList<>(nodeModel.getFaqItems());

        for (int i = 0; i < faqItemsSection.size(); i++) {

            String menuItem = faqItemsSection.get(i);/*
            menuItem.get("question");

            for (int j = 0; j < menuItem.size(); j++){
                menuItem.get("question");

            }*/


            System.out.println("Updating paragraph: " + menuItem + " ...");

            FaqItemsParagraphModel faqItemsParagraph;
            if (i > faqSectionElements.size() - 1) {
                faqItemsParagraph = FaqItemsParagraphModel.create(menuItem);
                faqItemsParagraph = faqItemsParagraphClient.post(faqItemsParagraph);
                faqSectionElements.add(new FaqItemsModel(faqItemsParagraph));
            } else {
                faqItemsParagraph = faqItemsParagraphClient
                        .get(faqSectionElements.get(i).getTargetId());
            }

            System.out.println("Updating paragraph: " + faqItemsParagraph.id() + " ...");

            Path docPath = workingDir.resolve(menuItem.toLowerCase(Locale.ROOT).replace(" ", "-")
                    + ".markdown");
            String faqSectionContent = readFile(docPath);
            Map<String, List<String>> frontmatterFaq = new FrontMatterReader().readFromString(faqSectionContent);

            FaqItemParagraphModel faqItemParagraphModel = new FaqItemParagraphModel();
            List<FaqQuestionModel> faqQuestionModelList = new ArrayList<>();
            List<FaqAnswerModel> faqAnswerModelList = new ArrayList<>();
            List<String> qAndA = frontmatterFaq.get("q-and-a");

            /*System.out.println(qAndA);*/


            for (var k = 0; k < qAndA.size(); k++) {
                if(qAndA.get(k).contains("question:")) {
                    String questionString = qAndA.get(k);
                    var question = questionString.split("question: ");
                    FaqQuestionModel questionModel = new FaqQuestionModel();
                    questionModel.setValue(question[1]);
                    faqQuestionModelList.add(questionModel);
                }

                if(qAndA.get(k).contains("answer:")) {
                    String answerString = qAndA.get(k);
                    var answer = answerString.split("answer: ");
                    FaqAnswerModel answerModel = new FaqAnswerModel();
                    answerModel.setValue(answer[1]);
                    faqAnswerModelList.add(answerModel);                }
            }

            faqItemParagraphModel.setQuestion(faqQuestionModelList);
            faqItemParagraphModel.setAnswer(faqAnswerModelList);
            faqItemParagraphModel.getOrCreateFirstId();
            faqItemParagraphModel.getOrCreateFirstRevisionId();
            faqItemParagraphModel.getOrCreateFirstUuid();
            faqItemParagraphModel.getOrCreateFirstTitle();
            faqItemParagraphModel.getOrCreateFirstDescription();

            System.out.println(faqItemParagraphModel.id());


            faqItemParagraphClient.patch(faqItemParagraphModel);

            if (!Files.exists(docPath)) {
                throw new IllegalStateException("File " + docPath + " not found");
            }

            faqItemsParagraph.getOrCreateFirstTitle().setValue(menuItem);

/*            Document currentParagraphDocument = Jsoup
                    .parse(faqItemsParagraph.getOrCreateFirstDescription().getProcessed()); */
            Document newParagraphDocument = Jsoup
                    .parse(converter.convertMarkdownToHtml(Files.readString(docPath)));

/*            for (Element imageElement : newParagraphDocument.select("img")) {

                if (imageElement.hasAttr("data-entity-type")) {
                    continue;
                }

                String imageSrc = imageElement.attr("src");
                Path imagePath = workingDir.resolve(imageSrc);

                if (!Files.exists(imagePath)) {
                    throw new IllegalStateException("File " + imagePath + " not found");
                }

                String filename = Optional.of(imagePath)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElseThrow();
                System.out.println("Uploading " + filename + "...");

                Elements images = newParagraphDocument.select("img[src=\"" + imageSrc + "\"]");
                images.attr("data-entity-type", "file");

                String md5 = imageClient.generateMd5Hash(imagePath);

                Element currentImage = currentParagraphDocument.selectFirst("img[src^=\"/sites/default/files/api-docs/" + md5 + "\"]");
                if (currentImage != null && currentImage.hasAttr("data-entity-uuid")) {
                    images.attr("src", currentImage.attr("src"));
                    images.attr("data-entity-uuid", currentImage.attr("data-entity-uuid"));
                } else {
                    FileUploadModel imageModel = imageClient.upload(imagePath, md5 + imagePath.getFileName());
                    images.attr("src", imageModel.getOrCreateFirstUri().getUrl());
                    images.attr("data-entity-uuid", imageModel.getOrCreateFirstUuid().getValue());
                }
            } */

            faqItemsParagraphClient.patch(faqItemsParagraph);
            System.out.println("Finished processing paragraph: " + faqItemsParagraph.id());
        }

        NodeModel faqNodeModel = new NodeModel();
        faqNodeModel.setFaqItems(faqSectionElements);







        Document newReleaseNoteDocument = Jsoup
                .parse(converter.convertMarkdownToHtml(Files.readString(releaseNoteFilePath)));

        Element releaseNoteBody = newReleaseNoteDocument.body();

        for (ReleaseNoteElementModel releaseNoteElement : nodeModel.getReleaseNotesElement()) {
            System.out.println("Updating release note: " + releaseNoteElement.getTargetId() + " ...");

            Element releaseNote = extractFirstReleaseNote(releaseNoteBody);

            ReleaseNoteParagraphModel releaseNoteParagraph = releaseNoteParagraphClient
                    .get(releaseNoteElement.getTargetId());

            StringValueModel releaseNoteTitle = releaseNoteParagraph.getOrCreateFirstTitle();
            DateValueModel dateValueModel = releaseNoteParagraph.getOrCreateFirstDate();

            releaseNoteTitle.setValue(releaseNote.select("h3").html());
            dateValueModel.setValue(releaseNote.select("h4").html());
            releaseNote.select("h3").remove();
            releaseNote.select("h4").remove();

            FormattedTextModel fieldDescription = releaseNoteParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(TextFormat.BASIC_HTML);
            fieldDescription.setValue(releaseNote.html());

            Set<ConstraintViolation<DateValueModel>> constraintViolations =
                    validator.validate( dateValueModel );

            if (constraintViolations.size() > 0){
                for(ConstraintViolation<DateValueModel> validation : constraintViolations ) {
                    System.out.print("Release Note " + releaseNoteElement.getTargetId() + " has the following validation error: " + validation.getMessage());
                }
                throw new Exception("Aborting the update of: " + title + "\nSee error above for more details. ");
            }

            releaseNoteParagraphClient.patch(releaseNoteElement.getTargetId(), releaseNoteParagraph);
            System.out.println("Finished processing release notes: " + releaseNoteElement.getTargetId());
        }

        patchNodeModel.setReleaseNotesElement(nodeModel.getReleaseNotesElement());

        while (releaseNoteBody.childNodeSize() > 0) {
            System.out.println("There is a new entry to the release notes. Creating new paragraph... ");

            var currentReleaseNotesList = patchNodeModel.getReleaseNotesElement();

            List<ReleaseNoteElementModel> newReleaseNoteElementList = new ArrayList<>();

            for (ReleaseNoteElementModel releaseNoteElementModel : currentReleaseNotesList) {
                newReleaseNoteElementList.add(releaseNoteElementModel);
            }

            Element releaseNote = extractFirstReleaseNote(releaseNoteBody);

            ReleaseNoteParagraphModel releaseNoteParagraph = new ReleaseNoteParagraphModel();

            StringValueModel releaseNoteTitle = releaseNoteParagraph.getOrCreateFirstTitle();
            DateValueModel dateValueModel = releaseNoteParagraph.getOrCreateFirstDate();

            releaseNoteTitle.setValue(releaseNote.select("h3").html());
            dateValueModel.setValue(releaseNote.select("h4").html());
            releaseNote.select("h3").remove();
            releaseNote.select("h4").remove();

            FormattedTextModel fieldDescription = releaseNoteParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(TextFormat.BASIC_HTML);
            fieldDescription.setValue(releaseNote.html());
            ReleaseNoteParagraphModel newReleaseNoteParagraph = releaseNoteParagraphClient.post(releaseNoteParagraph);

            ReleaseNoteElementModel newReleaseNoteElement = new ReleaseNoteElementModel();
            newReleaseNoteElement.setTargetId(newReleaseNoteParagraph.getOrCreateFirstId().getValue());
            newReleaseNoteElement.setTargetRevisionId(newReleaseNoteParagraph.getOrCreateFirstRevisionId().getValue());
            newReleaseNoteElement.setTargetUuid(newReleaseNoteParagraph.getOrCreateFirstUuid().getValue());

            newReleaseNoteElementList.add(newReleaseNoteElement);
            patchNodeModel.setReleaseNotesElement(newReleaseNoteElementList);
        }

        String currentApiReference = apiReferenceFileClient.download(nodeModel.getOrCreateFirstSourceFile().getUrl());
        String newApiReference = Files.readString(swaggerPath);
        if (!currentApiReference.equals(newApiReference)) {
            FileUploadModel apiReferenceModel = apiReferenceFileClient.upload(swaggerPath);
            patchNodeModel.getOrCreateFirstSourceFile().setTargetId(apiReferenceModel.getFid().get(0).getValue());
        }

        Document newMainDocument = Jsoup
                .parse(converter.convertMarkdownToHtml(Files.readString(mainFilePath)));
        patchNodeModel.getOrCreateFirstListDescription().setValue(newMainDocument.body().html());
        patchNodeModel.getOrCreateFirstListDescription().setFormat(TextFormat.BASIC_HTML);

        nodeClient.patch(nodeId, patchNodeModel);

        System.out.println("Finished processing node: " + nodeId);
        return 0;
    }

    public Element extractFirstReleaseNote(Element releaseNoteBody) {
        Elements headlines = releaseNoteBody.select("h3");

        List<Integer> titleIds = new ArrayList<>();

        for (Element headline : headlines) {
            titleIds.add(headline.siblingIndex());
        }

        Document releaseNoteDocument = Jsoup.parse("");

        if (titleIds.size() > 1) {
            for (int index = titleIds.get(0); index < titleIds.get(1); index++) {
                releaseNoteDocument.body().append(releaseNoteBody.childNode(index).outerHtml());
            }

            for (int index = titleIds.get(0); index < titleIds.get(1); index++) {
                releaseNoteBody.childNode(0).remove();
            }
        } else {
            for (int index = 0; index < releaseNoteBody.childNodeSize(); index++) {
                releaseNoteDocument.body().append(releaseNoteBody.childNode(index).outerHtml());
            }

            while (releaseNoteBody.childNodeSize() > 0) {
                releaseNoteBody.childNode(0).remove();
            }
        }

        return releaseNoteDocument.body();
    }

    String getSwaggerString(String swaggerPath) throws IOException {
        final String adjustedLocation = swaggerPath.replaceAll("\\\\", "/");
        final Path path = adjustedLocation.toLowerCase(Locale.ROOT).startsWith("file:") ?
                Paths.get(URI.create(adjustedLocation)) : Paths.get(adjustedLocation);

        return FileUtils.readFileToString(path.toFile(), "UTF-8");
    }

    boolean validate(String swaggerPath) {
        boolean validationResult = true;
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.readTree(getSwaggerString(swaggerPath));
        } catch (Exception error) {
            System.out.println("Validation of the OpenAPI Specification file(" + swaggerPath + ") failed! " + error);
            validationResult = false;
        }

        return validationResult;
    }

    public String readFile(Path filePath) {
        String result = "";
        try {
            result = Files.readString(filePath);
        } catch (IOException error) {
            throw new IllegalStateException("Wrong encoding for " + MAIN_MARKDOWN_FILE_NAME + ". Please make sure the file is UTF-8 encoded.");
        }
        return result;
    }
}
