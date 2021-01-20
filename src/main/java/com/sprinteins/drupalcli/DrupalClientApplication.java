package com.sprinteins.drupalcli;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DrupalClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println("Hi there!");

		GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
		FieldDescriptionModel fieldDescription = getStartedParagraph
				.getOrCreateFirstDescription();
		fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
		fieldDescription.setValue("# Title");

		String output = new ObjectMapper()
				.writeValueAsString(getStartedParagraph);
		System.out.println(output);
	}

}
