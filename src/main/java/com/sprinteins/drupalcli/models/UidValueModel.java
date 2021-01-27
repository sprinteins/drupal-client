package com.sprinteins.drupalcli.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UidValueModel {

    private Long targetId;
    private String targetType;
    private String targetUuid;
    private String url;

    @JsonProperty("target_id")
    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    @JsonProperty("target_type")
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @JsonProperty("target_uuid")
    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UidValueModel{" +
                "targetId=" + targetId +
                ", targetType='" + targetType + '\'' +
                ", targetUuid='" + targetUuid + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
