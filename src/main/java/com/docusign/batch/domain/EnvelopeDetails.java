/**
 * 
 */
package com.docusign.batch.domain;

import java.math.BigInteger;

/**
 * @author Amit.Bist
 *
 */
// envelopeId,reminderEnabled,reminderDelay,reminderFrequency,expireEnabled,expireAfter,expireWarn
public class EnvelopeDetails {

	private String envelopeId;

	private Boolean reminderEnabled;

	private BigInteger reminderDelay;

	private BigInteger reminderFrequency;

	private Boolean expireEnabled;

	private BigInteger expireAfter;

	private BigInteger expireWarn;

	private Boolean success;

	private String transMessage;

	private String rateLimitReset;

	private String rateLimitLimit;

	private String rateLimitRemaining;

	public String getEnvelopeId() {
		return envelopeId;
	}

	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}

	public Boolean getReminderEnabled() {
		return reminderEnabled;
	}

	public void setReminderEnabled(Boolean reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}

	public BigInteger getReminderDelay() {
		return reminderDelay;
	}

	public void setReminderDelay(BigInteger reminderDelay) {
		this.reminderDelay = reminderDelay;
	}

	public BigInteger getReminderFrequency() {
		return reminderFrequency;
	}

	public void setReminderFrequency(BigInteger reminderFrequency) {
		this.reminderFrequency = reminderFrequency;
	}

	public Boolean getExpireEnabled() {
		return expireEnabled;
	}

	public void setExpireEnabled(Boolean expireEnabled) {
		this.expireEnabled = expireEnabled;
	}

	public BigInteger getExpireAfter() {
		return expireAfter;
	}

	public void setExpireAfter(BigInteger expireAfter) {
		this.expireAfter = expireAfter;
	}

	public BigInteger getExpireWarn() {
		return expireWarn;
	}

	public void setExpireWarn(BigInteger expireWarn) {
		this.expireWarn = expireWarn;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getTransMessage() {
		return transMessage;
	}

	public void setTransMessage(String transMessage) {
		this.transMessage = transMessage;
	}

	public String getRateLimitReset() {
		return rateLimitReset;
	}

	public void setRateLimitReset(String rateLimitReset) {
		this.rateLimitReset = rateLimitReset;
	}

	public String getRateLimitLimit() {
		return rateLimitLimit;
	}

	public void setRateLimitLimit(String rateLimitLimit) {
		this.rateLimitLimit = rateLimitLimit;
	}

	public String getRateLimitRemaining() {
		return rateLimitRemaining;
	}

	public void setRateLimitRemaining(String rateLimitRemaining) {
		this.rateLimitRemaining = rateLimitRemaining;
	}
}