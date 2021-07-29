package com.sprinteins.drupalcli.converter;

import org.junit.jupiter.api.Test;

public class ConverterStrongTest {

    @Test
    void testFixStrongTag() throws Exception {
        String html = "<p><strong><span style=\"color:#d40511;\">Note: </span></strong>Usage of HTTP PUT and DELETE is not enabled.</p>";
        String expected = "**Note:** Usage of HTTP PUT and DELETE is not enabled.\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testStrongInsidePre() throws Exception {
        String html = "<pre><strong>429</strong></pre>";
        String expected = "    429\n\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testStrongInList() throws Exception {
        String html = "<ul><li><strong>Bla</strong>:</li></ul>";
        String expected = "* **Bla**:\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testStrongWithInnerList() throws Exception {
        // looks like a flexmark bug
        String html = "<ul><li><strong>Bla</strong>:<ul><li>Inner list</li></ul></li></ul>";
        String expected = "* **Bla**:\n  * Inner list\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }
}
