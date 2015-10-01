package com.tt.simplehttpproxy;

public class Parameter {

	private String name;
	
	private String value;
	
	public Parameter() {
	}

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
