package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class NodeModelTest {

    @Test
    void testJsonSerialization() throws Exception {
        NodeModel node = new NodeModel();
        node.getOrCreateFirstSourceFile().setTargetId(820L);

        testSerialization("node-serialize-source-file",
                node);
    }

    @Test
    void testNodeJsonDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String testFileAsString = TestFiles.readAllBytesToString("json/node-54-exported.json");

        var node = objectMapper.readValue(testFileAsString, NodeModel.class);
        Assertions.assertEquals( 3,node.getGetStartedDocsElements().size());
    }



    private void testSerialization(String path, Object value) throws Exception {
        String expected = TestFiles
                .readAllBytesToString("json/" + path + ".json");


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String actual = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(value);

        JSONAssert.assertEquals(expected, actual, true);
    }
}
