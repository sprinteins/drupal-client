package com.sprinteins.drupalcli.models;

public class StringValueModel {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StringValueModel {value=" + value + "}";
    }

}
