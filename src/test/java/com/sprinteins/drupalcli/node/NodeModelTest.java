package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprinteins.drupalcli.TestFiles;
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

    private void testSerialization(String path, Object value) throws Exception {
        String expected = TestFiles
                .readAllBytesToString("json/" + path + ".json");

        String actual = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(value);

        JSONAssert.assertEquals(expected, actual, true);
    }

}
