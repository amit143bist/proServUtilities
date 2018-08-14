@echo off

rem This script will process and generate csv files in the specified csvpath. 
rem This will be scheduled by the Customer and a sample cron is shown below
rem crontab - example daily job scheduled at 2am
rem * 2 * * * [psdir]/ps_oauth_process_demo.bat

rem script parameters
set userIds="51828699-9931-49e9-8426-79dcf3796dd3"
set integratorKey="4a571161-05ee-4812-83d0-da7ff5c2eab9"
set secretKey="978bcbb5-da53-4492-ac0b-4031fa245c7e"
set scope="signature"
set privatePemPath="C:\Softwares\GateKeeper/DemoOAuth-private.pem"
set publicPemPath="C:\Softwares\GateKeeper/DemoOAuth-public.pem"
set excludedAccountGUIDs=""
set env="demo"
set jarpath="C:\DocuApi\proServUtilities\target/proServUtilities-1.0.jar"
set csvpath="C:\Softwares\GateKeeper/testgc"
set narid="90895-3"
set groupNameContains=""
set proxyHost=""
set proxyPort=""
set accountIdEnvelopeCustomFieldName="ExternalAccountId"
set propertyPath="C:\Softwares\GateKeeper/profileDesc.properties"
set logfilePath="C:\Softwares\GateKeeper/proServUtilities.log"
set loglevel="info"
set logfileAppendFlag="false"
set logfileAppendDatePattern="'_'yyyy-MM-dd-HH"
set mandatoryPropKeys="'DocuSign Viewer_Required,DocuSign Sender_Required,Account Administrator_Required'"
set systemUserEmails="dtrssotestdemo@teksentric.com,barpe061OrgUser@teksentric.com"
set includeInvalidUsersFlag="FalSE"
set csvFieldNames="rights"

rem print parameters
echo Generating reports with narid %narid%. Files will be placed at %csvpath%.

rem execute
java -DpropertyPath=%propertyPath% -Dlogfile.name=%logfilePath% -Dloglevel=%loglevel% -DlogfileAppendFlag=%logfileAppendFlag% -DlogfileAppendDatePattern=%logfileAppendDatePattern% -jar %jarpath% ps_oauth_process --userIds %userIds% --integratorKey %integratorKey% --secretKey %secretKey% --scope %scope% --privatePemPath %privatePemPath% --publicPemPath %publicPemPath% --excludedAccountGUIDs %excludedAccountGUIDs% --env %env% --csvpath %csvpath% --narid %narid% --groupNamecontains %groupNameContains% --systemUserEmails %systemUserEmails% --accountIdEnvelopeCustomFieldName %accountIdEnvelopeCustomFieldName% --mandatoryPropKeys %mandatoryPropKeys% --includeInvalidUsersFlag %includeInvalidUsersFlag% --csvFieldNames %csvFieldNames% --proxyHost %proxyHost% --proxyPort %proxyPort%