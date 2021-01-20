package com.sprinteins.drupalcli;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetStartedParagraphModelTest {

	@Test
	void testJsonSerializationDescription() throws Exception {
		GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
		FieldDescriptionModel fieldDescription = getStartedParagraph
				.getOrCreateFirstDescription();
		fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
		fieldDescription.setValue("# Title");

		testSerialization("paragraph-serialize-description",
				getStartedParagraph);
	}

	@Test
	void testJsonSerializationTitle() throws Exception {
		GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
		getStartedParagraph.getOrCreateFirstTitle().setValue("Legal Terms");

		testSerialization("paragraph-serialize-title", getStartedParagraph);
	}

	private void testSerialization(String path, Object value) throws Exception {
		String expected = TestFiles
				.readAllBytesToString("json/" + path + ".json");

		String actual = new ObjectMapper().writerWithDefaultPrettyPrinter()
				.writeValueAsString(value);

		JSONAssert.assertEquals(expected, actual, true);
	}

}
