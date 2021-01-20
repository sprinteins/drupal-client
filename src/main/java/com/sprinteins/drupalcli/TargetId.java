package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TargetId {

	GET_STARTED_ELEMENT;

	@JsonValue
	public String toLowerCase() {
		return this.toString().toLowerCase();
	}

}
