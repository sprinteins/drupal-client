package com.sprinteins.drupalcli;

public class DescriptionModel {

	private String value;
	private ValueFormat format;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ValueFormat getFormat() {
		return format;
	}

	public void setFormat(ValueFormat format) {
		this.format = format;
	}

}
