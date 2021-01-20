package com.sprinteins.drupalcli;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class GetStartedParagraphModel {

	private List<FieldDescriptionModel> description;
	private List<FieldTitleModel> title;

	@JsonIgnore
	public FieldDescriptionModel getOrCreateFirstDescription() {
		if (description == null) {
			description = new ArrayList<>();
		}
		if (description.isEmpty()) {
			description.add(new FieldDescriptionModel());
		}
		return description.get(0);
	}

	@JsonIgnore
	public FieldTitleModel getOrCreateFirstTitle() {
		if (title == null) {
			title = new ArrayList<>();
		}
		if (title.isEmpty()) {
			title.add(new FieldTitleModel());
		}
		return title.get(0);
	}

	@JsonProperty("field_p_getstarted_description")
	public List<FieldDescriptionModel> getDescription() {
		return description;
	}

	public void setDescription(List<FieldDescriptionModel> description) {
		this.description = description;
	}

	@JsonProperty("field_p_getstarted_title")
	public List<FieldTitleModel> getTitle() {
		return title;
	}

	public void setTitle(List<FieldTitleModel> title) {
		this.title = title;
	}

}
