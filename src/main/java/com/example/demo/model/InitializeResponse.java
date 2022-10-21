package com.example.demo.model;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "AppTicket" })
@Generated("jsonschema2pojo")
public class InitializeResponse {

	@JsonProperty("AppTicket")
	private String appTicket;

	@JsonProperty("AppTicket")
	public String getAppTicket() {
		return appTicket;
	}

	@JsonProperty("AppTicket")
	public void setAppTicket(String appTicket) {
		this.appTicket = appTicket;
	}

}