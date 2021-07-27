package com.sprinteins.drupalcli.html;

import com.vladsch.flexmark.html2md.converter.ExtensionConversion;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlToMarkdownTest {
    
    @Test
    public void testSpacesInAndAroundStrong() throws Exception {
        String input = "<p><strong><span style=\"color:#d40511;\">Note: </span></strong>Usage of HTTP PUT and DELETE is not enabled.</p>";
        String expected = "**Note:** Usage of HTTP PUT and DELETE is not enabled.\n";
        extracted(input, expected);
    }
    
    @Test
    public void testStrongInsidePre() throws Exception {
        String input = "<pre><strong>429</strong></pre>";
        String expected = "```\n429\n```\n\n";
        extracted(input, expected);
    }
    
    @Test
    public void testNormalMinusInText() throws Exception {
        String input = "<p>Hallo - na wie gehts?</p>";
        String expected = "Hallo - na wie gehts?\n";
        extracted(input, expected);
    }
    
    @Test
    public void testWordDashBeingReplacedWithTwoMinuses() throws Exception {
        // this is a word dash
        String input = "<p>Hallo – na wie gehts?</p>";
        String expected = "Hallo -- na wie gehts?\n";
        extracted(input, expected);
    }
    
    @Test
    public void testHeaderWithIds() throws Exception {
        String input = "<h1 id=bla>Header</h1>";
        String expected = "# Header\n\n";
        extracted(input, expected);
    }
    
    @Test
    public void testDivs() throws Exception {
        String input = "<div><h3 id=bla>Header</h1></div>";
        String expected = "# Header\n\n";
        extracted(input, expected);
    }
    
    @Test
    public void testTables() throws Exception {
        String input = "<table><td>Hi</td></table>";
        String expected = "";
        extracted(input, expected);
    }
    
    @Test
    public void testStrongInList() throws Exception {
        String input = "<ul><li><strong>Bla</strong>:</li></ul>";
        String expected = "* **Bla**:\n";
        extracted(input, expected);
    }
    
    @Test
    public void testStrongWithInnerList() throws Exception {
        // looks like a flexmark bug
        String input = "<ul><li><strong>Bla</strong>:<ul><li>Inner list</li></ul></li></ul>";
        String expected = "* **Bla**:\n  * Inner list\n";
        extracted(input, expected);
    }
    
    @Test
    public void testEmInBracketsWorking() throws Exception {
        String input = "<p>Austria (<em>AT</em>), Belgium (BE)</p>";
        String expected = "Austria (*AT*), Belgium (BE)\n";
        extracted(input, expected);
    }
    
    @Test
    public void testEmInBracketsNotWorking() throws Exception {
        // looks like a flexmark bug
        String input = "<p>Austria (<em>AT</em>), Belgium (<em>BE</em>)</p>";
        String expected = "Austria (*AT*), Belgium (*BE*)\n";
        extracted(input, expected);
    }
    
    private String convertToMarkdown(String input) {
        Document document = Jsoup.parseBodyFragment(input);
        for (Element strongElement : document.select("strong")) {
            // if the text inside the element ends with an empty space
            if (strongElement.wholeText().endsWith(" ")) {
                // add one after the element so it will be collapsed properly
                strongElement.after(" ");
            }
        }
        for (Element headerElement : document.select("h3")) {
            headerElement.tagName("h1");
        }
        for (Element headerElement : document.select("h4")) {
            headerElement.tagName("h2");
        }
        for (Element headerElement : document.select("h5")) {
            headerElement.tagName("h3");
        }
        for (Element headerElement : document.select("h6")) {
            headerElement.tagName("h4");
        }
        DataHolder options = new MutableDataSet()
                .set(FlexmarkHtmlConverter.EXT_TABLES, ExtensionConversion.HTML)
                .set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false)
                .set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false)
                .toImmutable();
        return FlexmarkHtmlConverter.builder(options).build().convert(document.html());
    }

    private void extracted(String input, String expected) {
        String markdown = convertToMarkdown(input);
        assertEquals(expected, markdown);
    }

}
