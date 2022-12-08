package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.TextFormat;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class GetStartedParagraphModelTest {

    @Test
    void testJsonSerializationDescription() throws Exception {
        GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
        FormattedTextModel fieldDescription = getStartedParagraph
                .getOrCreateFirstDescription();
        fieldDescription.setFormat(TextFormat.GITHUB_FLAVORED_MARKDOWN);
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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String actual = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(value);

        JSONAssert.assertEquals(expected, actual, true);
    }

}
