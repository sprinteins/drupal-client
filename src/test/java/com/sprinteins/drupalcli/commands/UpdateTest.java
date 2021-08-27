package com.sprinteins.drupalcli.commands;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UpdateTest {

    @Test
    void testReleaseNoteExtraction(){
        String html = "<h3>First Headline</h3> \n" +
                "<ul><li>Foo</li> <li>Bar</li></ul>\n" +
                "<h3>Second Headline</h3>\n" +
                "<p>Paragraph here</p>\n" +
                "<h3>Third Headline</h3>\n" +
                "<span>Span here</span>\n" +
                "<h3>Fourth Headline</h3>\n" +
                "<ol><li>Foo</li><li>Bar</li></ol>\n";

        Document releaseNote = Jsoup.parse(html);
        Element releaseNoteBody = releaseNote.body();
        Update update = new Update();

        Element firstReleaseNote = update.extractFirstReleaseNote(releaseNoteBody);
        Element secondReleaseNote = update.extractFirstReleaseNote(releaseNoteBody);
        Element thirdReleaseNote = update.extractFirstReleaseNote(releaseNoteBody);
        Element fourthReleaseNote = update.extractFirstReleaseNote(releaseNoteBody);

        Assertions.assertEquals("First Headline", firstReleaseNote.select("h3").html());
        Assertions.assertEquals("Second Headline", secondReleaseNote.select("h3").html());
        Assertions.assertEquals("Third Headline", thirdReleaseNote.select("h3").html());
        Assertions.assertEquals("Fourth Headline", fourthReleaseNote.select("h3").html());
    }


}
