package com.docusign.proserv.application.commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.exception.InvalidInputException;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.jwt.domain.Account;
import com.docusign.jwt.domain.LoginUserInfo;
import com.docusign.proserv.application.domain.BulkOperations;
import com.docusign.proserv.application.domain.DSEnvironment;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;

@Configuration
@Component
public class PSBulkOpsCommands extends AbstractPSCommands implements CommandMarker {

	final static Logger logger = Logger.getLogger(PSBulkOpsCommands.class);

	@Resource
	private Environment env;

	@Autowired
	public PSProperties psProps;

	@CliAvailabilityIndicator({ "ps_bulkops_process" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "ps_bulkops_process", help = "Generate Report CSVs")
	public int process(@CliOption(key = { "userIds" }, mandatory = true, help = "UserIds") final String userIds,
			@CliOption(key = { "integratorKey" }, mandatory = true, help = "Integrator Key") final String integratorKey,
			@CliOption(key = { "scope" }, mandatory = true, help = "Scope") final String scope,
			@CliOption(key = {
					"tokenExpiryLimit" }, mandatory = true, help = "Access Token Expiry Limit") final String tokenExpiryLimit,
			@CliOption(key = {
					"privatePemPath" }, mandatory = true, help = "Private Pem Path") final String privatePemPath,
			@CliOption(key = {
					"publicPemPath" }, mandatory = true, help = "Public Pem Path") final String publicPemPath,
			@CliOption(key = { "env" }, mandatory = true, help = "DS environment") final String environment,
			@CliOption(key = { "proxyHost" }, mandatory = false, help = "DS ProxyHost") final String proxyHost,
			@CliOption(key = { "proxyPort" }, mandatory = false, help = "DS ProxyPort") final String proxyPort,
			@CliOption(key = {
					"operationNames" }, mandatory = true, help = "DS Bulk Operation Names") final String dsBulkOperationNames,
			@CliOption(key = {
					"inDirpath" }, mandatory = true, help = "Input Directory Path") String inputDirectoryPath,
			@CliOption(key = {
					"outDirpath" }, mandatory = true, help = "Output Directory Path") String outputDirectoryPath,
			@CliOption(key = {
					"appMaxThreadPoolSize" }, mandatory = true, help = "App Max Thread Pool Size") String appMaxThreadPoolSize,
			@CliOption(key = {
					"appCoreThreadPoolSize" }, mandatory = true, help = "App Core Thread Pool Size") String appCoreThreadPoolSize,
			@CliOption(key = {
					"appReminderAllowed" }, mandatory = false, help = "App Reminder Allowed") String appReminderAllowed,
			@CliOption(key = {
					"appExpirationAllowed" }, mandatory = false, help = "App Expiration Allowed") String appExpirationAllowed,
			@CliOption(key = {
					"validAccountGuids" }, mandatory = true, help = "Valid Account Guids") String validAccountGuids)
			throws IOException {

		logger.info("******************************** ps_bulkops_process Job started ********************************");

		if (!EnumUtils.isValidEnum(DSEnvironment.class, environment.toUpperCase())) {

			logger.error(psProps.getEnvErrorMessage());
			return -1;
		}

		try {

			printInputParameters(userIds, integratorKey, scope, tokenExpiryLimit, privatePemPath,
					publicPemPath, environment, proxyHost, proxyPort, dsBulkOperationNames, inputDirectoryPath,
					outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
					appExpirationAllowed, validAccountGuids);

			performBulkOperationForEachUser(userIds, integratorKey, scope, tokenExpiryLimit, privatePemPath,
					publicPemPath, environment, proxyHost, proxyPort, dsBulkOperationNames, inputDirectoryPath,
					outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
					appExpirationAllowed, validAccountGuids);

		} catch (HttpClientErrorException e) {

			logger.error("Exception with Response Body " + e.getResponseBodyAsString());
			logger.error(MessageFormat.format(psProps.getDSAPIError(), e.getMessage()));

			e.printStackTrace();
			return -1;

		} catch (Exception e) {

			logger.error(MessageFormat.format(psProps.getPSUnknownError(), e.getMessage()));

			e.printStackTrace();
			return -1;
		}

		return showSuccessFailureMessages(outputDirectoryPath, "ps_oauth_process");
	}

	private void printInputParameters(String userIds, String integratorKey, String scope,
			String tokenExpiryLimit, String privatePemPath, String publicPemPath, String environment, String proxyHost,
			String proxyPort, String dsBulkOperationNames, String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, String appReminderAllowed,
			String appExpirationAllowed, String validAccountGuids) {

		if (logger.isDebugEnabled()) {

			logger.debug("*********************** Input Parameters are ***********************");

			logger.debug("Input userIds: " + userIds);
			logger.debug("Input integratorKey: " + integratorKey);
			logger.debug("Input scope: " + scope);
			logger.debug("Input tokenExpiryLimit:" + tokenExpiryLimit);
			logger.debug("Input privatePemPath: " + privatePemPath);
			logger.debug("Input publicPemPath: " + publicPemPath);
			logger.debug("Input environment: " + environment);
			logger.debug("Input proxyHost: " + proxyHost);
			logger.debug("Input proxyPort: " + proxyPort);
			logger.debug("Input dsBulkOperationNames: " + dsBulkOperationNames);
			logger.debug("Input inputDirectoryPath: " + inputDirectoryPath);
			logger.debug("Input outputDirectoryPath: " + outputDirectoryPath);
			logger.debug("Input appMaxThreadPoolSize: " + appMaxThreadPoolSize);
			logger.debug("Input appCoreThreadPoolSize: " + appCoreThreadPoolSize);
			logger.debug("Input appReminderAllowed: " + appReminderAllowed);
			logger.debug("Input appExpirationAllowed: " + appExpirationAllowed);
			logger.debug("Input validAccountGuids: " + validAccountGuids);
		}
	}

	private void performBulkOperationForEachUser(final String userIds, final String integratorKey,
			final String scope, final String tokenExpiryLimit, final String privatePemPath,
			final String publicPemPath, final String environment, final String proxyHost, final String proxyPort,
			String dsBulkOperationNames, String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, String appReminderAllowed,
			String appExpirationAllowed, String validAccountGuids) throws IOException {

		List<String> userIdList = PSUtils.splitStringtoList(userIds, COMMA_DELIMITER);
		List<String> dsBulkOperationList = PSUtils.splitStringtoList(dsBulkOperationNames, COMMA_DELIMITER);
		List<String> validAccountGuidList = PSUtils.splitStringtoList(validAccountGuids, COMMA_DELIMITER);

		logger.info("validAccountGuidList " + validAccountGuidList);

		StringBuilder strBuilder = new StringBuilder();

		String audience = getAudienceForJWT(environment);
		String oAuthUrl = MessageFormat.format(psProps.getDSOAuth2TokenAPI(), audience);

		for (String userId : userIdList) {

			AccessToken userToken = createOAuthToken(userId, integratorKey, privatePemPath, publicPemPath,
					environment, scope, tokenExpiryLimit, proxyHost, proxyPort, audience, oAuthUrl);
			strBuilder.append(NEW_LINE);
			strBuilder.append(userId + "'s Token: " + userToken.getAccessToken());

			List<Account> userAccounts = fetchUserAccounts(environment, userToken, proxyHost, proxyPort);

			for (Account account : userAccounts) {

				if (null != validAccountGuidList && !validAccountGuidList.isEmpty()
						&& validAccountGuidList.contains(account.getAccountId())) {

					for (String dsOperationName : dsBulkOperationList) {

						if (!EnumUtils.isValidEnum(BulkOperations.class, dsOperationName.toUpperCase())) {

							logger.error(psProps.getOperationErrorMessage());
							throw new InvalidInputException(dsOperationName);
						}

						switch (EnumUtils.getEnum(BulkOperations.class, dsOperationName)) {

						case NOTIFICATIONCHANGES:
							processBulkNotificationChanges(inputDirectoryPath, outputDirectoryPath,
									appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
									appExpirationAllowed, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath,
									publicPemPath, scope, tokenExpiryLimit, audience, oAuthUrl);
							break;
						case ENVELOPEUPDATES:

							processBulkEnvelopeUpdates(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize,
									appCoreThreadPoolSize, proxyHost, proxyPort, account.getBaseUri(),
									account.getAccountId(), userId, integratorKey, privatePemPath,
									publicPemPath, scope, tokenExpiryLimit, audience, oAuthUrl);
							break;

						default:
							logger.error("No Bulk Operation Job to run, check the DS Operation Name");

						}
					}
				}
			}

		}

		logger.debug(strBuilder.toString());

	}

	/**
	 * @param environment
	 * @param token
	 * @param proxyHost
	 * @param proxyPort
	 * @return list of LoginAccount
	 */
	private List<Account> fetchUserAccounts(String environment, AccessToken accessToken, String proxyHost,
			String proxyPort) {

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken());
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		RestTemplate restTemplate = PSUtils.initiateRestTemplate(proxyHost, proxyPort);

		String host = getAudienceForJWT(environment);

		String url = MessageFormat.format(psProps.getAuthUserInfo(), host);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		logger.debug("In PSBulkOpsCommands.fetchUserAccounts(), Resturl is " + url + " entity " + entity);
		ResponseEntity<LoginUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				LoginUserInfo.class);

		return response.getBody().getAccounts();

	}

	private void processBulkNotificationChanges(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, String appReminderAllowed,
			String appExpirationAllowed, final String proxyHost, final String proxyPort, String baseUri,
			String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize,
				appReminderAllowed, appExpirationAllowed, proxyHost, proxyPort, baseUri, accountGuid, userId,
				integratorKey, privatePemPath, publicPemPath, scope, tokenExpiryLimit, audience, url,
				AppConstants.SPRING_CONTEXT_FILE_PATH, AppConstants.STRING_JOB_REPORT_FILE_PATH,
				AppConstants.SPRING_JOB_LAUNCHER, AppConstants.SPRING_REPORT_JOB_NAME,
				BulkOperations.NOTIFICATIONCHANGES.toString());
	}

	private void processBulkEnvelopeUpdates(String inputDirectoryPath, String outputDirectoryPath,
			String appMaxThreadPoolSize, String appCoreThreadPoolSize, final String proxyHost, final String proxyPort,
			String baseUri, String accountGuid, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope, final String tokenExpiryLimit,
			String audience, String url) {

		invokeBatchJob(inputDirectoryPath, outputDirectoryPath, appMaxThreadPoolSize, appCoreThreadPoolSize, null, null,
				proxyHost, proxyPort, baseUri, accountGuid, userId, integratorKey, privatePemPath,
				publicPemPath, scope, tokenExpiryLimit, audience, url, AppConstants.SPRING_CONTEXT_FILE_PATH,
				AppConstants.SPRING_JOB_ENV_UPDATE_FILE_PATH, AppConstants.SPRING_JOB_LAUNCHER,
				AppConstants.SPRING_REPORT_ENV_UPDATE_JOB_NAME, BulkOperations.ENVELOPEUPDATES.toString());

	}

	/**
	 * @param inputDirectoryPath
	 * @param outputDirectoryPath
	 * @param appMaxThreadPoolSize
	 * @param appCoreThreadPoolSize
	 * @param appReminderAllowed
	 * @param appExpirationAllowed
	 * @param proxyHost
	 * @param proxyPort
	 * @param baseUri
	 * @param accountGuid
	 * @param userId
	 * @param integratorKey
	 * @param secretKey
	 * @param privatePemPath
	 * @param publicPemPath
	 * @param scope
	 * @param tokenExpiryLimit
	 * @param audience
	 * @param url
	 * @param springContextFilePath
	 * @param springJobFilePath
	 * @param springLauncherName
	 * @param springJobName
	 */
	private void invokeBatchJob(String inputDirectoryPath, String outputDirectoryPath, String appMaxThreadPoolSize,
			String appCoreThreadPoolSize, String appReminderAllowed, String appExpirationAllowed,
			final String proxyHost, final String proxyPort, String baseUri, String accountGuid, final String userId,
			final String integratorKey, final String privatePemPath, final String publicPemPath,
			final String scope, final String tokenExpiryLimit, String audience, String url,
			String springContextFilePath, String springJobFilePath, String springLauncherName, String springJobName,
			String operationName) {

		ApplicationContext context = null;
		File inputDirFile = new File(inputDirectoryPath);
		File outputDirFile = new File(outputDirectoryPath);
		try {

			if (PSUtils.isInputDirValid(inputDirFile) && null != outputDirFile && outputDirFile.isDirectory()) {

				String[] springConfig = { springContextFilePath, springJobFilePath };

				context = new ClassPathXmlApplicationContext(springConfig);

				loadAppParameters(context, appMaxThreadPoolSize, appCoreThreadPoolSize, appReminderAllowed,
						appExpirationAllowed, proxyHost, proxyPort, userId, integratorKey, privatePemPath,
						publicPemPath, scope, tokenExpiryLimit, audience, url, operationName);

				JobLauncher jobLauncher = (JobLauncher) context.getBean(springLauncherName);
				Job job = (Job) context.getBean(springJobName);

				Map<String, JobParameter> parametersMap = new LinkedHashMap<String, JobParameter>();

				Date jobStartDate = Calendar.getInstance().getTime();

				logger.info("jobStartDate for " + operationName + " in PSBulkOpsCommands.invokeBatchJob()- "
						+ jobStartDate);

				DateFormat format = new SimpleDateFormat(AppConstants.FILE_NAME_DATE_PATTERN);
				parametersMap.put(AppConstants.JOB_START_TIME_PARAM_NAME,
						new JobParameter(format.format(jobStartDate)));
				parametersMap.put(AppConstants.JOB_INPUT_DIRECTORY_PATH_NAME, new JobParameter(inputDirectoryPath));
				parametersMap.put(AppConstants.JOB_OUTPUT_DIRECTORY_PATH_NAME, new JobParameter(outputDirectoryPath));
				parametersMap.put(AppConstants.JOB_BASEURI_VALUE, new JobParameter(baseUri));
				parametersMap.put(AppConstants.JOB_ACCOUNDGUID_VALUE, new JobParameter(accountGuid));

				File[] files = inputDirFile.listFiles();
				String totalFilesCount = String.valueOf(files.length);
				parametersMap.put(AppConstants.TOTAL_FILES_COUNT, new JobParameter(totalFilesCount));

				JobParameters jobParameters = new JobParameters(parametersMap);

				logger.info(
						" ------------ About to start job in PSBulkOpsCommands.invokeBatchJob() ------------ " + job);

				JobExecution execution = jobLauncher.run(job, jobParameters);

				logger.info("In PSBulkOpsCommands.invokeBatchJob() currentCount- "
						+ execution.getExecutionContext().get("currentCount") + " totalFileCount- " + totalFilesCount
						+ " successCount- " + execution.getExecutionContext().get("successCount") + " failureCount- "
						+ execution.getExecutionContext().get("failureCount") + " failureFileNames- "
						+ execution.getExecutionContext().get("failureFileNames"));
				logger.info(" ------------ Exit Status in PSBulkOpsCommands.invokeBatchJob() ------------  "
						+ execution.getStatus());
			} else {
				logger.error(inputDirectoryPath
						+ " is not a directory or no file exists in the directory in PSBulkOpsCommands.invokeBatchJob()");
			}
		} catch (JobExecutionAlreadyRunningException e) {
			logger.error("JobExecutionAlreadyRunningException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (JobRestartException e) {
			logger.error("JobRestartException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error("JobInstanceAlreadyCompleteException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			logger.error("JobParametersInvalidException in PSBulkOpsCommands.invokeBatchJob()" + e);
			e.printStackTrace();
		} finally {
			if (null != context) {
				((ClassPathXmlApplicationContext) context).close();
			}
		}
	}

	private void loadAppParameters(ApplicationContext context, String appMaxThreadPoolSize,
			String appCoreThreadPoolSize, String appReminderAllowed, String appExpirationAllowed,
			final String proxyHost, final String proxyPort, final String userId, final String integratorKey,
			final String privatePemPath, final String publicPemPath, final String scope,
			final String tokenExpiryLimit, String audience, String url, String operationName) {

		Integer maxThreadPoolSize = Integer.parseInt(appMaxThreadPoolSize);
		Integer coreThreadPoolSize = Integer.parseInt(appCoreThreadPoolSize);

		Boolean reminderAllowed = Boolean.parseBoolean(appReminderAllowed);
		Boolean expirationAllowed = Boolean.parseBoolean(appExpirationAllowed);

		logger.info("Reminder Allowed in MainApp.loadAppParameters() " + reminderAllowed + " Expiration Allowed- "
				+ expirationAllowed);

		AppParameters appParameters = (AppParameters) context.getBean(AppConstants.APP_PARAMETERS_BEAN_NAME);
		appParameters.setCoreThreadPoolSize(coreThreadPoolSize);
		appParameters.setMaxThreadPoolSize(maxThreadPoolSize);
		appParameters.setReminderAllowed(reminderAllowed);
		appParameters.setExpirationAllowed(expirationAllowed);
		appParameters.setProxyHost(proxyHost);
		appParameters.setProxyPort(proxyPort);
		appParameters.setUserId(userId);
		appParameters.setIntegratorKey(integratorKey);
		appParameters.setPrivatePemPath(privatePemPath);
		appParameters.setPublicPemPath(publicPemPath);
		appParameters.setScope(scope);
		appParameters.setTokenExpiryLimit(tokenExpiryLimit);
		appParameters.setAudience(audience);
		appParameters.setUrl(url);
		appParameters.setOperationName(operationName);
	}
}