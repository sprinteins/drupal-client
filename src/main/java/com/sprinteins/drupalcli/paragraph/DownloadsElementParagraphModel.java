package com.sprinteins.drupalcli.paragraph;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.SourceFileModel;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;

public class DownloadsElementParagraphModel extends ParagraphModel {

  private List<SourceFileModel> downloadableFiles;

/*   public static DownloadsElementParagraphModel create(String file) {
    DownloadsElementParagraphModel model = new DownloadsElementParagraphModel();
    model.getOrCreateFirstDownloadElement().setValue(file);

    return model;
} */

  public DownloadsElementParagraphModel() {
    super(new TypeModel(TargetId.DOWNLOAD_ELEMENT));
  }

  @JsonProperty("field_file_to_upload")
  public List<SourceFileModel> getDownloadFiles() {
    return Optional.ofNullable(downloadableFiles).map(List::copyOf).orElse(null);
  }
}
