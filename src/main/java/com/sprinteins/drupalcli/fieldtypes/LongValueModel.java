package com.sprinteins.drupalcli.fieldtypes;

public class LongValueModel {

    private Long value;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LongValueModel {value=" + value + "}";
    }

}
