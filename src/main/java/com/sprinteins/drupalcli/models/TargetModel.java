package com.sprinteins.drupalcli.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetModel {

    private Long targetId;
    private String targetType;
    private String targetUuid;

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

    @Override
    public String toString() {
        return "TargetModel {targetId=" + targetId + ", targetType=" + targetType + ", targetUuid=" + targetUuid + "}";
    }

}
