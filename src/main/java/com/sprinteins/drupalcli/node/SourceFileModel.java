package com.sprinteins.drupalcli.node;

import com.sprinteins.drupalcli.models.TargetModel;

public class SourceFileModel extends TargetModel {

    private Boolean display = Boolean.TRUE;
    private String description = "";

    public SourceFileModel() {
        super();
        this.setTargetType("file");
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

    @Override
    public String toString() {
        return "SourceFileModel {display=" + display + ", description=" + description + ", targetId=" + getTargetId()
                + ", targetType=" + getTargetType() + ", targetUuid=" + getTargetUuid() + "}";
    }

}
