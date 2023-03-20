package com.sprinteins.drupalcli.fields;

import com.sprinteins.drupalcli.fieldtypes.TextFormat;

public class FaqAnswerModel {

    private String value;
    private TextFormat format;
    private String processed;

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
        return "FaqAnswerModel {value=" + value + ", format=" + format + ", processed= " + processed + "}";
    }

}
