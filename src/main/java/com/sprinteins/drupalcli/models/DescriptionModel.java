package com.sprinteins.drupalcli.models;

public class DescriptionModel {

    private String value;
    private ValueFormat format;

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

    @Override
    public String toString() {
        return "DescriptionModel {value=" + value + ", format=" + format + "}";
    }

}
