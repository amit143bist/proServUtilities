/**
 * 
 */
package com.docusign.batch.processor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.batch.domain.EnvelopeDetails;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.domain.DSErrors;
import com.docusign.proserv.application.domain.Expirations;
import com.docusign.proserv.application.domain.Notification;
import com.docusign.proserv.application.domain.Reminders;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Amit.Bist
 *
 */
public class DSAPIProcessor implements ItemProcessor<EnvelopeDetails, EnvelopeDetails> {

	final static Logger logger = Logger.getLogger(DSAPIProcessor.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public PSProperties psProps;

	@Autowired
	private ProServServiceTemplate proServServiceTemplate;

	@Autowired
	CacheManager cacheManager;

	@Autowired
	AppParameters appParameters;

	private String baseUri;

	private String accountGuid;

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getAccountGuid() {
		return accountGuid;
	}

	public void setAccountGuid(String accountGuid) {
		this.accountGuid = accountGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public EnvelopeDetails process(EnvelopeDetails envDetails) throws Exception {

		Notification notification = createRequest(envDetails);

		objectMapper.setSerializationInclusion(Include.NON_NULL);

		String msgBody = objectMapper.writeValueAsString(notification);

		AccessToken accessToken = cacheManager.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> uri = new HttpEntity<String>(msgBody, headers);
		try {

			String url = MessageFormat.format(psProps.getEnvelopesNotificationApi(), baseUri, accountGuid,
					envDetails.getEnvelopeId());

			logger.debug("url in DSNotificationProcessor.process() " + url);

			ResponseEntity<String> responseEntity = proServServiceTemplate.getRestTemplate(appParameters).exchange(url,
					HttpMethod.PUT, uri, String.class);

			logger.info("Notification updated successfully for " + envDetails.getEnvelopeId());
			HttpHeaders responseHeaders = responseEntity.getHeaders();
			processResponseHeaders(envDetails, responseHeaders, "SuccessCall");
			envDetails.setSuccess(true);
			envDetails.setTransMessage(AppConstants.TRANS_SUCCESS_MSG);
		} catch (Exception exp) {

			exp.printStackTrace();

			DSErrors error = null;
			String transMessage = null;
			if (exp instanceof HttpClientErrorException) {

				HttpClientErrorException clientExp = (HttpClientErrorException) exp;

				logger.error(
						"HttpClientErrorException DSAPIProcessor.process()- " + clientExp.getResponseBodyAsString());

				HttpHeaders responseHeaders = clientExp.getResponseHeaders();
				processResponseHeaders(envDetails, responseHeaders, "HttpClientErrorException");

				if (clientExp.getResponseBodyAsString().contains("errorCode")) {

					error = objectMapper.readValue(clientExp.getResponseBodyAsString(), DSErrors.class);
				}
			}

			logger.error("Exception in DSAPIProcessor.process()- " + exp.getMessage() + " to process "
					+ envDetails.getEnvelopeId());

			transMessage = exp.getMessage();
			if (null != error) {
				transMessage = error.getErrorCode() + "_" + error.getMessage();
			}

			envDetails.setSuccess(false);
			envDetails.setTransMessage(transMessage);
			logger.info("Transaction failed to set in DSAPIProcessor.process() for " + envDetails.getEnvelopeId());
		}

		return envDetails;
	}

	/**
	 * @param envDetails
	 * @param responseEntity
	 */
	private void processResponseHeaders(EnvelopeDetails envDetails, HttpHeaders responseHeaders, String callingCode) {

		Set<Entry<String, List<String>>> headerEntrySet = responseHeaders.entrySet();
		for (Entry<String, List<String>> headerEntry : headerEntrySet) {
			logger.debug("In " + callingCode + " DSAPIProcessor.processResponseHeaders() HeaderKey- "
					+ headerEntry.getKey() + " Value- " + headerEntry.getValue());

			switch (headerEntry.getKey()) {

			case AppConstants.DS_HEADER_X_RATELIMIT_RESET:
				envDetails.setRateLimitReset(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_RATELIMIT_LIMIT:
				envDetails.setRateLimitLimit(headerEntry.getValue().get(0));
				break;
			case AppConstants.DS_HEADER_X_RATELIMIT_REMAINING:
				envDetails.setRateLimitRemaining(headerEntry.getValue().get(0));
				break;
			default:
			}
		}
	}

	/**
	 * @param envDetails
	 * @return
	 */
	private Notification createRequest(EnvelopeDetails envDetails) {

		Notification notification = new Notification();

		if (null != envDetails.getReminderEnabled()) {
			Reminders reminders = new Reminders();
			reminders.setReminderDelay(String.valueOf(envDetails.getReminderDelay()));
			reminders.setReminderEnabled(String.valueOf(envDetails.getReminderEnabled()));
			reminders.setReminderFrequency(String.valueOf(envDetails.getReminderFrequency()));
			notification.setReminders(reminders);
		}

		if (null != envDetails.getExpireEnabled()) {
			Expirations expirations = new Expirations();
			expirations.setExpireAfter(String.valueOf(envDetails.getExpireAfter()));
			expirations.setExpireEnabled(String.valueOf(envDetails.getExpireEnabled()));
			expirations.setExpireWarn(String.valueOf(envDetails.getExpireWarn()));
			notification.setExpirations(expirations);
		}

		return notification;
	}
}