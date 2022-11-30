package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.fields.FaqItemModel;
import com.sprinteins.drupalcli.fields.FaqTitleModel;
import com.sprinteins.drupalcli.fieldtypes.TargetType;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

public class FaqItemsParagraphModelTest {
    @Test
    void testJsonSerializationTitle() throws Exception {
        FaqItemsParagraphModel faqItemsParagraphModel = new FaqItemsParagraphModel();

        faqItemsParagraphModel.getOrCreateFirstTitle().setValue("Questions about Pizza toppings");

        testSerialization("faq-items-paragraph",
                faqItemsParagraphModel);
    }
    private void testSerialization(String path, Object value) throws Exception {
        String expected = TestFiles
                .readAllBytesToString("json/" + path + ".json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JsonNode expectedJson = objectMapper.readTree(expected);
        JsonNode actualJson = objectMapper.readTree(objectMapper.writeValueAsString(value));

        var expectedTitle = expectedJson.get("field_faq_title").findValue("value").toString();
        var actualTitle = actualJson.get("field_faq_title").findValue("value").toString();

        JSONAssert.assertEquals(expectedTitle, actualTitle, true);
    }

}
