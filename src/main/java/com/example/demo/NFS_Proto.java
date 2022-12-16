package com.example.demo;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableAsync
public class NFS_Proto extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NFS_Proto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NFS_Proto.class);

	public static void main(String[] args) {
		SpringApplication.run(NFS_Proto.class, args);
	}
	
	// This bean is required for aysnc http calls from the OrdsDocumentLookupService. 
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(40);
		executor.setThreadNamePrefix("Ordsthread -");
		executor.initialize();
		logger.info("TaskExecutor set");
		return executor;
	}

}
