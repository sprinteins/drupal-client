package com.sprinteins.drupalcli.extensions;

import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.html2md.converter.*;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.format.TableFormatOptions;
import com.vladsch.flexmark.util.html.CellAlignment;
import com.vladsch.flexmark.util.misc.Ref;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.*;
import java.util.regex.Pattern;

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
        public CustomTagConverter(DataHolder options) {

        }

        @Override
        public Set<HtmlNodeRendererHandler<?>> getHtmlNodeRendererHandlers() {
            return new HashSet<>(List.of(
                    new HtmlNodeRendererHandler<>("div", Element.class, this::processHtml),
                    new HtmlNodeRendererHandler<>("table", Element.class, this::processHtml)
            ));
        }

        private void processHtml(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out) {
            out.append(node.outerHtml());
        }

        static class Factory implements HtmlNodeRendererFactory {
            @Override
            public HtmlNodeRenderer apply(DataHolder options) {
                return new CustomTagConverter(options);
            }
        }

    }

}
