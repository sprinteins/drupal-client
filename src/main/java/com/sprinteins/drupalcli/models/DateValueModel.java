package com.sprinteins.drupalcli.models;

public class DateValueModel {

    private String value;
    private String format;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "DateValueModel{" +
                "value='" + value + '\'' +
                ", format='" + format + '\'' +
                '}';
    }

}
