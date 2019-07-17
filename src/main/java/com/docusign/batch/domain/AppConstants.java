/**
 * 
 */
package com.docusign.batch.domain;

/**
 * @author Amit.Bist
 *
 */
public interface AppConstants {

	String APP_INPUT_DIRECTORY_PATH = "app.inputDirectory.path";
	String APP_OUTPUT_DIRECTORY_PATH = "app.outputDirectory.path";
	String APP_REMINDER_ALLOWED = "app.reminder.allowed";
	String APP_EXPIRATION_ALLOWED = "app.expiration.allowed";

	String APP_MAX_THREAD_POOL_SIZE = "app.max.thread.pool.size";
	String APP_CORE_THREAD_POOL_SIZE = "app.core.thread.pool.size";

	String APP_PARAMETERS_BEAN_NAME = "appParameters";

	String SPRING_CONTEXT_FILE_PATH = "classpath*:/META-INF/spring/batch/config/context.xml";
	String STRING_JOB_REPORT_FILE_PATH = "classpath*:/META-INF/spring/batch/jobs/job-report.xml";
	
	String SPRING_JOB_ENV_UPDATE_FILE_PATH = "classpath*:/META-INF/spring/batch/jobs/job-dsenvelopeupdate.xml";

	String SPRING_JOB_LAUNCHER = "jobLauncher";
	String SPRING_REPORT_JOB_NAME = "reportJob";
	String SPRING_REPORT_ENV_UPDATE_JOB_NAME = "envUpdateJob";

	String FILE_NAME_DATE_PATTERN = "yyyyMMdd_HHmmss";

	String JOB_START_TIME_PARAM_NAME = "jobStartTime";
	String JOB_INPUT_DIRECTORY_PATH_NAME = "inputDirectoryPath";
	String JOB_OUTPUT_DIRECTORY_PATH_NAME = "outputDirectoryPath";
	String JOB_BASEURI_VALUE = "baseUri";
	String JOB_ACCOUNDGUID_VALUE = "accountGuid";
	String JOB_DS_THRESHOLD_SECS = "thresholdInSecs";

	String FILE_RESOURCE_PARAM_NAME = "fileResource";
	String FILE_PARAM_NAME = "fileName";
	String TOTAL_FILES_COUNT = "totalFilesCount";
	String CURRENT_FILES_COUNT = "currentFilesCount";

	String TRANS_SUCCESS_MSG = "Transaction Successful";

	String RSA_ALGO = "RSA";

	String DS_HEADER_X_RATELIMIT_RESET = "X-RateLimit-Reset";
	String DS_HEADER_X_RATELIMIT_LIMIT = "X-RateLimit-Limit";
	String DS_HEADER_X_RATELIMIT_REMAINING = "X-RateLimit-Remaining";

}
