package com.sprinteins.drupalcli.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConverterTest {

// convert html to markdown
    @Test
    void testConvertHtmlToMarkdown() throws Exception {
        Converter converter = new Converter();
        String html = "<u>im underlined</u> <strong>im strong</strong>";
        String expected = "im underlined **im strong**";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    // remove underline
    @Test
    void testRemoveUnderlineTag() throws Exception {
        Converter converter = new Converter();
        String html = "<u>im not underlined</u>";
        String expected = "im not underlined\n";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }
    // absolute links to the same portal should be relative
    @Test
    void testMakeAbsoluteLinksRelative() throws Exception {
        Converter converter = new Converter();
        String html = "<a href=\"https://example.com/test/123/site\">Link</a>\n" +
                "<a href=\"https://sub.example.com/test/123/site\">Link</a>";
        String expected = "[Link](/test/123/site) [Link](https://sub.example.com/test/123/site)\n";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }
    // ids in headings
    @Test
    void testRemoveIdsFromHeadings() throws Exception {
        Converter converter = new Converter();
        String html = "<h1 id=\"test\">h1</h1>\n"+
                "<h2 id=\"test\">h2</h2>\n"+
                "<h3 id=\"test\">h3</h3>\n"+
                "<h4 id=\"test\">h4</h4>\n"+
                "<h5 id=\"test\">h5</h5>\n"+
                "<h6 id=\"test\">h6</h6>";
        String expected = "# h1\n\n" +
                "## h2\n\n" +
                "### h3\n\n" +
                "#### h4\n\n" +
                "##### h5\n\n" +
                "###### h6\n\n";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    public static void assertHtmlToMarkdown(String html, String expected){
        Converter converter = new Converter();
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    // long microsoft -
    @Test
    void testFixLongDash() throws Exception {
        Converter converter = new Converter();
        String html = "<p>Hallo – na wie gehts?</p>";
        String expected = "Hallo - na wie gehts?\n";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    // keep table tags
    @Test
    void testIgnoreDivTags() throws Exception {
        Converter converter = new Converter();
        String html = "<div>TEST ME</div>";
        String expected = "<div>TEST ME</div>";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    // keep div tags
    @Test
    void testIgnoreTableTags() throws Exception {
        Converter converter = new Converter();
        String html = "<table>\n" +
                "  <thead>\n" +
                "    <tr><th> Head 1 </th><th> Head 2 </th><th> Head 3 </th></tr>\n" +
                "  </thead>\n" +
                "  <tbody>\n" +
                "    <tr><td> First Value 1 </td><td> First Value 2 </td><td> First Value 3 </td></tr>\n" +
                "    <tr><td> Second Value 1 </td><td> Second Value 2 </td><td> Second Value 3 </td></tr>\n" +
                "    <tr><td> Third Value 1 </td><td> Third Value 2 </td><td> 3 </td></tr>\n" +
                "  </tbody>\n" +
                "</table>";
        String expected = "<table>\n" +
                "  <thead>\n" +
                "    <tr><th> Head 1 </th><th> Head 2 </th><th> Head 3 </th></tr>\n" +
                "  </thead>\n" +
                "  <tbody>\n" +
                "    <tr><td> First Value 1 </td><td> First Value 2 </td><td> First Value 3 </td></tr>\n" +
                "    <tr><td> Second Value 1 </td><td> Second Value 2 </td><td> Second Value 3 </td></tr>\n" +
                "    <tr><td> Third Value 1 </td><td> Third Value 2 </td><td> Third Value 3 </td></tr>\n" +
                "  </tbody>\n" +
                "</table>";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }


    // convert markdown to html

        // fix bugs
            // markdown table is not converted
            // strong bugs

        // modify headline - h3 -> h1

        // modify image tags - set image ids






}

