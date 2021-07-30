package com.sprinteins.drupalcli.extensions;

import com.vladsch.flexmark.html2md.converter.*;
import com.vladsch.flexmark.html2md.converter.internal.HtmlConverterCoreNodeRenderer;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomFlexmarkExtension {

    public static class IgnoreTagExtension implements FlexmarkHtmlConverter.HtmlConverterExtension {
        public static IgnoreTagExtension create() {
            return new IgnoreTagExtension();
        }

        @Override
        public void rendererOptions(@NotNull MutableDataHolder options) {

        }

        @Override
        public void extend(FlexmarkHtmlConverter.@NotNull Builder builder) {
            builder.htmlNodeRendererFactory(new CustomTagConverter.Factory());
        }
    }

    static class CustomTagConverter implements HtmlNodeRenderer {

        private Set<HtmlNodeRendererHandler<?>> defaultRenderers;

        public CustomTagConverter(DataHolder options) {
            defaultRenderers = new HtmlConverterCoreNodeRenderer(options).getHtmlNodeRendererHandlers();
        }
        
        @Override
        public Set<HtmlNodeRendererHandler<?>> getHtmlNodeRendererHandlers() {
            return new HashSet<>(List.of(
                    new HtmlNodeRendererHandler<>(FlexmarkHtmlConverter.A_NODE, Element.class, this::processAnchor),
                    new HtmlNodeRendererHandler<>(FlexmarkHtmlConverter.IMG_NODE, Element.class, this::processImg),
                    new HtmlNodeRendererHandler<>(FlexmarkHtmlConverter.DIV_NODE, Element.class, this::processDiv),
                    new HtmlNodeRendererHandler<>(FlexmarkHtmlConverter.TABLE_NODE, Element.class, this::renderUnchanged)
            ));
        }
        
        private void renderDefault(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out) {
            defaultRenderers.stream()
                    .filter(h -> h.getTagName().equals(node.tagName()))
                    .findFirst()
                    .orElseThrow()
                    .render(node, context, out); 
        }

        private void renderUnchanged(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out) {
            if (node.isBlock()) {
                out.blankLine();
            }
            node.select(".flexmark-whitespace-wrapper").unwrap();
            out.preserveSpaces();
            out.append(node.outerHtml());
            // disable again to prevent wrong line breaks for each node that follows
            out.noPreserveSpaces();
        }
        
        private void processImg(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out) {
            if (node.hasAttr("class")) {
                renderUnchanged(node, context, out);
            } else {
                renderDefault(node, context, out);
            }
        }
        
        private void processDiv(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out) {
            // if the div has any attributes keep everything as is
            if (node.attributes().size() > 0) {
                renderUnchanged(node, context, out);
            } else {
                renderDefault(node, context, out);
            }
        }
        
        private void processAnchor(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out) {
            // if the anchor has any attributes other than href
            if (node.hasAttr("href") && node.attributes().size() > 1) {
                renderUnchanged(node, context, out);
            } else {
                renderDefault(node, context, out);
            }
        }

        static class Factory implements HtmlNodeRendererFactory {
            @Override
            public HtmlNodeRenderer apply(DataHolder options) {
                return new CustomTagConverter(options);
            }
        }

    }

}
