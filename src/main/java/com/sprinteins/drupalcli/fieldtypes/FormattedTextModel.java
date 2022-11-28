package com.sprinteins.drupalcli.fieldtypes;

public class FormattedTextModel {

    private String value;
    private TextFormat format;
    private String processed;
    
    public static FormattedTextModel basicHtml(String html) {
        FormattedTextModel result = new FormattedTextModel();
        result.value = html;
        result.format = TextFormat.BASIC_HTML;
        return result;
    }
    
    public FormattedTextModel() {
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TextFormat getFormat() {
        return format;
    }

    public void setFormat(TextFormat format) {
        this.format = format;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "DescriptionModel {value=" + value + ", format=" + format + ", processed= " + processed + "}";
    }

}
