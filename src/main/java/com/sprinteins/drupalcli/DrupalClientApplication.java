package com.sprinteins.drupalcli;

import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sprinteins.drupalcli.file.ApiReferenceFileClient;
import com.sprinteins.drupalcli.file.ApiReferenceFileModel;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphClient;
import com.sprinteins.drupalcli.getstartedparagraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.ValueFormat;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class DrupalClientApplication {

    public static String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";

    public static int run(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("You are missing some arguments!");
                System.out.println("Please add the following arguments when calling this program:");
                System.out.println("pathToConfigYaml [baseUri]");
                return 0;
            }

            Path configPath = Paths.get(args[0]);
            Path workingDir = configPath.toAbsolutePath().getParent();

            ObjectMapper configMapper = new ObjectMapper(new YAMLFactory());
            Config config = configMapper.readValue(Files.readString(configPath), Config.class);

            long nodeId = config.getNode();
            Path swaggerPath = workingDir.resolve(config.getSwagger());
            Path credentialsPath = workingDir.resolve("credentials.txt");

            String baseUri = DEFAULT_BASE_URI;
            if (args.length > 1) {
                baseUri = args[2];
            }

            System.out.println("NODE ID: " + nodeId);
            System.out.println("SWAGGER PATH: " + swaggerPath);
            System.out.println("CREDENTIALS FILE PATH: " + credentialsPath);
            System.out.println("BASE URI: " + baseUri);

            String authenticationHeader = "Basic " + Base64.getEncoder().encodeToString(
                    Files.readAllLines(credentialsPath)
                            .get(0)
                            .getBytes(StandardCharsets.UTF_8)
            );

            ObjectMapper objectMapper = new ObjectMapper();

            GetStartedParagraphClient getStartedParagraphClient = new GetStartedParagraphClient(objectMapper, baseUri, authenticationHeader);

            for(Config.ParagraphConfig paragraph : config.getParagraphs()) {
                long id = paragraph.getId();
                Path docPath = workingDir.resolve(paragraph.getContent());

                System.out.println("PARAGRAPH ID: " + id);
                System.out.println("DOCUMENT PATH: " + docPath);

                String markdown = Files.readString(docPath);

                GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
                getStartedParagraph.getOrCreateFirstTitle().setValue(paragraph.getTitle());
                DescriptionModel fieldDescription = getStartedParagraph
                        .getOrCreateFirstDescription();
                fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
                fieldDescription.setValue(markdown);

                getStartedParagraphClient.patch(id, getStartedParagraph);
            }

            ApiReferenceFileModel model =
                    new ApiReferenceFileClient(
                            objectMapper,
                            baseUri,
                            authenticationHeader)
                            .upload(swaggerPath);

            NodeModel nodeModel = new NodeModel();
            nodeModel.getOrCreateFirstSourceFile().setTargetId(model.getFid().get(0).getValue());
            new NodeClient(
                    objectMapper,
                    baseUri,
                    authenticationHeader)
                    .patch(nodeId, nodeModel);

            return 0;
        } catch (RuntimeException e) {
            System.err.println("Couldn't process: " + e);
            e.printStackTrace();
            return 1;
        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    public static void main(String[] args) {
        int status = run(args);
        System.exit(status);
    }

}
