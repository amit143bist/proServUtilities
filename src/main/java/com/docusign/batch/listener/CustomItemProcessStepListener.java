package com.docusign.batch.listener;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomItemProcessStepListener implements StepExecutionListener {

	final static Logger logger = Logger.getLogger(CustomItemProcessStepListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		logger.info("fileResource afterStep " + stepExecution.getExecutionContext().get("fileResource"));
		logger.info("fileName afterStep " + stepExecution.getExecutionContext().get("fileName"));
		logger.info("summary afterStep " + stepExecution.getSummary());
		logger.info("FailureExceptions afterStep " + stepExecution.getFailureExceptions());
		logger.info("Status afterStep " + stepExecution.getStatus());
		logger.info("ReadCount afterStep " + stepExecution.getReadCount());
		logger.info("WriteCount afterStep " + stepExecution.getWriteCount());

		if (null != stepExecution.getFailureExceptions() && (BatchStatus.FAILED == stepExecution.getStatus()
				|| stepExecution.getReadCount() != stepExecution.getWriteCount())) {

			if (null != stepExecution.getJobExecution().getExecutionContext().get("failureCount")) {

				int failureCount = (int) stepExecution.getJobExecution().getExecutionContext().get("failureCount");
				stepExecution.getJobExecution().getExecutionContext().put("failureCount", failureCount + 1);

			} else {
				stepExecution.getJobExecution().getExecutionContext().put("failureCount", 1);
			}

			if (null != stepExecution.getJobExecution().getExecutionContext().get("failureFileNames")) {

				String failureFileNames = (String) stepExecution.getJobExecution().getExecutionContext()
						.get("failureFileNames");
				stepExecution.getJobExecution().getExecutionContext().put("failureFileNames",
						failureFileNames + "," + stepExecution.getExecutionContext().get("fileName"));
			} else {
				stepExecution.getJobExecution().getExecutionContext().put("failureFileNames",
						stepExecution.getExecutionContext().get("fileName"));
			}
		} else {

			if (null != stepExecution.getJobExecution().getExecutionContext().get("successCount")) {

				int successCount = (int) stepExecution.getJobExecution().getExecutionContext().get("successCount");
				stepExecution.getJobExecution().getExecutionContext().put("successCount", successCount + 1);

			} else {
				stepExecution.getJobExecution().getExecutionContext().put("successCount", 1);
			}
		}
		return null;
	}

}