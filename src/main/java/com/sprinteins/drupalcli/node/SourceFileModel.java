package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceFileModel {

    private Long targetId;
    private Boolean display = Boolean.TRUE;
    private String description = "";
    private String targetType = "file";

    @JsonProperty("target_id")
    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("target_type")
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

}
