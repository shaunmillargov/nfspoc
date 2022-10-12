package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

//Form backing object. 
//Contains chosen docs list and individual thread data. 
public class FormBackingObject {
	
	private String documentIds; // chosen documentId list. 
	private List<Job> jobs = new ArrayList<Job>(); // maintains thread id, label, documentId for each job.
	private boolean testing; // indicates test cycle has begun. 
	private String errors = null; 

	public String getDocumentIds() {
		return documentIds;
	}

	public void setDocumentIds(String documentIds) {
		this.documentIds = documentIds;
	}

	public boolean isTesting() {
		return testing;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

}
