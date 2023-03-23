package com.sprinteins.drupalcli.fields;

public class SourceFileModel extends TargetModel {

    private Boolean display = Boolean.TRUE;
    private String description = "";
    private String url = "  ";

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

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
        return "SourceFileModel {display=" + display + ", description=" + description + ", targetId=" + getTargetId()
                + ", targetType=" + getTargetType() + ", targetUuid=" + getTargetUuid() + ", url=" + getUrl() + "}";
    }

}
