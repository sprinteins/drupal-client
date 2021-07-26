package com.sprinteins.drupalcli.commands;

import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.GetStartedDocsElementModel;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "export", description = "Export page")
public class Export implements Callable<Integer> {

    public static final String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";
    public static final String API_KEY_ENV_KEY = "DHL_API_DEVELOPER_PORTAL_TOKEN_FILE";

    @CommandLine.ArgGroup(multiplicity = "1")
    Dependent group;

    static class Dependent {
        @Option(names = { "--link" }, description = "Link to the api page that needs to be exported")
        String link;

        @Option(names = { "--html" }, description = "Local path to the html file")
        String htmlFile;
    }


    @Override
    public Integer call() throws Exception {
        String link = group.link;
        String htmlFile = group.htmlFile;
        List<String> htmlList = new ArrayList<>();

        if (link.length() > 1) {
            URI uri = URI.create(link);
            String baseUri = uri.getScheme() + "://" + uri.getHost();
            String apiKey = readApiKey();
            ApplicationContext applicationContext = new ApplicationContext(baseUri, apiKey);
            NodeClient nodeClient = applicationContext.nodeClient();
            GetStartedParagraphClient getStartedParagraphClient = applicationContext.getStartedParagraphClient();

            NodeModel nodeModel = nodeClient.getByUri(link);

            for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()) {
                GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient.get(getStartedDocsElement.getTargetId());
                DescriptionModel descriptionModel = getStartedParagraph.getOrCreateFirstDescription();
                if(descriptionModel.getProcessed() != null){
                    htmlList.add(descriptionModel.getProcessed());
                } else {
                    htmlList.add(descriptionModel.getValue());
                }
            }
        } else {
            htmlList.add(Files.readString(Paths.get(htmlFile)));
        }

        for(String html: htmlList){
            String markdown = FlexmarkHtmlConverter.builder().build().convert(html);
            System.out.println(markdown);
        }
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
}
