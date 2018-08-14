/**
 * 
 */
package com.docusign.batch.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ItemProcessListener;

import com.docusign.batch.domain.EnvelopeDetails;
import com.docusign.proserv.application.utils.PSUtils;

/**
 * @author Amit.Bist
 *
 */
public class CustomItemProcessListener implements ItemProcessListener<EnvelopeDetails, EnvelopeDetails> {

	final static Logger logger = Logger.getLogger(CustomItemProcessListener.class);

	@Override
	public void beforeProcess(EnvelopeDetails item) {

	}

	@Override
	public void afterProcess(EnvelopeDetails item, EnvelopeDetails result) {

		if (!StringUtils.isEmpty(item.getRateLimitReset())) {

			long longEpochTime = Long.parseLong(item.getRateLimitReset());

			Date resetDate = new Date(longEpochTime * 1000);
			logger.debug("ResetTime for resetDate " + resetDate);

			long sleepMillis = PSUtils.getDateDiff(Calendar.getInstance().getTime(), resetDate, TimeUnit.MILLISECONDS);

			logger.info(
					"Checking if need to send thread to sleep in CustomItemProcessListener or not, with RateLimitRemaining- "
							+ item.getRateLimitRemaining() + " RateLimitResetValue " + item.getRateLimitReset()
							+ " Float RateLimitRemaining " + Float.parseFloat(item.getRateLimitRemaining())
							+ " Float RateLimitLimit " + Float.parseFloat(item.getRateLimitLimit())
							+ " 0.02 of Float RateLimitLimit " + Float.parseFloat(item.getRateLimitLimit()) * 0.02);

			if ((Float.parseFloat(item.getRateLimitRemaining()) < (Float.parseFloat(item.getRateLimitLimit()) * 0.02))
					&& sleepMillis > 0) {

				logger.info("Sending thread to sleep in CustomItemProcessListener.afterProcess() for " + sleepMillis
						+ " milliseconds, and will wake up at " + resetDate);
				try {
					Thread.sleep(sleepMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onProcessError(EnvelopeDetails item, Exception e) {

	}

}