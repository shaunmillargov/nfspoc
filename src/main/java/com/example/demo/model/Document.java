package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "b64Guid" })
public class Document {

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("b64Guid")
	private String b64Guid;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("b64Guid")
	public String getB64Guid() {
		return b64Guid;
	}

	@JsonProperty("b64Guid")
	public void setB64Guid(String b64Guid) {
		this.b64Guid = b64Guid;
	}

}
