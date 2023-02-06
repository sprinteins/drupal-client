package com.sprinteins.drupalcli.commands;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.FrontMatterReader;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.fields.*;
import com.sprinteins.drupalcli.fieldtypes.DateValueModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;
import com.sprinteins.drupalcli.fieldtypes.TextFormat;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.DownloadFileClient;
import com.sprinteins.drupalcli.file.FileFinder;
import com.sprinteins.drupalcli.file.FileUploadModel;
import com.sprinteins.drupalcli.file.ImageClient;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.sprinteins.drupalcli.paragraph.*;
import com.sprinteins.drupalcli.translations.AvailableTranslationsModel;
import com.sprinteins.drupalcli.translations.TranslationClient;
import com.sprinteins.drupalcli.translations.TranslationModel;

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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
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
    public static final String API_DOCS_DOWNLOADS_DIRECTORY = "downloads";

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
    @Option(
            names = {"--use-json"},
            description = "Use JSON file to update api page"
    )
    boolean useJson;

    @Option(
            names = {"--lang"},
            description = "Enter langcode to update api page for a specific translation"
    )
    String langcodeInput = "en";

    @Option(
            names = {"--all-lang"},
            description = "Update all translations that exist for an api page"
    )
    boolean updateAllLanguages = false;


    @Override
    public Integer call() throws Exception {
        URI uri = URI.create(link);
        String baseUri = uri.getScheme() + "://" + uri.getHost();
        Path workingBaseDir = globalOptions.apiPageDirectory;
        ApplicationContext applicationContext = new ApplicationContext(baseUri, globalOptions);
        NodeClient nodeClient = applicationContext.nodeClient();
        TranslationClient translationClient = applicationContext.translationClient();
        NodeModel nodeModel = nodeClient.getByUri(link);
        Long nodeId = nodeModel.getOrCreateFirstNid().getValue();

        var getTranslations = translationClient.getTranslations(nodeId);
        var translationsListModel = new AvailableTranslationsModel(getTranslations);

        if(!translationsListModel.validate(langcodeInput)){
            throw new Exception ("The entered language code does not exist for this api page. (\""+ langcodeInput + "\" entered)\nUse one of the following: \n"+ translationsListModel.printValues());
        };

        ArrayList<String> translationsSet = new ArrayList<>();
        if (updateAllLanguages) {
            boolean localTranslationsMatched = false;
            translationsSet = compareLanguagesData(getTranslations, workingBaseDir);
        } else {
            translationsSet.add(langcodeInput);
        }

        for (String langCode:translationsSet) {

            nodeModel = nodeClient.getTranslatedNode(nodeId, langCode);

            Path workingDir= workingBaseDir.resolve(langCode);
            Path mainFilePath = workingDir.resolve(MAIN_MARKDOWN_FILE_NAME);
            Path releaseNoteFilePath = workingDir.resolve(RELEASE_NOTES_MARKDOWN_FILE_NAME);

            if (!Files.exists(mainFilePath)) {
                throw new Exception("No " + MAIN_MARKDOWN_FILE_NAME + " file in given directory (" + workingDir + ")");
            }
            checkIfFileExists(releaseNoteFilePath);

            Path swaggerPath;
            if(useJson){
                swaggerPath = FileFinder.findJsonFile(workingDir);
            }else {
                swaggerPath = FileFinder.findYamlFile(workingDir);
            }

            if(!validate(String.valueOf(swaggerPath))) {
                throw new Exception("Swagger " + swaggerPath + " is invalid");
            }

            String mainFileContent = readFile(mainFilePath);
            Map<String, List<String>> frontmatter = new FrontMatterReader().readFromString(mainFileContent);
            String title = frontmatter.get("title").get(0);
            List<String> getStartedMenuItems = frontmatter.get("get-started-menu");
            List<String> additionalInformationMenuItems = frontmatter.get("additional-information-menu");
            List<String> faqSectionItems = frontmatter.get("faqs");

            Path docPath = workingDir.resolve("downloads.markdown");
            checkIfFileExists(docPath);
            String downloadsSection = Files.readString(docPath);
            Map<String, List<String>> frontmatterDownloads = new FrontMatterReader().readFromString(downloadsSection);




            var getStartedParagraphClient = applicationContext.getStartedParagraphClient();
            var additionalInformationParagraphClient = applicationContext.additionalInformationParagraphClient();
            var faqItemsParagraphClient = applicationContext.faqItemsParagraphClient();
            var faqItemParagraphClient = applicationContext.faqItemParagraphClient();
            var releaseNoteParagraphClient = applicationContext.releaseNoteParagraphClient();
            var downloadsElementParagraphClient = applicationContext.downloadsElementParagraphClient();


            ImageClient imageClient = applicationContext.imageClient();
            ApiReferenceFileClient apiReferenceFileClient = applicationContext.apiReferenceFileClient();
            DownloadFileClient downloadFileClient = applicationContext.downloadFileClient();
            Converter converter = applicationContext.converter();
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();



            System.out.println("Updating node: " + title + " - " + nodeId + " ...");

            if (!title.equals(nodeModel.getOrCreateFirstDisplayTitle().getValue())) {
                throw new Exception(
                        "The page titles do not match. Are you updating the wrong API page?\n" + " Supplied title: " + title
                                + "\n" + " Target title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());
            }

            NodeModel patchNodeModel = new NodeModel();

            updateGetStartedSection(workingDir, getStartedMenuItems, getStartedParagraphClient, imageClient, converter, nodeModel, patchNodeModel);

            updateAdditionalInfoSection(workingDir, additionalInformationMenuItems, additionalInformationParagraphClient, imageClient, converter, nodeModel, patchNodeModel);

            updateFaqSection(workingDir, faqSectionItems, faqItemsParagraphClient, faqItemParagraphClient, converter, nodeModel, patchNodeModel);

            updateDownloadsSection (workingDir, frontmatterDownloads, downloadsElementParagraphClient, downloadFileClient, converter, nodeModel, patchNodeModel);

            Document newReleaseNoteDocument = Jsoup
                    .parse(converter.convertMarkdownToHtml(Files.readString(releaseNoteFilePath)));
            Element releaseNoteBody = newReleaseNoteDocument.body();

            updateReleaseNoteSection(title, releaseNoteParagraphClient, validator, nodeModel, patchNodeModel, releaseNoteBody);


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

            ObjectMapper mapper;
            if(this.useJson) {
                mapper = new ObjectMapper(new JsonFactory());
            } else{
                mapper = new ObjectMapper(new YAMLFactory());
            }
            var mapperNode = mapper.readTree(getSwaggerString(String.valueOf(swaggerPath)));
            try {
                var versionNode = mapperNode.findValue("version").toString();
                StringValueModel version = new StringValueModel();
                version.setValue(versionNode.replace("\"", ""));
                List<StringValueModel> versionList = new ArrayList<>();
                versionList.add(version);
                patchNodeModel.setVersion(versionList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            Document newMainDocument = Jsoup
                    .parse(converter.convertMarkdownToHtml(Files.readString(mainFilePath)));
            patchNodeModel.getOrCreateFirstListDescription().setValue(newMainDocument.body().html());
            patchNodeModel.getOrCreateFirstListDescription().setFormat(TextFormat.BASIC_HTML);
            nodeClient.patch(nodeId, patchNodeModel);

            System.out.println("Finished processing node: " + nodeId);
        }

        return 0;

    }

    private void updateReleaseNoteSection(String title, ParagraphClient<ReleaseNoteParagraphModel> releaseNoteParagraphClient, Validator validator, NodeModel nodeModel, NodeModel patchNodeModel, Element releaseNoteBody) throws Exception {
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
    }

    private static void updateGetStartedSection(Path workingDir, List<String> getStartedMenuItems, ParagraphClient<GetStartedParagraphModel> getStartedParagraphClient, ImageClient imageClient, Converter converter, NodeModel nodeModel, NodeModel patchNodeModel) throws IOException, NoSuchAlgorithmException {
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
            checkIfFileExists(docPath);
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
                checkIfFileExists(imagePath);
                String filename = Optional.of(imagePath)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElseThrow();
                System.out.println("Uploading " + filename + "...");
                Elements image = newParagraphDocument.select("img[src=\"" + imageSrc + "\"]");
                image.attr("data-entity-type", "file");
                String md5 = imageClient.generateMd5Hash(imagePath);
                Element currentImage = currentParagraphDocument.selectFirst("img[src^=\"/sites/default/files/api-docs/" + md5 + "\"]");
                if (currentImage != null && currentImage.hasAttr("data-entity-uuid")) {
                    image.attr("src", currentImage.attr("src"));
                    image.attr("data-entity-uuid", currentImage.attr("data-entity-uuid"));
                    image.attr("width", currentImage.attr("width"));
                    image.attr("height", currentImage.attr("height"));
                } else {
                    FileUploadModel imageModel = imageClient.upload(imagePath);
                    image.attr("src", imageModel.getOrCreateFirstUri().getUrl());
                    image.attr("data-entity-uuid", imageModel.getOrCreateFirstUuid().getValue());
                }
            }
            FormattedTextModel fieldDescription = getStartedParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(TextFormat.BASIC_HTML);
            fieldDescription.setValue(newParagraphDocument.body().html());
            getStartedParagraphClient.patch(getStartedParagraph);
            System.out.println("Finished processing paragraph: " + getStartedParagraph.id());
        }

        if(getStartedMenuItems.size() < getStartedDocsElements.size()){
            for(var d = getStartedDocsElements.size(); d > getStartedMenuItems.size(); d--){
                getStartedDocsElements.remove(d - 1);
            }
        }

        patchNodeModel.setGetStartedDocsElements(getStartedDocsElements);
    }

    private static void updateAdditionalInfoSection(Path workingDir, List<String> additionalInformationMenuItems, ParagraphClient<AdditionalInformationParagraphModel> additionalInformationParagraphClient, ImageClient imageClient, Converter converter, NodeModel nodeModel, NodeModel patchNodeModel) throws IOException, NoSuchAlgorithmException {
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
            checkIfFileExists(docPath);
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
                checkIfFileExists(imagePath);
                String filename = Optional.of(imagePath)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElseThrow();
                System.out.println("Uploading " + filename + "...");
                Elements image = newParagraphDocument.select("img[src=\"" + imageSrc + "\"]");
                image.attr("data-entity-type", "file");
                String md5 = imageClient.generateMd5Hash(imagePath);
                Element currentImage = currentParagraphDocument.selectFirst("img[src^=\"/sites/default/files/api-docs/" + md5 + "\"]");
                if (currentImage != null && currentImage.hasAttr("data-entity-uuid")) {
                    image.attr("src", currentImage.attr("src"));
                    image.attr("data-entity-uuid", currentImage.attr("data-entity-uuid"));
                    image.attr("width", currentImage.attr("width"));
                    image.attr("height", currentImage.attr("height"));
                } else {
                    FileUploadModel imageModel = imageClient.upload(imagePath);
                    image.attr("src", imageModel.getOrCreateFirstUri().getUrl());
                    image.attr("data-entity-uuid", imageModel.getOrCreateFirstUuid().getValue());
                }
            }
            FormattedTextModel fieldDescription = additionalInformationParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(TextFormat.BASIC_HTML);
            fieldDescription.setValue(newParagraphDocument.body().html());
            additionalInformationParagraphClient.patch(additionalInformationParagraph);
            System.out.println("Finished processing paragraph: " + additionalInformationParagraph.id());
        }

        if(additionalInformationMenuItems.size() < additionalInformationElements.size()){
            for(var d = additionalInformationElements.size(); d > additionalInformationMenuItems.size(); d--){
                additionalInformationElements.remove(d - 1);
            }
        }

        patchNodeModel.setAdditionalInformationElementsElements(additionalInformationElements);
    }

    private void updateFaqSection(Path workingDir, List<String> faqSectionItems, ParagraphClient<FaqItemsParagraphModel> faqItemsParagraphClient, ParagraphClient<FaqItemParagraphModel> faqItemParagraphClient, Converter converter, NodeModel nodeModel, NodeModel patchNodeModel) throws Exception {
        var faqSectionElements = new ArrayList<>(nodeModel.getFaqItems());

        for (int i = 0; i < faqSectionItems.size(); i++) {
            String menuItem = faqSectionItems.get(i);
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
            checkIfFileExists(docPath);
            String faqSectionContent = Files.readString(docPath);

            Map<String, List<String>> frontmatterFaq = new FrontMatterReader().readFromString(faqSectionContent);

            var faqItems = new ArrayList<>(faqItemsParagraph.getFaqItem());
            var iterations = (frontmatterFaq.size() - 2) / 2;

            for (int j = 0; j < iterations; j++) {
                FaqItemParagraphModel faqItemParagraphModel;

                if (j > faqItems.size() - 1) {
                    var questionString = frontmatterFaq.get("question-"+j);
                    var answerString = frontmatterFaq.get("answer-"+j);
                    faqItemParagraphModel = FaqItemParagraphModel.question(questionString.get(0));
                    faqItemParagraphModel = FaqItemParagraphModel.answer(answerString.get(0), faqItemParagraphModel);
                    faqItemParagraphModel = faqItemParagraphClient.post(faqItemParagraphModel);
                    faqItems.add(new FaqItemModel(faqItemParagraphModel));
                } else {
                    faqItemParagraphModel = faqItemParagraphClient.get(faqItems.get(j).getTargetId());
                }

                List<FaqQuestionModel> faqQuestionModelList = new ArrayList<>();
                List<FaqAnswerModel> faqAnswerModelList = new ArrayList<>();

                var questionString = frontmatterFaq.get("question-"+j);
                FaqQuestionModel questionModel = new FaqQuestionModel();
                questionModel.setValue(questionString.get(0));
                faqQuestionModelList.add(questionModel);

                var answerString = frontmatterFaq.get("answer-"+j);
                FaqAnswerModel answerModel = new FaqAnswerModel();
                answerModel.setValue(answerString.get(0));
                faqAnswerModelList.add(answerModel);

                faqItemParagraphModel.setQuestion(faqQuestionModelList);
                faqItemParagraphModel.setAnswer(faqAnswerModelList);

                System.out.println("Updating paragraph: " + faqItemParagraphModel.id());
                faqItemParagraphClient.patch(faqItemParagraphModel);
            }
            checkIfFileExists(docPath);

            if((frontmatterFaq.size() - 2) / 2 < faqItems.size()){
                for(var d = faqItems.size(); d > (frontmatterFaq.size() - 2) / 2; d--){
                    faqItems.remove(d - 1);
                }
            }

            faqItemsParagraph.setFaqItem(faqItems);
            faqItemsParagraph.getOrCreateFirstTitle().setValue(menuItem);

            faqItemsParagraph.setFaqItem(faqItems);
            faqItemsParagraphClient.patch(faqItemsParagraph);
            System.out.println("Finished processing paragraph: " + faqItemsParagraph.id());
        }

        if(faqSectionItems.size() < faqSectionElements.size()){
            for(var d = faqSectionElements.size(); d > faqSectionItems.size(); d--){
                faqSectionElements.remove(d - 1);
            }
        }

        patchNodeModel.setFaqItems(faqSectionElements);
    }


    private void updateDownloadsSection (Path workingDir, Map<String, List<String>> frontmatterDownloads, ParagraphClient<DownloadsElementParagraphModel> downloadsElementParagraphClient, DownloadFileClient downloadFileClient, Converter converter, NodeModel nodeModel, NodeModel patchNodeModel) throws IOException, NoSuchAlgorithmException {
        var downloadElements = new ArrayList<>(nodeModel.getDownloadElements());
        var keys = frontmatterDownloads.keySet();
        var values = frontmatterDownloads.values().iterator();
        String downloadsDirPath = workingDir.resolve(API_DOCS_DOWNLOADS_DIRECTORY).toString(); // api-docs/downloads
        var keySetArray = keys.toArray();


        for (var i = 0; i < keys.size(); i++) {
            var downloadItemFile = keySetArray[i];
            var downloadItemDescription = values.next().get(0);

            System.out.println("Updating paragraph: " + downloadItemDescription + " ...");
            DownloadsElementParagraphModel downloadsParagraph;

            Path downloadFilesPath = Paths.get(downloadsDirPath +"/" + downloadItemFile); //api-docs/downloads/example.json
            FileUploadModel downloadModel = downloadFileClient.upload(downloadFilesPath);


            if (i > downloadElements.size() - 1) {
                downloadsParagraph = DownloadsElementParagraphModel.create(downloadModel);
                downloadsParagraph.getOrCreateFirstDownloadFile().setTargetId(downloadModel.getFid().get(0).getValue());
                downloadsParagraph.getOrCreateFirstDownloadFile().setDescription(downloadItemDescription);
                downloadsParagraph = downloadsElementParagraphClient.post(downloadsParagraph);
                downloadElements.add(new DownloadsModel(downloadsParagraph));
            } else {
                downloadsParagraph = downloadsElementParagraphClient
                        .get(downloadElements.get(i).getTargetId());
                downloadsParagraph.getOrCreateFirstDownloadFile().setTargetId(downloadModel.getFid().get(0).getValue());
                downloadsParagraph.getOrCreateFirstDownloadFile().setDescription(downloadItemDescription);
                downloadsParagraph.getOrCreateFirstDownloadFile().setTargetUuid(downloadModel.getUuid().get(0).getValue());
                downloadsParagraph.getOrCreateFirstDownloadFile().setUrl(downloadModel.getUri().get(0).getValue());
                downloadsElementParagraphClient.patch(downloadsParagraph);
            }
            System.out.println("Updating paragraph: " + downloadsParagraph.id() + " ...");
            System.out.println("Finished processing paragraph: " + downloadsParagraph.id());
        }
        if(keys.size() < downloadElements.size()){
            for(var d = downloadElements.size(); d > keys.size(); d--){
                downloadElements.remove(d - 1);
            }
        }
        patchNodeModel.setDownloadElements(downloadElements);
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
    public boolean validate(String swaggerPath) {
        boolean validationResult = true;
        ObjectMapper mapper;

        if(this.useJson) {
            mapper = new ObjectMapper(new JsonFactory());

        } else{
            mapper = new ObjectMapper(new YAMLFactory());
        }

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

    public static void checkIfFileExists(Path filePath) {
        if (!Files.exists(filePath)) {
            throw new IllegalStateException("File " + filePath + " not found");
        }
    }

    public static ArrayList<String> compareLanguagesData(List<TranslationModel> translations, Path workingBaseDir) throws Exception {
        List<TranslationModel> languagesList = translations;
        ArrayList<String> pageLanguages = new ArrayList<>();
        ArrayList<String> localLangFolders = getTranslationFolders(workingBaseDir);

        for(TranslationModel translation: languagesList) {
            pageLanguages.add(translation.getLangcode());
        }

        boolean languageSetMatched = true;
        ArrayList<String> missingLanguages = new ArrayList<>();

        for (String lang:pageLanguages) {
            if(!localLangFolders.contains(lang)) {
                languageSetMatched = false;
                missingLanguages.add(lang);
            }
        }

        if(!languageSetMatched) {
            String missingLangsString = createMissingLanguagesString(missingLanguages);
            throw new Exception ("The following translations are missing from the local working directory: " + missingLangsString);
        }

        return pageLanguages;
    }

    public static ArrayList<String> getTranslationFolders(Path workingBaseDir) {
        ArrayList<String> subDirectories = new ArrayList<String>();

        File file = new File(workingBaseDir.toString());
        if (file == null) {
            throw new IllegalStateException("There aren't any directories by that path");
        }
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        for (String dir: directories) {
            subDirectories.add(dir);
        }

        return subDirectories;
    }

    public static String createMissingLanguagesString(ArrayList<String> languages) {
        String missingLanguages = "";
        for(String lang:languages) {
            missingLanguages += lang + ", ";
        }
        missingLanguages = missingLanguages.substring(0, missingLanguages.length() - 1);
        missingLanguages = missingLanguages.substring(0, missingLanguages.length() - 1);
        return missingLanguages;
    }
}

