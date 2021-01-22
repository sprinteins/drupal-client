package com.sprinteins.drupalcli;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DrupalClientApplication {
	public static void main(String[] args) throws Exception {

		long id = 195;
		String docPath = "README.md";
		String baseUri = "http://dhl.docker.amazee.io/entity/paragraph/";

		System.out.println("ID: " + id);
		System.out.println("DOC PATH: " + docPath);
		System.out.println("URI: " + baseUri);

		// read from file
		String markdown = Files.readString(Paths.get(docPath));

		GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
		DescriptionModel fieldDescription = getStartedParagraph
				.getOrCreateFirstDescription();
		fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
		fieldDescription.setValue(markdown);

		// send request
		new GetStartedParagraphClient(baseUri).patch(id, getStartedParagraph);
	}

}
