package com.sprinteins.drupalcli.converter;

import com.sprinteins.drupalcli.extensions.CustomFlexmarkExtension;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Converter {

    public Converter() {
    }

    public String convertHtmlToMarkdown(String input, String baseUri){
        // microsoft word aka long dash is replaced with regular minus
        input = input.replace("–","-");
        Document document = Jsoup.parse(input, baseUri);
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.removeTags("u");
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
            element.wrap("<span></span>");
        }


        MutableDataSet options = new MutableDataSet()
                .set(FlexmarkHtmlConverter.SETEXT_HEADINGS, false)
                .set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false)
                .set(Parser.EXTENSIONS, Collections.singletonList(CustomFlexmarkExtension.IgnoreTagExtension.create()));
        // add options to the HtmlConverter

        return FlexmarkHtmlConverter.builder(options).build().convert(document.html());
    }

    public String convertMarkdownToHtml(String input){
        return "";
    }

}
