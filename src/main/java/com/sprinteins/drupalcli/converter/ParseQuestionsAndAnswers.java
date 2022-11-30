package com.sprinteins.drupalcli.converter;

import com.sprinteins.drupalcli.fields.FaqAnswerModel;
import com.sprinteins.drupalcli.fields.FaqQuestionModel;
import com.sprinteins.drupalcli.paragraph.FaqItemParagraphModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParseQuestionsAndAnswers {


    public List<FaqItemParagraphModel> parseQuestionsAndAnswersFromMarkdown(String markdown) {

        Converter  converter = new Converter();

        Document newParagraphDocument = Jsoup.parse(converter.convertMarkdownToHtml(markdown));
        List<Node> pTagList = newParagraphDocument.childNodes().get(0).childNodes().get(1).childNodes();



        /* String FAQ_MARKDOWN_FILE_NAME = "questions-about-pizza-sauces.markdown";


        GlobalOptions globalOptions = new GlobalOptions();

        String link = "";
        URI uri = URI.create(link);
        String baseUri = uri.getScheme() + "://" + uri.getHost();
        Path workingDir = globalOptions.apiPageDirectory;
        ApplicationContext applicationContext = new ApplicationContext(baseUri, globalOptions); */

        List<FaqItemParagraphModel> faqItemParagraphList = new ArrayList<FaqItemParagraphModel>();

        FaqItemParagraphModel faqItemParagraph = new FaqItemParagraphModel();
/*        List<FaqQuestionModel> faqQuestionModelList = new ArrayList<FaqQuestionModel>();
        List<FaqAnswerModel> faqAnswerModelList = new ArrayList<FaqAnswerModel>();*/

        Type test1 = pTagList.get(0).getClass();
        Boolean test2 = pTagList.get(1).hasAttr("value");
        Attributes test3 = pTagList.get(2).attributes();

        // Logic for retrieving Qs and A
        for (Node el : pTagList) {
            // find correct condition
            if (){
                List<String> resultQAs = new ArrayList<>();
                Node pTagContent = el.childNodes().get(0);

                String[] substrA = pTagContent.toString().split(" ANSWER: ");
                String[] substrQ = substrA[0].split("QUESTION: ");

                String question = substrQ[1];
                String answer = substrA[1];

                resultQAs.add(question);
                resultQAs.add(answer);

                for (String element : resultQAs) {
                    List<FaqQuestionModel> faqQuestionModelList = new ArrayList<>();
                    List<FaqAnswerModel> faqAnswerModelList = new ArrayList<>();
                    FaqQuestionModel faqQuestion = new FaqQuestionModel();
                    FaqAnswerModel faqAnswer = new FaqAnswerModel();
                    faqQuestion.setValue(element);
                    faqAnswer.setValue(element);
                    faqQuestionModelList.add(faqQuestion);
                    faqAnswerModelList.add(faqAnswer);
                    faqItemParagraph.setQuestion(faqQuestionModelList);
                    faqItemParagraph.setAnswer(faqAnswerModelList);
                    faqItemParagraphList.add(faqItemParagraph);
                }
            }
        }

        return faqItemParagraphList;
    }

}
