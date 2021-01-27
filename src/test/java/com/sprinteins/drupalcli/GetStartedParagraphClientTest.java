package com.sprinteins.drupalcli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetStartedParagraphClientTest {

    @LocalServerPort
    private int port;

    @Test
    public void testPatchSuccess() {
        GetStartedParagraphModel getStartedParagraph = new GetStartedParagraphModel();

        DescriptionModel fieldDescription = getStartedParagraph
                .getOrCreateFirstDescription();
        fieldDescription.setFormat(ValueFormat.GITHUB_FLAVORED_MARKDOWN);
        fieldDescription.setValue("Test Value");
        try {
            new GetStartedParagraphClient(new ObjectMapper(), "http://localhost:" + port + "/entity/paragraph/", "")
                    .patch(195, getStartedParagraph);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    public void testPatchNotFound() throws Exception {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            new GetStartedParagraphClient(new ObjectMapper(), "http://localhost:" + port + "/not-found/", "")
                    .patch(195, new GetStartedParagraphModel());
        });
    }
}
