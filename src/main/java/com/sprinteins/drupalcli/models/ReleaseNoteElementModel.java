package com.sprinteins.drupalcli.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseNoteElementModel {

    private Long targetId;
    private Long targetRevisionId;
    private TargetType targetType = TargetType.PARAGRAPH;
    private String targetUuid;

    @JsonProperty("target_id")
    public Long getTargetId() {
        return targetId;
    }
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    @JsonProperty("target_revision_id")
    public Long getTargetRevisionId() {
        return targetRevisionId;
    }
    public void setTargetRevisionId(Long targetRevisionId) {
        this.targetRevisionId = targetRevisionId;
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
