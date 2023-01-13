package com.sprinteins.drupalcli.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fieldtypes.TargetType;
import com.sprinteins.drupalcli.paragraph.DownloadsElementParagraphModel;

public class DownloadsModel {
  
  private Long targetId;
  private Long targetRevisionId;
  private TargetType targetType = TargetType.PARAGRAPH;
  private String targetUuid;
  
  public DownloadsModel() {

  }

  public DownloadsModel(DownloadsElementParagraphModel downloadsParagraph) {
    setTargetId(downloadsParagraph.getOrCreateFirstId().getValue());
    setTargetRevisionId(downloadsParagraph.getOrCreateFirstRevisionId().getValue());
    setTargetUuid(downloadsParagraph.getOrCreateFirstUuid().getValue());
  }

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
