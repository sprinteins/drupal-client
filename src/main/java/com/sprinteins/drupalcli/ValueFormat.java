package com.sprinteins.drupalcli;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValueFormat {

	GITHUB_FLAVORED_MARKDOWN, BASIC_HTML;

	@JsonValue
	public String toLowerCase() {
		return this.toString().toLowerCase(Locale.ROOT);
	}

}
