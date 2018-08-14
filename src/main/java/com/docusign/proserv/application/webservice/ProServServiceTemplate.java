package com.docusign.proserv.application.webservice;

import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

import com.docusign.batch.domain.AppParameters;
import com.docusign.proserv.application.utils.PSUtils;

public class ProServServiceTemplate {

	final static Logger logger = Logger.getLogger(ProServServiceTemplate.class);

	private RestTemplate restTemplate = null;

	public RestTemplate getRestTemplate(AppParameters appParameters) {

		if (null == restTemplate) {

			restTemplate = PSUtils.initiateRestTemplate(appParameters.getProxyHost(),
					appParameters.getProxyPort());
		}

		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
}