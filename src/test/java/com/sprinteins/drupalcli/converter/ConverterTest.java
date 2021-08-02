package com.sprinteins.drupalcli.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConverterTest {

    @Test
    void testPre() throws Exception {
        String html = "<pre><code>429\n"
                + "</code></pre>";
        String expected = "    429\n\n";
        assertHtmlToMarkdown(html, expected);
    }
    
    @Test
    void testP() throws Exception {
        String html = "<p>  <br>  &nbsp;<br>&nbsp;  &nbsp;<br>&nbsp;  Hi &nbsp;<br>&nbsp; There  <br>  <br>  <br>  &nbsp;</p>";
        String expected = "Hi  \nThere\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testHtmlCommentBetweenLists() throws Exception {
        String html = "<ul><li>Test</li></ul>"
                + "<ul><li>Test</li></ul>";
        String expected = "* Test\n"
                + "\n\n"
                + "* Test\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testListAgainAndAgain() throws Exception {
        String html = "<ol>\n"
                + " <li>From the <a href=\"/user/apps\">My Apps</a> screen, click on the name of your app.<br> The Details screen appears.&nbsp;</li> \n"
                + " <li>If you have access to more than one API, click the name of the relevant API.&nbsp;<br><strong>Note:</strong> The APIs are listed under the “Credentials” section.&nbsp;</li> \n"
                + " <li>Click the <strong>Show </strong>link below the asterisks that is hiding the <em>Consumer Key</em>.&nbsp;<br> The <em>Consumer Key</em> appears.&nbsp;&nbsp; &nbsp;</li> \n"
                + "</ol>";
        String expected = "1. From the [My Apps](/user/apps) screen, click on the name of your app.  \n"
                + "   The Details screen appears.\n"
                + "2. If you have access to more than one API, click the name of the relevant API.  \n"
                + "   **Note:** The APIs are listed under the \"Credentials\" section.\n"
                + "3. Click the **Show** link below the asterisks that is hiding the *Consumer Key*.  \n"
                + "   The *Consumer Key* appears.\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testList() throws Exception {
        String html = "<ol> \n"
                + " <li>Packet</li> \n"
                + " <li>Airwaybill</li> \n"
                + " <li>Customs</li> \n"
                + "</ol> ";
        String expected = "1. Packet\n2. Airwaybill\n3. Customs\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testListAgain() throws Exception {
        String html = "<p>&nbsp;</p> \n"
                + "<p>Generally, the API provides:</p> \n"
                + "<ol> \n"
                + " <li>Packet</li> \n"
                + " <li>Airwaybill</li> \n"
                + " <li>Customs</li> \n"
                + "</ol> \n"
                + "<p>&nbsp;</p> ";
        String expected = "Generally, the API provides:\n\n1. Packet\n2. Airwaybill\n3. Customs\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testLinebreaksRemovalEasy() throws Exception {
        String html = "<p>Hi</p><p><br /></p>";
        String expected = "Hi\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testLinebreaksRemovalEasyPartTwo() throws Exception {
        String html = "<p>Hello<br />World</p>";
        String expected = "Hello  \n" +
                "World\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testLinebreaksRemovalMoreComplex() throws Exception {
        String html = "<p><strong><span style=\"color:#d40511;\">Note: </span></strong>Usage of HTTP PUT and DELETE is not enabled.</p> \n"
                + "<p>&nbsp;</p> \n"
                + "<h3>&nbsp;</h3> \n"
                + "<ul> \n"
                + "</ul> \n"
                + "<p><br> &nbsp;</p>";
        String expected = "**Note:** Usage of HTTP PUT and DELETE is not enabled.\n";
        assertHtmlToMarkdown(html, expected);
    }
    
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
        String html = "<p><a target=\"_blank\" href=\"/test/123/site\">Link</a></p>\n";
        String expected = "<a target=\"_blank\" href=\"/test/123/site\">Link</a>\n";
        assertHtmlToMarkdown(html, expected);
    }
    
    // ids in headings
    @Test
    void testRemoveIdsFromHeadings() throws Exception {
        String html = "<h3 id=\"test\">h3</h3>\n"+
                "<h4 id=\"test\">h4</h4>\n"+
                "<h5 id=\"test\">h5</h5>\n"+
                "<h6 id=\"test\">h6</h6>";
        String expected = "# h3\n\n" +
                "## h4\n\n" +
                "### h5\n\n" +
                "#### h6\n\n";
        assertHtmlToMarkdown(html, expected);
    }

    public static void assertHtmlToMarkdown(String html, String expected){
        Converter converter = new Converter();
        String markdown = converter.convertHtmlToMarkdown(html, "https://example.com/api-reference/location-finder");
        Assertions.assertEquals(expected, markdown);
        String markdownToHtml = converter.convertMarkdownToHtml(markdown);
        String htmlToMarkdown = converter.convertHtmlToMarkdown(markdownToHtml, "https://example.com/api-reference/location-finder");
        Assertions.assertEquals(markdown, htmlToMarkdown);
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
                "  TEST ME\n" +
                "</div>\n";
        assertHtmlToMarkdown(html, expected);
    }
    
    @Test
    void testDivWithId() throws Exception {
        String html = "<div id=test>TEST ME</div>";
        String expected = "<div id=\"test\">\n  TEST ME\n</div>\n";
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
                " <thead>\n" +
                "  <tr>\n" +
                "   <th> Head 1 </th>\n" +
                "   <th> Head 2 </th>\n" +
                "   <th> Head 3 </th>\n" +
                "  </tr>\n" +
                " </thead>\n" +
                " <tbody>\n" +
                "  <tr>\n" +
                "   <td> First Value 1 </td>\n" +
                "   <td> First Value 2 </td>\n" +
                "   <td> First Value 3 </td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "   <td> Second Value 1 </td>\n" +
                "   <td> Second Value 2 </td>\n" +
                "   <td> Second Value 3 </td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "   <td> Third Value 1 </td>\n" +
                "   <td> Third Value 2 </td>\n" +
                "   <td> Third Value 3 </td>\n" +
                "  </tr>\n" +
                " </tbody>\n" +
                "</table>\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testIgnoreDivTagsWithAttributes() throws Exception {
        String html = "<div class=\"table-responsive\">TEST ME</div>";
        String expected = "<div class=\"table-responsive\">\n" +
                "  TEST ME\n" +
                "</div>\n";
        assertHtmlToMarkdown(html, expected);
    }

    @Test
    void testIgnoreTableTagsWithAttributes() throws Exception {
        String html = "<table class=\"table table-hover table-striped\" cellspacing=\"1\" cellpadding=\"1\" border=\"1\">TEST ME</table>";
        String expected = "<table class=\"table table-hover table-striped\" cellspacing=\"1\" cellpadding=\"1\" border=\"1\">\n" +
                "  TEST ME\n" +
                "</table>\n";
        assertHtmlToMarkdown(html, expected);
    }
    
    @Test
    public void testTableWithStyle() throws Exception {
        String html = "<table align=\"center\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 569px;\">\n"
                + " <tbody>\n"
                + "  <tr>\n"
                + "   <td> <p class=\"text-align-center\">Packet Tracked</p> </td>\n"
                + "   <td style=\"width: 103px;\"> <p class=\"text-align-center\">Packet Plus</p> </td>\n"
                + "   <td style=\"width: 205px;\"> <p class=\"text-align-center\">Packet (Standard / Priority)</p> </td>\n"
                + "   <td style=\"width: 149px;\"> <p class=\"text-align-center\">Packet Return</p> </td>\n"
                + "  </tr>\n"
                + " </tbody>\n"
                + "</table>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    @Test
    public void testTableWithStrong() throws Exception {
        String html = "<table>\n"
                + " <tbody>\n"
                + "  <tr>\n"
                + "   <td><strong>Hi</strong></td>\n"
                + "  </tr>\n"
                + " </tbody>\n"
                + "</table>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    @Test
    public void testTableWithHeight() throws Exception {
        String html = "<table>\n"
                + " <tbody>\n"
                + "  <tr height=\"45\">\n"
                + "   <td height=\"45\">Hi</td>\n"
                + "  </tr>\n"
                + " </tbody>\n"
                + "</table>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }
    
    @Test
    public void testAccordion() throws Exception {
        String html = "<a class=\"btn btn-primary\" data-parent=\"#accordion\" data-toggle=\"collapse\" href=\"#collapse2\">EventAirCode</a>\n";
        ConverterTest.assertHtmlToMarkdown(html, html);
    }

    @Test
    public void testOrderedListAfterTable() {
        String html = "<table align=\"center\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 569px;\">\n" +
                " <tbody>\n" +
                "  <tr>\n" +
                "   <td> <p class=\"text-align-center\">Packet Tracked</p> </td>\n" +
                "   <td style=\"width: 103px;\"> <p class=\"text-align-center\">Packet Plus</p> </td>\n" +
                "   <td style=\"width: 205px;\"> <p class=\"text-align-center\">Packet (Standard / Priority)</p> </td>\n" +
                "   <td style=\"width: 149px;\"> <p class=\"text-align-center\">Packet Return</p> </td>\n" +
                "  </tr>\n" +
                " </tbody>\n" +
                "</table>\n" +
                "<ol> \n" +
                " <li>Packet label</li> \n" +
                " <li>Airwaybill (AWB = transportation document)</li> \n" +
                " <li>Customs document (CN22)</li> \n" +
                "</ol> \n";
        String expected = "<table align=\"center\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 569px;\">\n" +
                " <tbody>\n" +
                "  <tr>\n" +
                "   <td> <p class=\"text-align-center\">Packet Tracked</p> </td>\n" +
                "   <td style=\"width: 103px;\"> <p class=\"text-align-center\">Packet Plus</p> </td>\n" +
                "   <td style=\"width: 205px;\"> <p class=\"text-align-center\">Packet (Standard / Priority)</p> </td>\n" +
                "   <td style=\"width: 149px;\"> <p class=\"text-align-center\">Packet Return</p> </td>\n" +
                "  </tr>\n" +
                " </tbody>\n" +
                "</table>\n" +
                "\n" +
                "1. Packet label\n" +
                "2. Airwaybill (AWB = transportation document)\n" +
                "3. Customs document (CN22)\n";

        assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testBrOnEndOfPTag() throws Exception {
        String html = "<p>The Revoke Access Token API call provides you an option to revoke your Access Token, which not yet expired.<br> &nbsp;</p> ";
        String exprected = "The Revoke Access Token API call provides you an option to revoke your Access Token, which not yet expired.\n";
        ConverterTest.assertHtmlToMarkdown(html, exprected);
    }

    @Test
    public void testBrOnStartOfPTag() throws Exception {
        String html = "<p><br> <strong>Discover when a shipment will arrive&nbsp;</strong></p>\n";
        String exprected = "**Discover when a shipment will arrive**\n";
        ConverterTest.assertHtmlToMarkdown(html, exprected);
    }

    @Test
    @Disabled
    public void testNonBreakingSpaceBeforeBrTag() throws Exception {
        String html = "<p>Test:&nbsp;<br> with non breaking space before br</p>\n" +
                "<p>Test:&nbsp; with non breaking space without br</p>\n";
        String exprected = "**Discover when a shipment will arrive**\n";
        ConverterTest.assertHtmlToMarkdown(html, exprected);
    }

    @Test
    public void testImgTagWithinPTag() throws Exception {
        String html = "<p><img src=\"https://dhl.docker.amazee.io/sites/default/files/api-docs/test256_1.png\" alt=\"Test 256\"></p>";
        String exprected = "![Test 256](https://dhl.docker.amazee.io/sites/default/files/api-docs/test256_1.png)\n";
        ConverterTest.assertHtmlToMarkdown(html, exprected);
    }
    
    @Test
    public void testRelativeImage() throws Exception {
        String html = "<p><img src=\"/images/test256_1.png\" alt=\"Test 256\"></p>";
        String exprected = "![Test 256](/images/test256_1.png)\n";
        ConverterTest.assertHtmlToMarkdown(html, exprected);
    }
    
    @Test
    public void testFrontmatter() throws Exception {
        String input = "---\ntitle: Bla\n---\n# Hi";
        String expected = "<h3>Hi</h3>";
        String actual = new Converter().convertMarkdownToHtml(input);
        Assertions.assertEquals(expected, actual);
    }

    // convert markdown to html

        // fix bugs
            // markdown table is not converted
            // strong bugs

        // modify image tags - set image ids

}

