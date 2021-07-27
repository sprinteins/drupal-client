package com.sprinteins.drupalcli;


import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.misc.Extension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrontMatterReader {

    private static final Set<Extension> EXTENSIONS = Collections.singleton(YamlFrontMatterExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

    public Map<String, List<String>> readFromString(String content) throws Exception {
        AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
        Node document = PARSER.parse(content);
        visitor.visit(document);
        return visitor.getData();
    }
}
