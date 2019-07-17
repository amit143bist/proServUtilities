package com.docusign.batch.processor;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.docusign.batch.domain.AbstractEnvelopeItem;
import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.proserv.application.domain.DSErrors;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractAPIProcessor {

	final static Logger logger = Logger.getLogger(AbstractAPIProcessor.class);

	/**
	 * @param envelopeItem
	 * @param responseEntity
	 */
	protected void processResponseHeaders(AbstractEnvelopeItem envelopeItem, HttpHeaders responseHeaders,
			String callingCode) {

		Set<Entry<String, List<String>>> headerEntrySet = responseHeaders.entrySet();
		for (Entry<String, List<String>> headerEntry : headerEntrySet) {
			logger.debug("In " + callingCode + " AbstractAPIProcessor.processResponseHeaders() HeaderKey- "
					+ headerEntry.getKey() + " Value- " + headerEntry.getValue());

			switch (headerEntry.getKey()) {

			case AppConstants.DS_HEADER_X_RATELIMIT_RESET:
				envelopeItem.setRateLimitReset(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_RATELIMIT_LIMIT:
				envelopeItem.setRateLimitLimit(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_RATELIMIT_REMAINING:
				envelopeItem.setRateLimitRemaining(headerEntry.getValue().get(0));
				break;
			default:
			}
		}
	}

	/**
	 * @param objectMapper
	 * @param envelopeItem
	 * @param exp
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	protected void handleExceptionData(ObjectMapper objectMapper, AbstractEnvelopeItem envelopeItem, Exception exp)
			throws IOException, JsonParseException, JsonMappingException {
		DSErrors error = null;
		String transMessage = null;
		if (exp instanceof HttpClientErrorException) {

			HttpClientErrorException clientExp = (HttpClientErrorException) exp;

			logger.error(
					"HttpClientErrorException AbstractAPIProcessor.process()- " + clientExp.getResponseBodyAsString());

			HttpHeaders responseHeaders = clientExp.getResponseHeaders();
			processResponseHeaders(envelopeItem, responseHeaders, "HttpClientErrorException");

			if (clientExp.getResponseBodyAsString().contains("errorCode")) {

				error = objectMapper.readValue(clientExp.getResponseBodyAsString(), DSErrors.class);
			}
		}

		logger.error("Exception in AbstractAPIProcessor.process()- " + exp.getMessage() + " to process "
				+ envelopeItem.getEnvelopeId());

		transMessage = exp.getMessage();
		if (null != error) {
			transMessage = error.getErrorCode() + "_" + error.getMessage();
		}

		envelopeItem.setSuccess(false);
		envelopeItem.setTransMessage(transMessage);
		logger.info("Transaction failed to set in AbstractAPIProcessor.process() for " + envelopeItem.getEnvelopeId() + " errorMessage is " + transMessage);
	}

	/**
	 * @param proServServiceTemplate
	 * @param appParameters
	 * @param envelopeItem
	 * @param uri
	 * @param url
	 */
	protected void callDSAPI(ProServServiceTemplate proServServiceTemplate, AppParameters appParameters,
			AbstractEnvelopeItem envelopeItem, HttpEntity<String> uri, String url) {
		
		ResponseEntity<String> responseEntity = proServServiceTemplate.getRestTemplate(appParameters).exchange(url,
				HttpMethod.PUT, uri, String.class);

		logger.info(appParameters.getOperationName() + " completed successfully for " + envelopeItem.getEnvelopeId());
		HttpHeaders responseHeaders = responseEntity.getHeaders();
		processResponseHeaders(envelopeItem, responseHeaders, "SuccessCall");
		envelopeItem.setSuccess(true);
		envelopeItem.setTransMessage(AppConstants.TRANS_SUCCESS_MSG);
	}
}