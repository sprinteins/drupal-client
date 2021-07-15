package com.sprinteins.drupalcli;

import java.util.List;
import java.util.Optional;

public class Main {

    private Long node;
    private String swagger;
    private List<ParagraphConfig> paragraphs;

    public Long getNode() {
        return node;
    }

    public void setNode(Long node) {
        this.node = node;
    }

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public List<ParagraphConfig> getParagraphs() {
        return Optional.ofNullable(paragraphs).map(List::copyOf).orElse(null);
    }

    public void setParagraphs(List<ParagraphConfig> paragraphs) {
        this.paragraphs = Optional.ofNullable(paragraphs).map(List::copyOf).orElse(null);
    }

    public static class ParagraphConfig {
        private Long id;
        private String title;
        private String content;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
