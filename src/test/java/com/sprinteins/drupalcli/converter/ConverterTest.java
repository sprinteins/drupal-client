package com.sprinteins.drupalcli.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConverterTest {

    @Test
    void testRemoveUnderlineTag() throws Exception {
        String html = "<u>im not underlined</u>";
        String expected = "im not underlined\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testMakeAbsoluteLinksRelative() throws Exception {
        String html = "<a href=\"https://example.com/test/123/site\">Link</a>\n" +
                "<a href=\"https://sub.example.com/test/123/site\">Link</a>";
        String expected = "[Link](/test/123/site) [Link](https://sub.example.com/test/123/site)\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testAnchorTargetBlank() throws Exception {
        String html = "<a target=\"_blank\" href=\"/test/123/site\">Link</a>\n";
        assertHtmlToMarkdown(html, html);
    }
    
    // ids in headings
    @Test
    void testRemoveIdsFromHeadings() throws Exception {
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
        assertHtmlToMarkdown(html, expected);
    }

    public static void assertHtmlToMarkdown(String html, String expected){
        Converter converter = new Converter();
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    @Test
    void testFixLongDash() throws Exception {
        String html = "<p>Hallo – na wie gehts?</p>";
        String expected = "Hallo - na wie gehts?\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testIgnoreDivTags() throws Exception {
        String html = "<div class=\"something\">TEST ME</div>";
        String expected = "<div class=\"something\">\n" +
                "TEST ME\n" +
                "</div>\n";
        assertHtmlToMarkdown(html, expected);
    }
    
    @Test
    void testDivWithId() throws Exception {
        String html = "<div id=test>TEST ME</div>";
        String expected = "<div id=\"test\">\nTEST ME\n</div>\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testIgnoreTableTags() throws Exception {
        String html = "<table>\n" +
                "  <thead>\n" +
                "    <tr><th> Head 1 </th><th> Head 2 </th><th> Head 3 </th></tr>\n" +
                "  </thead>\n" +
                "  <tbody>\n" +
                "    <tr><td> First Value 1 </td><td> First Value 2 </td><td> First Value 3 </td></tr>\n" +
                "    <tr><td> Second Value 1 </td><td> Second Value 2 </td><td> Second Value 3 </td></tr>\n" +
                "    <tr><td> Third Value 1 </td><td> Third Value 2 </td><td> Third Value 3 </td></tr>\n" +
                "  </tbody>\n" +
                "</table>";
        String expected = "<table>\n" +
                "<thead>\n" +
                "<tr>\n" +
                "<th> Head 1 </th>\n" +
                "<th> Head 2 </th>\n" +
                "<th> Head 3 </th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td> First Value 1 </td>\n" +
                "<td> First Value 2 </td>\n" +
                "<td> First Value 3 </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td> Second Value 1 </td>\n" +
                "<td> Second Value 2 </td>\n" +
                "<td> Second Value 3 </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td> Third Value 1 </td>\n" +
                "<td> Third Value 2 </td>\n" +
                "<td> Third Value 3 </td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testIgnoreDivTagsWithAttributes() throws Exception {
        Converter converter = new Converter();
        String html = "<div class=\"table-responsive\">TEST ME</div>";
        String expected = "<div class=\"table-responsive\">\n" +
                "TEST ME\n" +
                "</div>\n";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }

    @Test
    void testIgnoreTableTagsWithAttributes() throws Exception {
        Converter converter = new Converter();
        String html = "<table class=\"table table-hover table-striped\" cellspacing=\"1\" cellpadding=\"1\" border=\"1\">TEST ME</table>";
        String expected = "<table class=\"table table-hover table-striped\" cellspacing=\"1\" cellpadding=\"1\" border=\"1\">\n" +
                "TEST ME\n" +
                "</table>\n";
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com");
        Assertions.assertEquals(expected, markdown);
    }
    
    @Test
    public void testTableWithStyle() throws Exception {
        String html = "<table align=\"center\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 569px;\">\n"
                + "<tbody>\n"
                + "<tr>\n"
                + "<td> <p class=\"text-align-center\">Packet Tracked</p> </td>\n"
                + "<td style=\"width: 103px;\"> <p class=\"text-align-center\">Packet Plus</p> </td>\n"
                + "<td style=\"width: 205px;\"> <p class=\"text-align-center\">Packet (Standard / Priority)</p> </td>\n"
                + "<td style=\"width: 149px;\"> <p class=\"text-align-center\">Packet Return</p> </td>\n"
                + "</tr>\n"
                + "</tbody>\n"
                + "</table>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    @Test
    public void testTableWithStrong() throws Exception {
        String html = "<table>\n"
                + "<tbody>\n"
                + "<tr>\n"
                + "<td><strong>Hi</strong></td>\n"
                + "</tr>\n"
                + "</tbody>\n"
                + "</table>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    @Test
    public void testTableWithHeight() throws Exception {
        String html = "<table>\n"
                + "<tbody>\n"
                + "<tr height=\"45\">\n"
                + "<td height=\"45\">Hi</td>\n"
                + "</tr>\n"
                + "</tbody>\n"
                + "</table>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    @Test
    public void testAccordion() throws Exception {
        String html = "<a class=\"btn btn-primary\" data-parent=\"#accordion\" data-toggle=\"collapse\" href=\"#collapse2\">EventAirCode</a>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    // convert markdown to html

        // fix bugs
            // markdown table is not converted
            // strong bugs

        // modify headline - h3 -> h1

        // modify image tags - set image ids






}

