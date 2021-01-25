package com.sprinteins.drupalcli;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class DrupalClientApplication {

    public static int run(String[] args) {
        try {
            if (args.length < 3) {
                System.out.println("You are missing some arguments!");
                System.out.println("Please add the following arguments when calling this program:");
                System.out.println("paragraph-id document-path credentials-doc-path [baseUri/entity/paragraph (default is amazee)]");
                return 0;
            }

            long id = Long.parseLong(args[0]);

            String docPath = args[1];
			String credentialsPath = args[2];

            String baseUri = "http://dhl.docker.amazee.io/entity/paragraph/";
            if (args.length > 3) {
                baseUri = args[3];
            }

            System.out.println("ID: " + id);
            System.out.println("DOC PATH: " + docPath);
            System.out.println("CRED FILE PATH: " + credentialsPath);
            System.out.println("URI: " + baseUri);

			String token = Base64.getEncoder().encodeToString(
					Files.readAllLines(Paths.get(credentialsPath))
							.get(0)
							.getBytes()
			);

            String markdown = Files.readString(Paths.get(docPath));

            GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
            DescriptionModel fieldDescription = getStartedParagraph
                    .getOrCreateFirstDescription();
            fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
            fieldDescription.setValue(markdown);

            new GetStartedParagraphClient(baseUri).patch(id, getStartedParagraph, token);
            return 0;
        } catch (Exception e) {
        	System.err.println("Something went wrong: "+ e.getMessage());
        	e.printStackTrace();
            return 1;
        }
    }

    public static void main(String[] args) {
        int status = run(args);
        System.exit(status);
    }

}
