package com.example.demo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.model.Document;
import com.example.demo.model.Documents;
import com.example.demo.model.FormBackingObject;
import com.example.demo.model.Job;
import com.example.demo.service.OrdsDocumentLookupService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Page controller.
 * 
 * @author 176899
 *
 */
@Controller
@RequestMapping("/demo")
public class PageController {
	
	private static final Logger logger = LoggerFactory.getLogger(PageController.class);
	
	@Autowired
	private OrdsDocumentLookupService ordsDocumentLookupService;

	private Map<String, String> documentMap;

	@GetMapping("/start")
	public String startView(Model model) {

		FormBackingObject fbo = new FormBackingObject();

		model.addAttribute("documentOptions", this.documentMap);
		model.addAttribute("fbo", fbo);

		return "sseIndex";
	}

	@PostConstruct
	private void postConstruct() throws IOException {

		Documents documents = loadDocsFromJSON();

		documentMap = new HashMap<>();
		for (Document document : documents.getDocuments()) {
			documentMap.put(document.getB64Guid(), document.getName());
		}
	}

	/**
	 * Read JSON documents file.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Documents loadDocsFromJSON() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		InputStream fileStream = new ClassPathResource("documents.json").getInputStream();
		Documents documents = mapper.readValue(fileStream, Documents.class);
		fileStream.close();
		return documents;
	}

	/**
	 * Called when user selects document id values and submits. 
	 * 
	 * @param fbo
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/commenceDemo", params = "submit", method = RequestMethod.POST)
	public String submitForm(@ModelAttribute("fbo") FormBackingObject fbo, HttpServletRequest request) throws IOException, URISyntaxException {

		logger.info("Starting test. Test doc guid(s): " + fbo.getDocumentGuids());

		if (null == fbo.getDocumentGuids()) {
			fbo.setErrors("One or more document Ids must be selected");
			request.getSession().setAttribute("documentOptions", this.documentMap);
			return "sseIndex";
		}

		// create the jobs objects within the FBO and add to the session
		fbo = createJobs(fbo);
		request.getSession().setAttribute("documentOptions", this.documentMap);

		fbo.setTesting(true);
		request.getSession().setAttribute("fbo", fbo);
		
		ordsDocumentLookupService.SendOrdsGetDocumentRequests(fbo.getJobs());
		
		return "sseIndex";
	}

	/**
	 * 
	 * Utility function that creates Jobs from the selected document ids. 
	 * 
	 * @param fbo
	 * @return
	 * @throws IOException
	 */
	private FormBackingObject createJobs(FormBackingObject fbo) throws IOException {
		List<Job> jobs = new ArrayList<Job>();

		List<String> documentsList = Arrays.asList(fbo.getDocumentGuids().split(",", -1));
		for (String guid : documentsList) {
			Job aJob = new Job();
			aJob.setDocGuid(guid);
			aJob.setThreadId(getThreadId());
			aJob.setLabel(getThreadLabel(guid));
			jobs.add(aJob);
		}

		fbo.setJobs(jobs);

		return fbo;
	}

	/**
	 * Returns thread label based on docId in json list.
	 * 
	 * @param d
	 * @return
	 * @throws IOException 
	 */
	private String getThreadLabel(String d) throws IOException {
		Documents documents = loadDocsFromJSON();
		for (Document document : documents.getDocuments()) {
			if (document.getB64Guid().equals(d)) {
				return document.getName();
			}
		}
		return null;
	}

	/**
	 * Randomized (short) thread id.
	 * 
	 * @return
	 */
	private String getThreadId() {

		final int SHORT_ID_LENGTH = 8;
		
		// HEX: 0-9, a-f. For example: 6587fddb, c0f182c1
		return RandomStringUtils.random(SHORT_ID_LENGTH, "0123456789abcdef"); 
	}

	/**
	 * 
	 * Project reset. Called when the 'reset' button is clicked. 
	 * 
	 * @param fbo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/commenceDemo", params = "reset", method = RequestMethod.POST)
	public String resetForm(@ModelAttribute("fbo") FormBackingObject fbo, HttpServletRequest request) {

		System.out.println("Reseting form");

		fbo.setTesting(false);
		fbo.setDocumentGuids(null);

		request.getSession().setAttribute("documentOptions", this.documentMap);
		request.getSession().setAttribute("fbo", fbo);

		return "sseIndex";
	}

}
