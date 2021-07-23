package com.sprinteins.drupalcli.commands;

import com.sprinteins.drupalcli.file.FileUploadModel;
import com.sprinteins.drupalcli.models.StringValueModel;
import com.sprinteins.drupalcli.models.UriValueModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UpdateTest {

    @Test
    public void testReplaceImageTag() {
        Update update = new Update();
        FileUploadModel imageModel = new FileUploadModel();
        List<StringValueModel> uuidList = new ArrayList<>();
        List<UriValueModel> uriList = new ArrayList<>();
        StringValueModel uuid = new StringValueModel();
        UriValueModel uri = new UriValueModel();

        String markdown = "* Document dimensions: specified by the user, it's always a square.\n" +
                "* Format support: PNG\n" +
                "\n" +
                "![QR Code](images/qr_code.png)\n" +
                "\n" +
                "### Using the API\n";

        String expected = "* Document dimensions: specified by the user, it's always a square.\n" +
                "* Format support: PNG\n" +
                "\n" +
                "<img alt=\\\"QR Code\\\" data-align=\\\"center\\\" data-entity-type=\\\"file\\\" data-entity-uuid=\\\"ba7f7051-6273-4d66-8ddb-f12b6d39a814\\\" src=\\\"/sites/default/files/api-docs/media/qr_code.png\\\" />\n" +
                "\n" +
                "### Using the API\n";


        uuid.setValue("ba7f7051-6273-4d66-8ddb-f12b6d39a814");
        uri.setUrl("/sites/default/files/api-docs/media/qr_code.png");
        uuidList.add(uuid);
        uriList.add(uri);
        imageModel.setUuid(uuidList);
        imageModel.setUri(uriList);

        String actual = update.replaceImageTag(markdown, Paths.get("images/qr_code.png"), imageModel);
        Assertions.assertEquals(expected, actual);

    }
}
