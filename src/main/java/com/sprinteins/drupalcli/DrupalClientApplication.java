package com.sprinteins.drupalcli;

import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class DrupalClientApplication {

    public static String DEFAULT_BASE_URI = "http://dhl.docker.amazee.io";

    public static int run(String[] args) {
        try {
            if (args.length < 5) {
                System.out.println("You are missing some arguments!");
                System.out.println("Please add the following arguments when calling this program:");
                System.out.println("node-id paragraph-id document-path swagger-path credentials-doc-path [baseUri/entity/paragraph (default is amazee)]");
                return 0;
            }

            long nodeId = Long.parseLong(args[0]);
            long id = Long.parseLong(args[1]);
            String docPath = args[2];
            String swaggerPath = args[3];
            String credentialsPath = args[4];

            String baseUri = DEFAULT_BASE_URI;
            if (args.length > 5) {
                baseUri = args[6];
            }

            System.out.println("NODE ID: " + nodeId);
            System.out.println("PARAGRAPH ID: " + id);
            System.out.println("DOCUMENT PATH: " + docPath);
            System.out.println("SWAGGER PATH: " + swaggerPath);
            System.out.println("CREDENTIALS FILE PATH: " + credentialsPath);
            System.out.println("BASE URI: " + baseUri);

            String authenticationHeader = "Basic " + Base64.getEncoder().encodeToString(
                    Files.readAllLines(Paths.get(credentialsPath))
                            .get(0)
                            .getBytes(StandardCharsets.UTF_8)
            );

            ObjectMapper objectMapper = new ObjectMapper();

            String markdown = Files.readString(Paths.get(docPath));

            GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
            DescriptionModel fieldDescription = getStartedParagraph
                    .getOrCreateFirstDescription();
            fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
            fieldDescription.setValue(markdown);

            new GetStartedParagraphClient(objectMapper, baseUri, authenticationHeader)
                    .patch(id, getStartedParagraph);
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
