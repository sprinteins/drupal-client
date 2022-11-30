package com.sprinteins.drupalcli.update;

import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.commands.GlobalOptions;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.converter.ParseQuestionsAndAnswers;
import com.sprinteins.drupalcli.paragraph.FaqItemParagraphModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import picocli.CommandLine;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ParseFaqQuestionsAndAnswersTest {

    @Test
    void TestMarkdownConversion() throws Exception {

        String questionsMarkdown = TestFiles
                .readAllBytesToString("markdown/questions-about-pizza-sauces.markdown");

        ParseQuestionsAndAnswers parseQA = new ParseQuestionsAndAnswers();

        List<FaqItemParagraphModel> faqItemParagraphModelList = parseQA.parseQuestionsAndAnswersFromMarkdown(questionsMarkdown);
        String actualString = faqItemParagraphModelList.get(0).getQuestion().get(0).getValue();

        JSONAssert.assertEquals("<p>No. We accept suggestions if you think some combination is absolutely worth it.</p>", actualString, true);

    }

}
