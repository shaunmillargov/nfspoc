package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "threadId", "progress", "error", "duration", "fileUrl", "mimeType", "duration", "errorMessage" })
public class Transmission {

	@JsonProperty("threadId")
	private String threadId;
	
	@JsonProperty("progress")
	private String progress;
	
	@JsonProperty("error")
	private boolean error = false;
	
	@JsonProperty("duration")
	private String duration;
	
	@JsonProperty("fileUrl")
	private String fileUrl; 
	
	@JsonProperty("mimeType")
	private String mimeType; 
	
	@JsonProperty("errorMessage")
	private String errorMessage; 

	@JsonProperty("fileUrl")
	public String getFileUrl() {
		return fileUrl;
	}

	@JsonProperty("fileUrl")
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@JsonProperty("mimeType")
	public String getMimeType() {
		return mimeType;
	}

	@JsonProperty("mimeType")
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

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

	@JsonProperty("errorMessage")
	public String getErrorMessage() {
		return errorMessage;
	}

	@JsonProperty("errorMessage")
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
