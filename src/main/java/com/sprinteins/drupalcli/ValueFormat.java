package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueFormat {

	GITHUB_FLAVORED_MARKDOWN, BASIC_HTML;

	@JsonValue
	public String toLowerCase() {
		return this.toString().toLowerCase();
	}

}
