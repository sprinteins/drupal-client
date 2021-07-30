package com.sprinteins.drupalcli.html;

import com.sprinteins.drupalcli.ApplicationContext;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.converter.Converter;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.GetStartedDocsElementModel;
import com.sprinteins.drupalcli.models.ReleaseNoteElementModel;
import com.sprinteins.drupalcli.node.NodeClient;
import com.sprinteins.drupalcli.node.NodeModel;
import com.sprinteins.drupalcli.paragraph.GetStartedParagraphModel;
import com.sprinteins.drupalcli.paragraph.ReleaseNoteParagraphModel;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlexmarkJavaTest {
    
    @Test
    void testName() throws Exception {
        String html = TestFiles.readAllBytesToString("html/overview.html");
        String markdown = FlexmarkHtmlConverter.builder().build().convert(html);
        System.out.println(markdown);
    }

    @Test
    void testName2() throws Exception {
        String html = TestFiles.readAllBytesToString("markdown/overview.markdown");
        String markdown = FlexmarkHtmlConverter.builder().build().convert(html);
        System.out.println(markdown);
    }

    @Test
    @Disabled
    void testCompareHtml() throws IOException, InterruptedException {
        List<String> linkList = new ArrayList<>();
        linkList.add("http://dhl.docker.amazee.io/api-reference/deutsche-post-international-post-parcel-germany");
        linkList.add("http://dhl.docker.amazee.io/api-reference/clone-deutsche-post-international-post-parcel-germany");
        linkList.add("http://dhl.docker.amazee.io/api-reference/parcel-eu");
        linkList.add("http://dhl.docker.amazee.io/api-reference/shipment-tracking");
        linkList.add("http://dhl.docker.amazee.io/api-reference/dgf-freight-tracking");
        linkList.add("http://dhl.docker.amazee.io/api-reference/location-finder");

        ApplicationContext applicationContext = new ApplicationContext("http://dhl.docker.amazee.io", "d4b611bea88b5bbaaa79018ab5b5da4b");
        NodeClient nodeClient = applicationContext.nodeClient();
        var getStartedParagraphClient = applicationContext.getStartedParagraphClient();
        var releaseNoteParagraphModelParagraphClient = applicationContext.releaseNoteParagraphClient();
        Converter converter = new Converter();
        
        for(String link: linkList){
            NodeModel nodeModel = nodeClient.getByUri(link);

            String workingDir = "./target/test-compare-html";
            
            for(GetStartedDocsElementModel getStartedDocsElement: nodeModel.getGetStartedDocsElement()) {
                GetStartedParagraphModel getStartedParagraph = getStartedParagraphClient.get(getStartedDocsElement.getTargetId());
                DescriptionModel descriptionModel = getStartedParagraph.getOrCreateFirstDescription();
                Document doc = Jsoup.parse(descriptionModel.getProcessed());

                //get html
                String html = doc.body().html();

                //save html
                Files.createDirectories(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "html"));
                Files.writeString(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "html", getStartedParagraph
                        .getOrCreateFirstTitle()
                        .getValue()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "-") + ".html"), html);

                //convert to markdown
                String markdown = converter.convertHtmlToMarkdown(html, link);

                //save markdown
                Files.createDirectories(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "markdown"));
                Files.writeString(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "markdown", getStartedParagraph
                        .getOrCreateFirstTitle()
                        .getValue()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "-") + ".markdown"), markdown);

                //reconvert markdown to html
                String parsedHtml = converter.convertMarkdownToHtml(markdown);

                //save new html
                Files.createDirectories(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "newHtml"));
                Files.writeString(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "newHtml", getStartedParagraph
                        .getOrCreateFirstTitle()
                        .getValue()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "-") + ".html"), parsedHtml);
                
                String markdownHtmlToMarkdown = converter.convertHtmlToMarkdown(parsedHtml, link);

                Assertions.assertEquals(markdown, markdownHtmlToMarkdown);

            }

            for(ReleaseNoteElementModel releaseNoteElementModel: nodeModel.getReleaseNotesElement()) {
                ReleaseNoteParagraphModel releaseNoteParagraphModel = releaseNoteParagraphModelParagraphClient.get(releaseNoteElementModel.getTargetId());
                DescriptionModel descriptionModel = releaseNoteParagraphModel.getOrCreateFirstDescription();
                Document doc = Jsoup.parse(descriptionModel.getProcessed());

                //convert to md
                String html = doc.body().html();

                //save html
                Files.createDirectories(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "html"));
                Files.writeString(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "html", releaseNoteParagraphModel
                        .getOrCreateFirstTitle()
                        .getValue()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "-") + ".html"), html);

                String markdown = new Converter().convertHtmlToMarkdown(html, link);

                //save markdown
                Files.createDirectories(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "markdown"));
                Files.writeString(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "markdown", releaseNoteParagraphModel
                        .getOrCreateFirstTitle()
                        .getValue()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "-") + ".markdown"), markdown);

                //reconvert markdown to html
                String parsedHtml = converter.convertMarkdownToHtml(markdown);

                //save new html
                Files.createDirectories(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "newHtml"));
                Files.writeString(Paths.get(workingDir, nodeModel.getOrCreateFirstDisplayTitle().getValue(), "newHtml", releaseNoteParagraphModel
                        .getOrCreateFirstTitle()
                        .getValue()
                        .toLowerCase(Locale.ROOT)
                        .replace(" ", "-") + ".html"), parsedHtml);

                String markdownHtmlToMarkdown = converter.convertHtmlToMarkdown(parsedHtml, link);

                Assertions.assertEquals(markdown, markdownHtmlToMarkdown);
            }
        }



    }
}
