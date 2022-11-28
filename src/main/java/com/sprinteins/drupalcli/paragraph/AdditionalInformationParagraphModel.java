package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;

import java.util.List;

public class AdditionalInformationParagraphModel extends ParagraphModel {

    public static AdditionalInformationParagraphModel create(String title, FormattedTextModel description) {
        AdditionalInformationParagraphModel model = new AdditionalInformationParagraphModel();
        model.getOrCreateFirstTitle().setValue(title);
        model.setDescription(List.of(description));
        return model;
    }

    public AdditionalInformationParagraphModel() {
        super(new TypeModel(TargetId.ADDITIONAL_INFO_ELEMENT));
    }

    @Override
    @JsonProperty("field_p_additional_info_title")
    public List<StringValueModel> getTitle() {
        return super.getTitle();
    }

    @Override
    @JsonProperty("field_p_additional_info_desc")
    public List<FormattedTextModel> getDescription() {
        return super.getDescription();
    }

}
