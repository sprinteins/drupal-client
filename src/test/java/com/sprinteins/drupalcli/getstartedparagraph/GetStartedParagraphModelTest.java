package com.sprinteins.drupalcli.getstartedparagraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.ValueFormat;
import com.sprinteins.drupalcli.paragraph.GetStartedParagraphModel;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class GetStartedParagraphModelTest {

    @Test
    void testJsonSerializationDescription() throws Exception {
        GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();
        DescriptionModel fieldDescription = getStartedParagraph
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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String actual = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(value);

        JSONAssert.assertEquals(expected, actual, true);
    }

}
