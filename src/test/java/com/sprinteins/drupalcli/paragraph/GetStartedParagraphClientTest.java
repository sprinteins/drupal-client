package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.TextFormat;
import com.sprinteins.drupalcli.mock.DrupalMockApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DrupalMockApplication.class)
public class GetStartedParagraphClientTest {

    @LocalServerPort
    private int port;

    @Test
    public void testPatchSuccess() {
        GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();

        FormattedTextModel fieldDescription = getStartedParagraph
                .getOrCreateFirstDescription();
        fieldDescription.setFormat(TextFormat.GITHUB_FLAVORED_MARKDOWN);
        fieldDescription.setValue("Test Value");
        try {
            new ParagraphClient<>(
                    new ObjectMapper(),
                    "http://localhost:" + port, "",
                    GetStartedParagraphModel.class,
                    HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build())
                        .patch(195, getStartedParagraph);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    public void testPatchNotFound()  {
        Assertions.assertThrows(IllegalStateException.class, () -> new ParagraphClient<>(
                new ObjectMapper(),
                "http://localhost:" + port + "/not-found/",
                "",
                GetStartedParagraphModel.class,
                HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build())
                    .patch(195, new GetStartedParagraphModel()));
    }
}
