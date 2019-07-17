/**
 * 
 */
package com.docusign.batch.domain;

/**
 * @author Amit.Bist
 *
 */
//envelopeId, envelopeStatus, voidedReason, emailSubject, emailBlurb, purgeState, resendEnvelope
public class EnvelopeUpdateDetails extends AbstractEnvelopeItem {

	private String envelopeStatus;

	private String voidedReason;

	private String emailSubject;

	private String emailBlurb;

	private String purgeState;

	private String resendEnvelope;

	private String reminderEnabled;

	private String reminderDelay;

	private String reminderFrequency;

	private String expireEnabled;

	private String expireAfter;

	private String expireWarn;

	public String getEnvelopeStatus() {
		return envelopeStatus;
	}

	public void setEnvelopeStatus(String envelopeStatus) {
		this.envelopeStatus = envelopeStatus;
	}

	public String getVoidedReason() {
		return voidedReason;
	}

	public void setVoidedReason(String voidedReason) {
		this.voidedReason = voidedReason;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBlurb() {
		return emailBlurb;
	}

	public void setEmailBlurb(String emailBlurb) {
		this.emailBlurb = emailBlurb;
	}

	public String getPurgeState() {
		return purgeState;
	}

	public void setPurgeState(String purgeState) {
		this.purgeState = purgeState;
	}

	public String getResendEnvelope() {
		return resendEnvelope;
	}

	public void setResendEnvelope(String resendEnvelope) {
		this.resendEnvelope = resendEnvelope;
	}

	public String getReminderEnabled() {
		return reminderEnabled;
	}

	public void setReminderEnabled(String reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}
	
	public String getReminderDelay() {
		return reminderDelay;
	}

	public void setReminderDelay(String reminderDelay) {
		this.reminderDelay = reminderDelay;
	}

	public String getReminderFrequency() {
		return reminderFrequency;
	}

	public void setReminderFrequency(String reminderFrequency) {
		this.reminderFrequency = reminderFrequency;
	}

	public String getExpireEnabled() {
		return expireEnabled;
	}

	public void setExpireEnabled(String expireEnabled) {
		this.expireEnabled = expireEnabled;
	}

	public String getExpireAfter() {
		return expireAfter;
	}

	public void setExpireAfter(String expireAfter) {
		this.expireAfter = expireAfter;
	}

	public String getExpireWarn() {
		return expireWarn;
	}

	public void setExpireWarn(String expireWarn) {
		this.expireWarn = expireWarn;
	}
}