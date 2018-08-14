package com.docusign.proserv.application.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class PSProperties {

	@Autowired
	private Environment env;
	
	public String getCustomerSystemText() {
		return env.getProperty("customer.system.text");
	}

	public String getDefaultCsvFolder() {
		return env.getProperty("output.default.folder");
	}

	public String getUserAccountFilename() {
		return env.getProperty("useraccount.filename");
	}

	public String getEntitlementFilename() {
		return env.getProperty("entitlement.filename");
	}

	public String getDSOAuthTokenAPI() {
		return env.getProperty("ds.oauth.token.api");
	}

	public String getDSOAuth2TokenAPI() {
		return env.getProperty("ds.oauth2.token.api");
	}

	public String getDSUsersAPI() {
		return env.getProperty("ds.users.api");
	}

	public String getTokenOutputMessage() {
		return env.getProperty("token.output.message");
	}

	public String getProcessOutputMessage() {
		return env.getProperty("process.output.message");
	}

	public String getDSAPIError() {
		return env.getProperty("ds.api.error");
	}

	public String getPSUnknownError() {
		return env.getProperty("ps.unknown.error");
	}

	public String getEnvErrorMessage() {
		return env.getProperty("environment.error.message");
	}
	
	public String getOperationErrorMessage() {
		return env.getProperty("dsoperation.error.message");
	}

	public String getAccountErrorMessage() {
		return env.getProperty("account.error.message");
	}

	public String getAuthLoginApi() {
		return env.getProperty("ds.auth.login.api");
	}

	public String getDemoEnvErrorMessage() {
		return env.getProperty("demo.environment.error.message");
	}

	public String getProdEnvErrorMessage() {
		return env.getProperty("prod.environment.error.message");
	}

	public String getDemoEnvHost() {
		return env.getProperty("demo.oauth2.as.api.host");
	}

	public String getProdEnvHost() {
		return env.getProperty("prod.oauth2.as.api.host");
	}

	public String getAuthUserInfo() {
		return env.getProperty("ds.oauth2.userinfo.api");
	}

	public String getUserListApi() {
		return env.getProperty("ds.users.list.api");
	}

	public String getAccountCustomFieldsApi() {
		return env.getProperty("ds.account.envelope.customfields.api");
	}
	
	public String getEnvelopesNotificationApi() {
		return env.getProperty("ds.account.envelope.notification.api");
	}

	public String getReqKeysMissingErrorMessage() {
		return env.getProperty("ps.req.keys.missing.error.message");
	}
	
	public String getFriendlyAccountIdMissingErrorMessage(){
		return env.getProperty("ps.friendlyaccountid.missing.error.message");
	}
	
	public String getFriendlyAccountIdListEmptyErrorMessage(){
		return env.getProperty("ps.friendlyaccountid.listitems.empty.error.message");
	}
	
	public String getFriendlyAccountIdListInvalidErrorMessage(){
		return env.getProperty("ps.friendlyaccountid.listitems.invalid.error.message");
	}
}