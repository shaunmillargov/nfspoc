package com.example.demo.model;

/**
 * Maintains params specific to each job (thread). 
 * 
 * @author 176899
 *
 */
public class Job {
	
	private String docGuid; 
	private String label;
	private String threadId;
	private boolean error; 
	
	private long starttime; 
	private long endtime; 
	private long duration; // current duration ms. 
	private String percentageComplete = "0";
	
	private String fileName; 
	private String mimeType; 
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getDocGuid() {
		return docGuid;
	}
	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public long getEndtime() {
		return endtime;
	}
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration - this.getStarttime();
	}
	public String getPercentageComplete() {
		return percentageComplete;
	}
	public void setPercentageComplete(String percentageComplete) {
		this.percentageComplete = percentageComplete;
	}
	
}
