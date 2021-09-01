package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;

public class NodeClientTest {

    @Test
    public void testCleanUri() throws Exception {
        String link = "http://dhl.docker.amazee.io/api-reference/clone-deutsche-post-international-post-parcel-germany#get-started-section/overview";
        String expected = "http://dhl.docker.amazee.io/api-reference/clone-deutsche-post-international-post-parcel-germany";

        String actual = new NodeClient(
                new ObjectMapper(),
                "",
                "",
                HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build())
                    .cleanLink(link);

        Assertions.assertEquals(expected, actual);
    }
}
