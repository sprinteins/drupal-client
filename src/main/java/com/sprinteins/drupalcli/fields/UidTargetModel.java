package com.sprinteins.drupalcli.fields;

import com.sprinteins.drupalcli.fields.TargetModel;

public class UidTargetModel extends TargetModel {

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
