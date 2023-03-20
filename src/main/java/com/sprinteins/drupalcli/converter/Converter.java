package com.sprinteins.drupalcli.converter;

import com.sprinteins.drupalcli.extensions.CustomFlexmarkExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

public class Converter {

    private static final Pattern REMOVE_TRAILING_PATTERN = Pattern.compile("''+$", Pattern.MULTILINE);

    private final boolean customHtml;

    public Converter() {
        this.customHtml = false;
    }
    public Converter(boolean customHtml) {
        this.customHtml = customHtml;
    }

    public String convertHtmlToMarkdown(String input, String link){
        // Microsoft Word aka long dash is replaced with regular minus
        input = input.replace("–","-");
        input = input.replace("&nbsp;", " ");

        Document document = Jsoup.parse(input, link);
        Safelist safelist = Safelist.relaxed();
        safelist.removeTags("u");
        safelist.addAttributes("table", "style", "class", "align", "border", "cellpadding", "cellspacing");
        safelist.addAttributes("tr", "style", "class", "height");
        safelist.addAttributes("td", "style", "class", "height");
        safelist.addAttributes("p", "style", "class");
        safelist.addAttributes("div", "id", "class");
        safelist.addAttributes("col", "style");
        safelist.addAttributes("a", "class", "data-parent", "data-toggle", "target");
        safelist.preserveRelativeLinks(true);

        Cleaner cleaner = new Cleaner(safelist);
        document = cleaner.clean(document);
        
        for(Element br : document.select("br")){
            Node sibling = br.previousSibling();
            if (sibling instanceof TextNode) {
                TextNode textNode = (TextNode) sibling;
                textNode.text(textNode.text().stripTrailing());
            }
            sibling = br.nextSibling();
            if (sibling instanceof TextNode) {
                TextNode textNode = (TextNode) sibling;
                textNode.text(textNode.text().stripLeading());
            }
        }

        for(Element anchorTag: document.select("a")){
            URI uri = URI.create(link);
            String baseUri = uri.getScheme() + "://" + uri.getHost();
            String href = anchorTag.attr("href");
            href = StringUtils.removeStart(href, link);
            href = StringUtils.removeStart(href, baseUri);
            anchorTag.attr("href", href);
        }

        for (Element element : document.select("strong,em")) {
            // if the text inside the element ends with an empty space
            if (element.wholeText().endsWith(" ")) {
                // add one after the element , so it will be collapsed properly
                element.after(" ");
            }
            // this prevents flexmark from adding unnecessary whitespace if a tag follows
            element.wrap("<span class=flexmark-whitespace-wrapper></span>");
        }
        
        for(Element element : document.select("p,h1,h2,h3,h4,h5,h6,ol,ul,li")){
            for (int i = 0; i < element.childNodeSize(); i++) {
                Node childNode = element.childNode(i);
                if (childNode instanceof TextNode) {
                    TextNode textNode = (TextNode) childNode;
                    if (textNode.isBlank()) {
                        textNode.remove();
                        // set iterator back by one since we removed a child node
                        i = Math.max(-1, i - 1);
                    } else {
                        break;
                    }
                } else if (childNode instanceof Element) {
                    Element childElement = (Element) childNode;
                    if (childElement.tagName().equals("br")) {
                        childElement.remove();
                        // set iterator back by one since we removed a child node
                        i = Math.max(-1, i - 1);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            for (int i = element.childNodeSize() - 1; i >= 0; i--) {
                Node childNode = element.childNode(i);
                if (childNode instanceof TextNode) {
                    TextNode textNode = (TextNode) childNode;
                    if (textNode.isBlank()) {
                        textNode.remove();
                        // set iterator back by one since we removed a child node
                        i = Math.min(element.childNodeSize(), i + 1);
                    } else {
                        break;
                    }
                } else if (childNode instanceof Element) {
                    Element childElement = (Element) childNode;
                    if (childElement.tagName().equals("br")) {
                        childElement.remove();
                        // set iterator back by one since we removed a child node
                        i = Math.min(element.childNodeSize(), i + 1);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        for(Element element : document.select("h1,h2,h3,h4,h5,h6,p,ol,ul")){
            if (element.children().isEmpty() && element.text().isBlank()) {
                element.remove();
            }
        }
        
        for(Element element : document.select("pre")){
            if (element.select("code").isEmpty()) {
                Objects.requireNonNull(element.unwrap()).wrap("<pre><code></code></pre>");
            }
        }
        
        document.select("h3").tagName("h1");
        document.select("h4").tagName("h2");
        document.select("h5").tagName("h3");
        document.select("h6").tagName("h4");

        // add options to the HtmlConverter
        DataHolder options = generateConverterOptions();

        return FlexmarkHtmlConverter.builder(options).build().convert(document.body().html());
    }

    public String convertMarkdownToHtml(String input){
        DataHolder options = new MutableDataSet()
                .set(Parser.LISTS_END_ON_DOUBLE_BLANK, true)
                .set(TablesExtension.COLUMN_SPANS, false)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, false)
                .set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), YamlFrontMatterExtension.create()))
                .toImmutable();

        String html = HtmlRenderer.builder(options)
            .build()
            .render(Parser.builder(options)
                    .build()
                    .parse(input));

        Document document = Jsoup.parse(html);

        document.select("h4").tagName("h6");
        document.select("h3").tagName("h5");
        document.select("h2").tagName("h4");
        document.select("h1").tagName("h3");

        String body = document.body().html();

        return REMOVE_TRAILING_PATTERN.matcher(body).replaceAll("");
    }

    private DataHolder generateConverterOptions() {
        if(customHtml) {
            return new MutableDataSet()
                .set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false)
                .set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false)
                .set(FlexmarkHtmlConverter.LISTS_END_ON_DOUBLE_BLANK, true)
                .set(Parser.EXTENSIONS, Collections.singletonList(CustomFlexmarkExtension.IgnoreTagExtension.create()))
                .toImmutable();
        } else {
            return new MutableDataSet()
                .set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false)
                .set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false)
                .set(FlexmarkHtmlConverter.LISTS_END_ON_DOUBLE_BLANK, true)
                .set(TablesExtension.COLUMN_SPANS, false)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, false)
                .set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()))
                .toImmutable();
        }
    }
}
