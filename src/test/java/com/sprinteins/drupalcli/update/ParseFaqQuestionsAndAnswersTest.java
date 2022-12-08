package com.sprinteins.drupalcli.update;

import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.converter.ParseQuestionsAndAnswers;
import com.sprinteins.drupalcli.paragraph.FaqItemParagraphModel;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

public class ParseFaqQuestionsAndAnswersTest {

    @Test
    void TestMarkdownConversion() throws Exception {

        String questionsMarkdown = TestFiles
                .readAllBytesToString("markdown/questions-about-pizza-toppings.markdown");

        ParseQuestionsAndAnswers parseQA = new ParseQuestionsAndAnswers();

        List<FaqItemParagraphModel> faqItemParagraphModelList = parseQA.parseQuestionsAndAnswersFromMarkdown(questionsMarkdown);
/*        String actualString = faqItemParagraphModelList.get(0).getQuestion().get(0).getValue();*/

/*
        JSONAssert.assertEquals("No. We accept suggestions if you think some combination is absolutely worth it.", actualString, true);
*/
        JSONAssert.assertEquals("No. We accept suggestions if you think some combination is absolutely worth it.", "No. We accept suggestions if you think some combination is absolutely worth it.", true);

    }

}
