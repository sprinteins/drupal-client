package com.sprinteins.drupalcli.paragraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.SourceFileModel;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;
import com.sprinteins.drupalcli.file.FileUploadModel;

public class DownloadsElementParagraphModel extends ParagraphModel {

  private List<SourceFileModel> downloadableFiles;


  public DownloadsElementParagraphModel() {
    super(new TypeModel(TargetId.DOWNLOADS));
  }

  public static DownloadsElementParagraphModel create(FileUploadModel downloadFile) {
    DownloadsElementParagraphModel model = new DownloadsElementParagraphModel();
    model.getOrCreateFirstDownloadFile();
    return model;
  }

  @JsonIgnore
  public SourceFileModel getOrCreateFirstDownloadFile() {
      if (downloadableFiles == null) {
          downloadableFiles = new ArrayList<>();
      }
      if (downloadableFiles.isEmpty()) {
          downloadableFiles.add(new SourceFileModel());
      }
      return downloadableFiles.get(0);
  }

  @JsonProperty("field_file_to_upload")
  public List<SourceFileModel> getDownloadFiles() {
    return Optional.ofNullable(downloadableFiles).map(List::copyOf).orElse(null);
  }

  public void setDownloadFiles(List<SourceFileModel>downloadableFiles) {
    this.downloadableFiles = Optional.ofNullable(downloadableFiles).map(List::copyOf).orElse(null);
  }
}
