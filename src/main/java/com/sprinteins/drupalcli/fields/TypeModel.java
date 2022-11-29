package com.sprinteins.drupalcli.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fieldtypes.TargetId;
import com.sprinteins.drupalcli.fieldtypes.TargetType;

public class TypeModel {

    private TargetId targetId;
    private TargetType targetType;
    private String targetUuid;
    private String href;

    public TypeModel() {
    }

    public TypeModel(TargetId targetId) {
        this.targetId = targetId;
    }

    @JsonProperty("href")
    public String getHrefhref() { return href; }
    public void setHref(String href) { this.href = href; }

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

    @Override
    public String toString() {
        return "TypeModel {targetId=" + targetId + "}";
    }

}
