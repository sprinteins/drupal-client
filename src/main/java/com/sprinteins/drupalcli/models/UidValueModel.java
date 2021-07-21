package com.sprinteins.drupalcli.models;

public class UidValueModel extends TargetModel {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UidValueModel{url='" + url + "', targetId='" + getTargetId() + "', targetType='"
                + getTargetType() + "', targetUuid='" + getTargetUuid() + "'}";
    }

}
