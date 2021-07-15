package com.sprinteins.drupalcli.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeModel {

    private TargetId targetId;
    private TargetType targetType;
    private String targetUuid;

    public TypeModel() {
    }

    public TypeModel(TargetId targetId) {
        this.targetId = targetId;
    }

    @JsonProperty("target_id")
    public TargetId getTargetId() {
        return targetId;
    }
    public void setTargetId(TargetId targetId) {
        this.targetId = targetId;
    }

    @JsonProperty("target_type")
    public TargetType getTargetType() {
        return targetType;
    }
    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    @JsonProperty("target_uuid")
    public String getTargetUuid() { return targetUuid; }
    public void setTargetUuid(String targetUuid) { this.targetUuid = targetUuid; }
}
