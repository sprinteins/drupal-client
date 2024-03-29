package com.sprinteins.drupalcli.fields;

public class UriValueModel {

    private String value;
    private String url;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UriValueModel {value=" + value + ", url=" + url + "}";
    }

}
