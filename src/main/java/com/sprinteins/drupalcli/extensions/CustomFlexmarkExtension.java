package com.sprinteins.drupalcli.extensions;

import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.html2md.converter.*;
import com.vladsch.flexmark.html2md.converter.internal.HtmlConverterCoreNodeRenderer;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

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
            builder.linkResolverFactory(new CustomLinkResolver.Factory());
            builder.htmlNodeRendererFactory(new CustomTagConverter.Factory());
        }
    }

    static class CustomLinkResolver implements HtmlLinkResolver {
        public CustomLinkResolver(HtmlNodeConverterContext context) {
        }

        @Override
        public ResolvedLink resolveLink(Node node, HtmlNodeConverterContext context, ResolvedLink link) {
            // convert all links from http:// to https://
            if (link.getUrl().startsWith("http:")) {
                return link.withUrl("https:" + link.getUrl().substring("http:".length()));
            }
            return link;
        }

        static class Factory implements HtmlLinkResolverFactory {
            @Nullable
            @Override
            public Set<Class<?>> getAfterDependents() {
                return null;
            }

            @Nullable
            @Override
            public Set<Class<?>> getBeforeDependents() {
                return null;
            }

            @Override
            public boolean affectsGlobalScope() {
                return false;
            }

            @Override
            public HtmlLinkResolver apply(HtmlNodeConverterContext context) {
                return new CustomLinkResolver(context);
            }
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
