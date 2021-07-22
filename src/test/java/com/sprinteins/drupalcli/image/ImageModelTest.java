package com.sprinteins.drupalcli.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
import com.sprinteins.drupalcli.models.LinksModel;
import com.sprinteins.drupalcli.models.StringValueModel;
import com.sprinteins.drupalcli.models.TypeModel;
import com.sprinteins.drupalcli.models.UriValueModel;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

public class ImageModelTest {

    @Test
    void testModelSerialization() throws Exception {
//        ImageModel image = new ImageModel();
//
//        LinksModel links = new LinksModel();
//        TypeModel linkType = new TypeModel();
//
//        StringValueModel filename = new StringValueModel();
//        StringValueModel filemime = new StringValueModel();
//
//        List<UriValueModel> listOfUri = new ArrayList<>();
//        UriValueModel uri = new UriValueModel();
//
//        List<StringValueModel> listOfData = new ArrayList<>();
//        StringValueModel data = new StringValueModel();
//
//        linkType.setHref("http://dhl.docker.amazee.io/rest/type/file/image");
//        links.setType(linkType);
//        uri.setValue("public://api-docs/test-file.png");
//        listOfUri.add(uri);
//        filename.setValue("test-file.png");
//        filemime.setValue("image/png");
//        data.setValue("DATA_HERE");
//        listOfData.add(data);
//
//        image.setLinks(links);
//        image.setUri(listOfUri);
//        image.setFilename(filename);
//        image.setFilemime(filemime);
//        image.setData(listOfData);
//
//        String expected = TestFiles
//                .readAllBytesToString("json/image-request-model.json");
//
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        String actual = objectMapper.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(image);
//
//        JSONAssert.assertEquals(expected, actual, true);
    }
}
