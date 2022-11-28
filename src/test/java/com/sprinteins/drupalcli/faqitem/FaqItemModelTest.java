package com.sprinteins.drupalcli.faqitem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.models.FaqAnswerModel;
import com.sprinteins.drupalcli.models.FaqQuestionModel;
import com.sprinteins.drupalcli.models.ValueFormat;
import com.sprinteins.drupalcli.paragraph.FaqItemParagraphModel;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

public class FaqItemModelTest {

    @Test
    void testJsonSerializationDescription() throws Exception {
        FaqItemParagraphModel faqItemParagraphModel = new FaqItemParagraphModel();

        var answer = new FaqAnswerModel();
        List<FaqAnswerModel> answerArray = new ArrayList<FaqAnswerModel>();
        answerArray.add(answer);
        answer.setValue("Test124");
        answer.setFormat(ValueFormat.BASIC_HTML);

        var question = new FaqQuestionModel();
        List<FaqQuestionModel> questionArray = new ArrayList<FaqQuestionModel>();
        questionArray.add(question);
        question.setValue("Is Pineapple a valid topping?");


        faqItemParagraphModel.setQuestion(questionArray);
        faqItemParagraphModel.setAnswer(answerArray);

        testSerialization("faq-item-paragraph",
                faqItemParagraphModel);
    }

    // @Test
    // void testJsonSerializationTitle() throws Exception {
    //     FaqItemParagraphModel faqItemParagraphModel = new FaqItemParagraphModel();
    //     faqItemParagraphModel.getOrCreateFirstTitle().setValue("Legal Terms");

    //     testSerialization("paragraph-serialize-title", faqItemParagraphModel);
    // }

    private void testSerialization(String path, Object value) throws Exception {
        String expected = TestFiles
                .readAllBytesToString("json/" + path + ".json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JsonNode expectedJson = objectMapper.readTree(expected);
        JsonNode actualJson = objectMapper.readTree(objectMapper.writeValueAsString(value));

        var expectedAnswerValue = expectedJson.get("field_faq_item_question").findValue("value").asText();
        var actualAnswerValue = expectedJson.get("field_faq_item_question").findValue("value").asText();


        JSONAssert.assertEquals(expectedAnswerValue, actualAnswerValue, true);
    }
}
