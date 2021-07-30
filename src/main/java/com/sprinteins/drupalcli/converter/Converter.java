package com.sprinteins.drupalcli.converter;

import com.sprinteins.drupalcli.extensions.CustomFlexmarkExtension;
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
import org.jsoup.safety.Whitelist;

import java.util.Collections;

public class Converter {

    public Converter() {
    }

    public String convertHtmlToMarkdown(String input, String baseUri){
        // microsoft word aka long dash is replaced with regular minus
        input = input.replace("–","-");
        input = input.replace("&nbsp;", " ");
        input = input.replaceAll("\s*<br>\s*", "<br>");
        input = input.replaceAll("\s*<br\s*/>\s*", "<br/>");
        Document document = Jsoup.parse(input, baseUri);
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.removeTags("u");
        whitelist.addAttributes("table", "style", "class", "align", "border", "cellpadding", "cellspacing");
        whitelist.addAttributes("tr", "style", "class", "height");
        whitelist.addAttributes("td", "style", "class", "height");
        whitelist.addAttributes("p", "style", "class");
        whitelist.addAttributes("div", "id", "class");
        whitelist.addAttributes("col", "style");
        whitelist.addAttributes("a", "class", "data-parent", "data-toggle", "target");
        whitelist.preserveRelativeLinks(false);

        Cleaner cleaner = new Cleaner(whitelist);
        document = cleaner.clean(document);

        for(Element anchorTag: document.select("a")){
            String uri = anchorTag.attr("href");
            anchorTag.attr("href", StringUtils.removeStart(uri, baseUri));
        }

        for (Element element : document.select("strong,em")) {
            // if the text inside the element ends with an empty space
            if (element.wholeText().endsWith(" ")) {
                // add one after the element so it will be collapsed properly
                element.after(" ");
            }
            // this prevents flexmark from adding unnecessary whitespace if a tag follows
            element.wrap("<span class=flexmark-whitespace-wrapper></span>");
        }
        
        for(Element element : document.select("p,h1,h2,h3,h4,h5,h6")){
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
                element.unwrap().wrap("<pre><code></code></pre>");
            }
        }

        // add options to the HtmlConverter
        DataHolder options = new MutableDataSet()
                .set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false)
                .set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false)
                .set(Parser.EXTENSIONS, Collections.singletonList(CustomFlexmarkExtension.IgnoreTagExtension.create()))
                .toImmutable();

        return FlexmarkHtmlConverter.builder(options).build().convert(document.body().html());
    }

    public String convertMarkdownToHtml(String input){
        String html = HtmlRenderer.builder()
            .build()
            .render(Parser.builder()
                    .build()
                    .parse(input));
        return Jsoup.parse(html).body().html();
    }

}
