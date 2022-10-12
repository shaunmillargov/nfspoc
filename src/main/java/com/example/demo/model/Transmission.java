package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "threadId", "progress", "error", "duration" })
public class Transmission {

	@JsonProperty("id")
	private String threadId;
	
	@JsonProperty("progress")
	private String progress;
	
	@JsonProperty("error")
	private boolean error = false;
	
	@JsonProperty("duration")
	private String duration;

	@JsonProperty("threadId")
	public String getThreadId() {
		return threadId;
	}

	@JsonProperty("threadId")
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	@JsonProperty("progress")
	public String getProgress() {
		return progress;
	}

	@JsonProperty("progress")
	public void setProgress(String progress) {
		this.progress = progress;
	}

	@JsonProperty("error")
	public boolean getError() {
		return error;
	}

	@JsonProperty("error")
	public void setError(boolean error) {
		this.error = error;
	}

	@JsonProperty("duration")
	public String getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(String duration) {
		this.duration = duration;
	}

}
