package com.sprinteins.drupalcli.converter;

import org.junit.jupiter.api.Test;

public class ConverterEmTest {

    @Test
    void testFixEmTag() throws Exception {
        String html = "<p><em><span style=\"color:#d40511;\">Note: </span></em>Usage of HTTP PUT and DELETE is not enabled.</p>";
        String expected = "*Note:* Usage of HTTP PUT and DELETE is not enabled.\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testEmInsidePre() throws Exception {
        String html = "<pre><em>429</em></pre>";
        String expected = "```\n429\n```\n\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testEmInList() throws Exception {
        String html = "<ul><li><em>Bla</em>:</li></ul>";
        String expected = "* *Bla*:\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testEmWithInnerList() throws Exception {
        // looks like a flexmark bug
        String html = "<ul><li><em>Bla</em>:<ul><li>Inner list</li></ul></li></ul>";
        String expected = "* *Bla*:\n  * Inner list\n";
        ConverterTest.assertHtmlToMarkdown(html, expected);
    }

    @Test
    public void testEmInBracketsWorking() throws Exception {
        String input = "<p>Austria (<em>AT</em>), Belgium (BE)</p>";
        String expected = "Austria (*AT*), Belgium (BE)\n";
        ConverterTest.assertHtmlToMarkdown(input, expected);
    }

    @Test
    public void testEmInBracketsNotWorking() throws Exception {
        // looks like a flexmark bug
        String input = "<p>Austria (<em>AT</em>), Belgium (<em>BE</em>)</p>";
        String expected = "Austria (*AT*), Belgium (*BE*)\n";
        ConverterTest.assertHtmlToMarkdown(input, expected);
    }
}
