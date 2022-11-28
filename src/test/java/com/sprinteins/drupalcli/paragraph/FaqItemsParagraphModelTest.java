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
    void testJsonSerializationDescription() throws Exception {
        FaqItemsParagraphModel faqItemsParagraphModel = new FaqItemsParagraphModel();
        faqItemsParagraphModel.create("Questions about Pizza toppings");


        FaqItemParagraphModel faqItemParagraphModel = new FaqItemParagraphModel();

        var faqItem = new FaqItemModel(faqItemParagraphModel);
        List<FaqItemModel> faqItemArray = new ArrayList<FaqItemModel>();
        faqItemArray.add(faqItem);
        faqItem.setTargetId(1500L);
        faqItem.setTargetRevisionId(35223L);
        faqItem.setTargetType(TargetType.PARAGRAPH);
        faqItem.setTargetUuid("228e52fe-ed47-467b-a1e9-3b5dc9c6b2dd");

        FaqTitleModel faqTitle = new FaqTitleModel();
        faqTitle.setValue("Questions about Pizza toppings");

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

        var expectedType = expectedJson.get("type").findValue("target_id").asText();

        var expectedTargetid = expectedJson.get("field_faq_item").findValue("target_id").asText();
        var actualTargetid = expectedJson.get("field_faq_item").findValue("target_id").asText();

        var expectedTargetRevisionId = expectedJson.get("field_faq_item").findValue("target_revision_id").asText();
        var actualTargetRevisionId = expectedJson.get("field_faq_item").findValue("target_revision_id").asText();

        var expectedTargetType = expectedJson.get("field_faq_item").findValue("target_type").asText();
        var actualTargetType = expectedJson.get("field_faq_item").findValue("target_type").asText();

        var expectedTargetUuid = expectedJson.get("field_faq_item").findValue("target_uuid").asText();
        var actualTargetUuid = expectedJson.get("field_faq_item").findValue("target_uuid").asText();

        var expectedTitle = expectedJson.get("field_faq_title").findValue("value").asText();
        var actualTitle = expectedJson.get("field_faq_title").findValue("value").asText();

        JSONAssert.assertEquals(expectedTargetid, actualTargetid, true);
        JSONAssert.assertEquals(expectedTargetRevisionId, actualTargetRevisionId, true);
        JSONAssert.assertEquals(expectedTargetType, actualTargetType, true);
        JSONAssert.assertEquals(expectedTargetUuid, actualTargetUuid, true);
        JSONAssert.assertEquals(expectedTitle, actualTitle, true);
    }

}
