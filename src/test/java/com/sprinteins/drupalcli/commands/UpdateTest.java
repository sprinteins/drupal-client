package com.sprinteins.drupalcli.commands;

import com.sprinteins.drupalcli.translations.TranslationModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.sprinteins.drupalcli.commands.Update.*;
import static com.sprinteins.drupalcli.commands.Update.compareLanguagesData;

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

    @Test
    void testValidationV2() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL test = Objects.requireNonNull(classLoader.getResource("petstore-simple.yaml"));
        Update update = new Update();

        boolean valid = update.validate(test.toString());
        Assertions.assertTrue(valid);
    }

    @Test
    void testValidationV3() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL test = Objects.requireNonNull(classLoader.getResource("petstore.yaml"));
        Update update = new Update();

        boolean valid = update.validate(test.toString());
        Assertions.assertTrue(valid);
    }

    @Test
    void testValidationV3_invalid() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL test = Objects.requireNonNull(classLoader.getResource("petstore_invalid.yaml"));
        Update update = new Update();

        boolean valid = update.validate(test.toString());
        Assertions.assertFalse(valid);
    }

    @Test
    void testValidation_invalidPath() throws Exception {
        Update update = new Update();

        boolean valid = update.validate("laksjdlkajsdlkj");
        Assertions.assertFalse(valid);
    }

    @Test
    void testValidation_nullPath() throws Exception {
        Update update = new Update();

        boolean valid = update.validate(null);
        Assertions.assertFalse(valid);
    }

    @Test
    void testValidation_locationFinderNullPointer() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL test = Objects.requireNonNull(classLoader.getResource("petstore-simple-with-tabs.yaml"));
        Update update = new Update();

        boolean valid = update.validate(test.toString());
        Assertions.assertFalse(valid);
    }

    @Test
    void testGetTranslationFolders() {
        Path pathToTestFolderDir = Paths.get("src/test/resources/testFolderTranslations");
        ArrayList<String> expectedValue = new ArrayList<>(Arrays.asList("de", "en"));
        var actualValue = getTranslationFolders(pathToTestFolderDir);
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void testCreateMissingLanguagesString() {
        ArrayList<String> testLanguages = new ArrayList<>(Arrays.asList("de", "it"));
        String expectedValue = "de, it";
        String actualValue = createMissingLanguagesString(testLanguages);
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void testCompareLanguagesData() throws Exception {
        TranslationModel model = new TranslationModel();
        model.setLangcode("de");
        TranslationModel model2 = new TranslationModel();
        model2.setLangcode("en");
        List<TranslationModel> translations = new ArrayList<>();
        translations.add(model);
        translations.add(model2);
        Path testPathTranslation = Paths.get("src/test/resources/testFolderTranslations");

        ArrayList<String> expectedValue = new ArrayList<>(Arrays.asList("de", "en"));
        ArrayList<String> actualValue = compareLanguagesData(translations, testPathTranslation);

        Assertions.assertEquals(expectedValue, actualValue);
    }
}
