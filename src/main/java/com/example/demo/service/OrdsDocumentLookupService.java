package com.example.demo.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.controller.SSEController;
import com.example.demo.model.Job;
import com.example.demo.model.OrdsResponse;

@Service
public class OrdsDocumentLookupService {

	private static final Logger logger = LoggerFactory.getLogger(OrdsDocumentLookupService.class);

	private final RestTemplate restTemplate;
	
	@Autowired
	private SSEController sse; 
	
	@Value("${ords.endpoint:unknown}")
	private String ordsEndpoint; 

	public OrdsDocumentLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	private CompletableFuture<ResponseEntity<OrdsResponse>> setNFSDocument(String documentId) throws InterruptedException {
		logger.debug("Heard call to setNFSDocument with documentId, " + documentId);
		ResponseEntity<OrdsResponse> results = restTemplate.exchange(ordsEndpoint, HttpMethod.GET, null, OrdsResponse.class, documentId);
		return CompletableFuture.completedFuture(results);
	}
	
	@Async
	public void SendOrdsGetDocumentRequests(List<Job> jobs) throws URISyntaxException {
		
		// Calls ORDS fetch doc for each job, async. 
		for(Job job: jobs) {
			try {
				job.setStarttime(System.currentTimeMillis());
				CompletableFuture<ResponseEntity<OrdsResponse>> future = this.setNFSDocument(job.getId());
				future.get(); // wait for the thread to complete.
				job.setPercentageComplete("50");
			} catch (Exception e) {	
				job.setError(true); // triggers error progress indicator bar.
				job.setPercentageComplete("100");
				logger.error("Error sending Get Document request for thread id " + job.getThreadId());
				e.printStackTrace();
			}
			job.setDuration(System.currentTimeMillis()); // sets the duration (ms) to this point in the process
			DispatchOrdsResponse(job);
		}
	}
	
	/**
	 * 
	 * Dispatch Good response to front page from call to request NFS document. (Green progress bar) 
	 * 
	 * @param threadId
	 * @throws URISyntaxException
	 */
	private void DispatchOrdsResponse(Job job) throws URISyntaxException {
		sse.sendEvent(job);
	}

}
