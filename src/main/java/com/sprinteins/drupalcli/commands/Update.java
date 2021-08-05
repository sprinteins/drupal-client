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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.net.ProxySelector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

@Command(
        name = "update",
        description = "Update description"
        )
public class Update implements Callable<Integer> {

    public static final String MAIN_MARKDOWN_FILE_NAME = "main.markdown";
    public static final String IMAGE_FOLDER_NAME = "images";

    @Mixin
    private GlobalOptions globalOptions;

    @Option(
            names = { "--api-page" , "-a"},
            description = "API page ID",
            required = true
            )
    Long nodeId;

    @Option(
            names = { "--portal-environment" , "-p"},
            description = "Portal environment to update",
            defaultValue = "http://dhl.docker.amazee.io"
            )
    String portalEnv = "http://dhl.docker.amazee.io";

    @Option(
            names = { "--explicitly-disable-checks" , "-e"},
            description = "Explicitly disabled checks"
            )
    ArrayList<String> disabledChecks;

    @Override
    public Integer call() throws Exception {
        ProxySearch proxySearch = new ProxySearch();
        proxySearch.addStrategy(Strategy.ENV_VAR);
        ProxySelector proxySelector = proxySearch.getProxySelector();
        if (proxySelector != null) {
            ProxySelector.setDefault(proxySelector);
        }

        Path workingDir = globalOptions.apiPageDirectory;
        Path mainFilePath = workingDir.resolve(MAIN_MARKDOWN_FILE_NAME);

        boolean markdownFileExists = Files.exists(mainFilePath);
        if (!markdownFileExists) {
            throw new Exception("No " + MAIN_MARKDOWN_FILE_NAME + " file in given directory (" + workingDir + ")");
        }

        if (portalEnv.endsWith("/")) {
            portalEnv = portalEnv.substring(0, portalEnv.length() - 1);
        }

        OpenAPI apiSpec = new OpenAPI(workingDir);
        String openAPISpecFileName = apiSpec.getOpenAPISpecFileName();

        String content = Files.readString(mainFilePath);
        FrontMatterReader frontMatter = new FrontMatterReader();
        Map<String, List<String>> data = frontMatter.readFromString(content);
        List<String> titleList = data.get("title");
        String title = titleList.get(0);

        Path swaggerPath = workingDir.resolve(openAPISpecFileName);

        ApplicationContext applicationContext = new ApplicationContext(portalEnv, globalOptions);
        NodeClient nodeClient = applicationContext.nodeClient();
        var getStartedParagraphClient = applicationContext.getStartedParagraphClient();
        ImageClient imageClient = applicationContext.imageClient();
        ApiReferenceFileClient apiReferenceFileClient = applicationContext.apiReferenceFileClient();

        System.out.println("Updating node: " + title + " - " + nodeId + " ...");

        NodeModel nodeModel = nodeClient.get(nodeId);

        if (!title.equals(nodeModel.getOrCreateFirstDisplayTitle().getValue())) {
            throw new Exception(
                    "The page titles do not match. Are you updating the wrong API page?\n" + " Supplied title: " + title
                            + "\n" + " Target title: " + nodeModel.getOrCreateFirstDisplayTitle().getValue());
        }

        for (GetStartedDocsElementModel getStartedDocsElement : nodeModel.getGetStartedDocsElement()) {

            System.out.println("Updating paragraph: " + getStartedDocsElement.getTargetId() + " ...");

            GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient
                    .get(getStartedDocsElement.getTargetId());

            Path docPath = workingDir.resolve(
                    getStartedParagraph.getOrCreateFirstTitle().getValue().toLowerCase(Locale.ROOT).replace(" ", "-")
                            + ".markdown");

            if (!Files.exists(docPath)) {
                throw new IllegalStateException("File " + docPath + " not found");
            }

            Document currentParagraphDocument = Jsoup
                    .parse(getStartedParagraph.getOrCreateFirstDescription().getProcessed());
            Document newParagraphDocument = Jsoup
                    .parse(applicationContext.converter().convertMarkdownToHtml(Files.readString(docPath)));

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

            DescriptionModel fieldDescription = getStartedParagraph.getOrCreateFirstDescription();
            fieldDescription.setFormat(ValueFormat.BASIC_HTML);
            fieldDescription.setValue(newParagraphDocument.body().html());

            getStartedParagraphClient.patch(getStartedDocsElement.getTargetId(), getStartedParagraph);
            System.out.println("Finished processing paragraph: " + getStartedDocsElement.getTargetId());
        }

        FileUploadModel apiReferenceModel = apiReferenceFileClient.upload(swaggerPath);

        NodeModel patchNodeModel = new NodeModel();
        patchNodeModel.getOrCreateFirstSourceFile().setTargetId(apiReferenceModel.getFid().get(0).getValue());
        nodeClient.patch(nodeId, patchNodeModel);

        System.out.println("Finished processing node: " + nodeId);
        return 0;
    }

}
