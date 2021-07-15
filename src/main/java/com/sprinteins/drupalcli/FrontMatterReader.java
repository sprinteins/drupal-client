package com.sprinteins.drupalcli;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrontMatterReader {

    private static final Set<Extension> EXTENSIONS = Collections.singleton(YamlFrontMatterExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

    public Map<String, List<String>> readFromFile(String fileString) throws Exception {
        YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
        Node document = PARSER.parse(fileString);
        document.accept(visitor);
        return visitor.getData();
    }
}
