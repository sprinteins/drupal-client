package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeModel {

	private TargetId targetId;

	public TypeModel() {
	}

	public TypeModel(TargetId targetId) {
		this.targetId = targetId;
	}

	@JsonProperty("target_id")
	public TargetId getTargetId() {
		return targetId;
	}

	public void setTargetId(TargetId targetId) {
		this.targetId = targetId;
	}

}
