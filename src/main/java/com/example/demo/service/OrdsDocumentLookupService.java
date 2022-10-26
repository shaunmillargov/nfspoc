package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.controller.SSEController;
import com.example.demo.model.GetFileResponse;
import com.example.demo.model.InitializeRequest;
import com.example.demo.model.InitializeResponse;
import com.example.demo.model.Job;

@Service
public class OrdsDocumentLookupService {

	private static final Logger logger = LoggerFactory.getLogger(OrdsDocumentLookupService.class);

	private final RestTemplate restTemplate;
	
	@Autowired
	private SSEController sse; 
	
	@Value("${poc.ords.endpoint:unknown}")
	private String ordsEndpoint; 
	
	@Value("${poc.app.id:id}")
	private String appId;
	
	@Value("${poc.app.pwd:password}")
	private String appPwd;
	
	@Value("${poc.app.ticketlifetime:unknown}")
	private String ticLifeTime;

	public OrdsDocumentLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	/**
	 * 
	 * Call to PUT document on NFS
	 * 
	 * @param documentGuid
	 * @param appTicket
	 * @return
	 */
	private CompletableFuture<ResponseEntity<GetFileResponse>> getFilePOC(Job job, String appTicket) {
		
		logger.info("Calling getDocPOC for threadId: " + job.getThreadId());
		
		String getEndpoint = ordsEndpoint + "/getFilePoc?AppTicket=%s" + 
											"&ObjectGuid=%s" + 
											"&TicketLifeTime=%s" + 
											"&PutId=SCVPOC";
		
		// The base64 document guid has to be additionally HTML escaped as it's sent as a param to a RESTful ORDS operation.  
		String htmlEscapedBase64Guid = null; 
		try {
			htmlEscapedBase64Guid = encodeValue(job.getDocGuid());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		getEndpoint = String.format(getEndpoint, appTicket, htmlEscapedBase64Guid, this.ticLifeTime);
		
		ResponseEntity<GetFileResponse> results = restTemplate.exchange(getEndpoint, HttpMethod.GET, null, GetFileResponse.class);		
	    
		return CompletableFuture.completedFuture(results);
	}
	
	/**
	 * Utility function to apply url encoding to a string. 
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String encodeValue(String value) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}
	
	/**
	 * 
	 * Initializes the next POC putFile call.  
	 * 
	 * @return
	 */
	private CompletableFuture<ResponseEntity<InitializeResponse>> initializeNFSDocument(Job job) throws InterruptedException {
		
		logger.info("Calling initialize for threadId: " + job.getThreadId());
		
		ResponseEntity<InitializeResponse> resp = null;
		try {
			
			HttpEntity<InitializeRequest> body = new HttpEntity<InitializeRequest>(new InitializeRequest(appId, appPwd, ticLifeTime));
			
			resp = restTemplate.exchange(ordsEndpoint + "/initialize",
										HttpMethod.POST,
										body,
										InitializeResponse.class);
			
			logger.info("Http Response from initialize call for threadId: " + job.getThreadId() + " was " + resp.getStatusCodeValue());
			return CompletableFuture.completedFuture(resp);
		
		} catch (HttpStatusCodeException ex) {
			logger.error("Initialize call for threadId: " + job.getThreadId() + " resulted in an HTTPStatus code response of " + ex.getRawStatusCode());
			throw ex;
		}

	}

	@Async
	public void SendOrdsGetDocumentRequests(List<Job> jobs) throws URISyntaxException {
		
		String appTicket = null; 
		
		// Calls initialize first for the upcoming document request, async. 
		for(Job job: jobs) {
			try {
				
				job.setStarttime(System.currentTimeMillis());
				CompletableFuture<ResponseEntity<InitializeResponse>> future = this.initializeNFSDocument(job);
				future.get(); // wait for the thread to complete.
				appTicket = future.get().getBody().getAppTicket();
				job.setPercentageComplete("50");
				job.setEndInitTime(System.currentTimeMillis());
				DispatchOrdsResponse(job); // dispatch first half of request (init). 
				
				try { 
					CompletableFuture<ResponseEntity<GetFileResponse>> future2 = this.getFilePOC(job, appTicket);
					ResponseEntity<GetFileResponse> _resp = future2.get();
					job.setEndGetDocTIme(System.currentTimeMillis());
					job.setFileName(_resp.getBody().getFilename());
					job.setMimeType(_resp.getBody().getMimeType());
					logger.info("File name returned for thread Id: " + job.getThreadId() + " was " + _resp.getBody().getFilename());
					job.setPercentageComplete("100");
	
				} catch (Exception e) {	
					job.setEndGetDocTIme(System.currentTimeMillis());
					job.setError(true); // triggers error progress indicator bar.
					job.setPercentageComplete("100");
					String msg = "Error received when sending get document request for thread id " + job.getThreadId() + ". Error: " + e.getMessage();
					job.setErrorMessage(msg);
					logger.error(msg);
					e.printStackTrace();
				}
				
			} catch (Exception e) {	
				job.setEndInitTime(System.currentTimeMillis());
				job.setError(true); // triggers error progress indicator bar.
				job.setPercentageComplete("100");
				String msg = "Error received when sending initialize for thread id " + job.getThreadId() + ". Error: " + e.getMessage();
				job.setErrorMessage(msg);
				logger.error(msg);
				e.printStackTrace();
			}
			
			DispatchOrdsResponse(job); // dispatch second half of request (getPOCFile).
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
