/**
 * 
 */
package com.docusign.batch.listener;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.docusign.batch.domain.AbstractEnvelopeItem;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

/**
 * @author Amit.Bist
 *
 */
public class CustomItemProcessListener implements ItemProcessListener<AbstractEnvelopeItem, AbstractEnvelopeItem> {

	final static Logger logger = Logger.getLogger(CustomItemProcessListener.class);

	@Autowired
	private PSProperties psProps;

	@Override
	public void beforeProcess(AbstractEnvelopeItem item) {

		logger.info("Before Process " + item.getEnvelopeId());

	}

	@Override
	public void afterProcess(AbstractEnvelopeItem item, AbstractEnvelopeItem result) {

		boolean threadSleeping = false;

		logger.info("After Process " + item.getEnvelopeId());

		if (null != item && !StringUtils.isEmpty(item.getRateLimitReset())) {

			long longEpochTime = Long.parseLong(item.getRateLimitReset());

			Date resetDate = Date.from(Instant.ofEpochSecond(longEpochTime + 5));
			logger.debug("ResetTime for resetDate " + resetDate);

			long sleepMillis = PSUtils.getDateDiff(Calendar.getInstance().getTime(), resetDate, TimeUnit.MILLISECONDS);

			Float thresholdLimit = Float.parseFloat(item.getRateLimitLimit())
					* Integer.parseInt(psProps.getDSAPIThresholdPercent()) / 100f;

			logger.info(
					"Checking if need to send thread to sleep in CustomItemProcessListener or not, with API Hourly RateLimitRemaining- "
							+ item.getRateLimitRemaining() + " RateLimitResetValue " + item.getRateLimitReset()
							+ " Float RateLimitRemaining " + Float.parseFloat(item.getRateLimitRemaining())
							+ " Float RateLimitLimit " + Float.parseFloat(item.getRateLimitLimit())
							+ " 0.02 of Float RateLimitLimit " + thresholdLimit);

			if ((Float.parseFloat(item.getRateLimitRemaining()) < thresholdLimit) && sleepMillis > 0) {

				sleepThread(resetDate, sleepMillis, "APIHourlyRateLimitThreshold");
				threadSleeping = true;
			}

		}

		if (psProps.isBurstLimitCheckEnabled() && !threadSleeping && null != item
				&& !StringUtils.isEmpty(item.getBurstLimitRemaining())) {

			Float thresholdBurstLimit = Float.parseFloat(item.getBurstLimitLimit())
					* Integer.parseInt(psProps.getDSAPIThresholdPercent()) / 100f;
			logger.info(
					"Checking if need to send thread to sleep in CustomItemProcessListener or not with item.getBurstLimitRemaining() is "
							+ item.getBurstLimitRemaining() + " Float.parseFloat(item.getBurstLimitRemaining()) -> "
							+ Float.parseFloat(item.getBurstLimitRemaining()) + " and item.getBurstLimitLimit() is "
							+ item.getBurstLimitLimit() + " and ThresholdBurstLimit is " + thresholdBurstLimit);

			if (Float.parseFloat(item.getBurstLimitRemaining()) < thresholdBurstLimit) {

				sleepThread(new Date(PSUtils.addSecondsAndconvertToEpochTime(PSUtils.currentTimeInString(), 5) * 1000),
						35000, "BurstRateLimitThreshold");
				threadSleeping = true;
			}

		}

		if (psProps.isTooManyRequestCheckEnabled() && !threadSleeping && null != item
				&& !StringUtils.isEmpty(item.getRateLimitReset())
				&& HttpStatus.TOO_MANY_REQUESTS.value() == item.getHttpStatusCode()) {

			logger.info("Checking with 429 - TOO_MANY_REQUESTS error code, not handled by Burst or API Hourly limit");
			long longEpochTime = Long.parseLong(item.getRateLimitReset());

			Date resetDate = Date.from(Instant.ofEpochSecond(longEpochTime + 5));
			long sleepMillis = PSUtils.getDateDiff(Calendar.getInstance().getTime(), resetDate, TimeUnit.MILLISECONDS);

			sleepThread(resetDate, (sleepMillis + 5000), "429 - TooManyRequests");
			threadSleeping = true;
		}
	}

	public void sleepThread(Date resetDate, long sleepMillis, String sleepReason) {

		logger.info(sleepReason + " reached so sending thread name -> " + Thread.currentThread().getName()
				+ " and threadId -> " + Thread.currentThread().getId() + " to sleep in sleepThread() for " + sleepMillis
				+ " milliseconds, and expected to wake up at " + resetDate);
		try {

			Thread.sleep(sleepMillis);
		} catch (InterruptedException e) {

			logger.error("InterruptedException thrown for thread name- " + Thread.currentThread().getName()
					+ " and threadId- " + Thread.currentThread().getId());
			e.printStackTrace();
		}
	}

	@Override
	public void onProcessError(AbstractEnvelopeItem item, Exception e) {

		logger.info("On Process Error " + item.getEnvelopeId());
	}

}