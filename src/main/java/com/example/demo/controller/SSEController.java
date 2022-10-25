package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.model.Job;
import com.example.demo.model.Transmission;


/**
 * 
 * Server Sent Event (SSE) Controller.
 * 
 * 
 * @author 176899
 *
 */
@RestController
public class SSEController {

	public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	// Method for client subscriptions
	@CrossOrigin
	@RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
	public SseEmitter subscribe() {

		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		try {
			sseEmitter.send(SseEmitter.event().name("INIT"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));

		// Store connection with clients.
		emitters.add(sseEmitter);

		return sseEmitter;

	}
	
	/**
	 * 
	 * dispatch events to client via injection to controller (or called from RESTful end point above for testing purposes). 
	 * 
	 * @param threadId
	 * @param progress
	 * @param error
	 */
	public void sendEvent(Job job) {
		for (SseEmitter emitter : emitters) {
			try {
				// create the transmitted object.
				Transmission transmission = new Transmission();
				transmission.setThreadId(job.getThreadId());
				transmission.setProgress(job.getPercentageComplete());
				transmission.setError(job.isError());
				transmission.setFileName(job.getFileName());
				transmission.setMimeType(job.getMimeType());
				transmission.setDuration(job.getDurations()); 
				
				emitter.send(SseEmitter.event().name("updates").data(transmission));
			} catch (IOException e) {
				
				// removes emitter gracefully when client is no longer connected. 
				emitters.remove(emitter);
			}
		}
	}
}
