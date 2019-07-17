package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "voidedReason", "emailSubject", "emailBlurb", "purgeState", "notification" })
public class EnvelopeUpdateRequest {

	@JsonProperty("status")
	private String status;
	@JsonProperty("voidedReason")
	private String voidedReason;
	@JsonProperty("emailSubject")
	private String emailSubject;
	@JsonProperty("emailBlurb")
	private String emailBlurb;
	@JsonProperty("purgeState")
	private String purgeState;
	@JsonProperty("notification")
	private Notification notification;

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("voidedReason")
	public String getVoidedReason() {
		return voidedReason;
	}

	@JsonProperty("voidedReason")
	public void setVoidedReason(String voidedReason) {
		this.voidedReason = voidedReason;
	}

	@JsonProperty("emailSubject")
	public String getEmailSubject() {
		return emailSubject;
	}

	@JsonProperty("emailSubject")
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	@JsonProperty("emailBlurb")
	public String getEmailBlurb() {
		return emailBlurb;
	}

	@JsonProperty("emailBlurb")
	public void setEmailBlurb(String emailBlurb) {
		this.emailBlurb = emailBlurb;
	}

	@JsonProperty("purgeState")
	public String getPurgeState() {
		return purgeState;
	}

	@JsonProperty("purgeState")
	public void setPurgeState(String purgeState) {
		this.purgeState = purgeState;
	}
	
	@JsonProperty("notification")
	public Notification getNotification() {
	return notification;
	}

	@JsonProperty("notification")
	public void setNotification(Notification notification) {
	this.notification = notification;
	}

}