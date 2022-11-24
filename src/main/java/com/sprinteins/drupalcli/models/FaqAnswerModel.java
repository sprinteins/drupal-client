package com.sprinteins.drupalcli.models;

public class FaqAnswerModel {

    private String value;
    private ValueFormat format;
    private String processed;
    
    public static FaqAnswerModel basicHtml(String html) {
      FaqAnswerModel result = new FaqAnswerModel();
        result.value = html;
        result.format = ValueFormat.BASIC_HTML;
        return result;
    }
      
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueFormat getFormat() {
        return format;
    }

    public void setFormat(ValueFormat format) {
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
        return "FaqAnswerModel {value=" + value + ", format=" + format + ", processed= " + processed + "}";
    }

}
