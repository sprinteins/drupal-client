package com.sprinteins.drupalcli.fields;

public class FaqQuestionModel {

    private String value;
      
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FaqQuestionModel {value=" + value + "}";
    }

}
