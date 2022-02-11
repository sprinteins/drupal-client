package com.sprinteins.drupalcli.models;

import javax.validation.constraints.Pattern;

public class DateValueModel {

    @Pattern(regexp="^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message="Value for date is not valid, please use YYYY-MM-DD\n")
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
        return "DateValueModel{value='" + value + "', format='" + format + "'}";
    }

}
