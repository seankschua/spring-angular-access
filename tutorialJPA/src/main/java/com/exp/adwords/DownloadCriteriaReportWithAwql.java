package com.exp.adwords;

import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201509.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201502.rm.AdwordsUserListService;
import com.google.api.ads.adwords.axis.v201509.cm.Selector;
import com.google.api.ads.adwords.axis.v201509.rm.AdwordsUserListServiceInterface;
import com.google.api.ads.adwords.axis.v201509.rm.UserList;
import com.google.api.ads.adwords.axis.v201509.rm.UserListOperation;
import com.google.api.ads.adwords.axis.v201509.rm.UserListPage;

//Copyright 2015 Google Inc. All Rights Reserved.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.jaxb.v201509.DownloadFormat;
import com.google.api.ads.adwords.lib.selectorfields.v201509.cm.AdwordsUserListField;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.v201509.ReportDownloader;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.client.auth.oauth2.Credential;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
* This example downloads a criteria performance report with AWQL.
*
* <p>Credentials and properties in {@code fromFile()} are pulled from the
* "ads.properties" file. See README for more info.
*/
public class DownloadCriteriaReportWithAwql {

public static void downloadReport(String customerId, String query, String fileName) throws Exception {
	
	File pathFile = new File("");
	String path = pathFile.getAbsolutePath();
	
 // Generate a refreshable OAuth2 credential.
 Credential oAuth2Credential = new OfflineCredentials.Builder()
	      .forApi(Api.ADWORDS)
	      .withClientSecrets(Secret.CLIENT_ID, Secret.CLIENT_SECRET)
	      .withRefreshToken(Secret.REFRESH_TOKEN)
	      .build()
	      .generateCredential();

 // Construct an AdWordsSession.
 AdWordsSession session = new AdWordsSession.Builder()
     .withDeveloperToken(Secret.DEVELOPER_TOKEN)
     .withOAuth2Credential(oAuth2Credential)
     .withUserAgent(Secret.USER_AGENT)
     .withClientCustomerId(customerId)
     .build();

 // Location to download report to.
 String reportFile = path + "/src/main/resources/adwords/" + fileName + ".csv";
 runExample(session, query, reportFile);
}

public static void runExample(AdWordsSession session, String query, String reportFile) throws Exception {
 // Create query.
	

 // Optional: Set the reporting configuration of the session to suppress header, column name, or
 // summary rows in the report output. You can also configure this via your ads.properties
 // configuration file. See AdWordsSession.Builder.from(Configuration) for details.
 // In addition, you can set whether you want to explicitly include or exclude zero impression
 // rows.
 ReportingConfiguration reportingConfiguration =
     new ReportingConfiguration.Builder()
         .skipReportHeader(true)
         .skipColumnHeader(false)
         .skipReportSummary(true)
         // Set to false to exclude rows with zero impressions.
         .includeZeroImpressions(false)
         .build();
 session.setReportingConfiguration(reportingConfiguration);
 
 if(reportFile.contains("audience")){
	 int offset = 0;
	 int page_size = 1000;
	 
	 AdWordsServices adWordsServices = new AdWordsServices();
	 AdwordsUserListServiceInterface userListService =
		        adWordsServices.get(session, AdwordsUserListServiceInterface.class);
	 SelectorBuilder builder = new SelectorBuilder();
	 Selector selector = builder
	     .fields(AdwordsUserListField.Id, AdwordsUserListField.Name)
	     .orderAscBy(AdwordsUserListField.Id)
	     .offset(offset)
	     .limit(page_size)
	     .build();
	 
	 UserListPage page = null;
	 File pathFile = new File("");
	 String path = pathFile.getAbsolutePath();
	 String audiencePath = path + "/src/main/resources/adwords/" + "audienceListNames" + ".csv";
	 CsvWriter writer = new CsvWriter(new OutputStreamWriter(new FileOutputStream(audiencePath), "UTF-8"), new CsvWriterSettings());
	 do {
		  // Get all campaigns.
		  page = userListService.get(selector);

		  // Display campaigns.
		  if (page.getEntries() != null) {
		    for (UserList userList : page.getEntries()) {
		      /*System.out.printf("userList with name '%s' and ID %d was found.%n", userList.getName(),
		    		  userList.getId());*/
		    	writer.writeRow(Arrays.asList(userList.getId(), userList.getName()));
		    }
		  } else {
		    System.out.println("No userList were found.");
		  }

		  offset += page_size;
		  selector = builder.increaseOffsetBy(page_size).build();
		} while (offset < page.getTotalNumEntries());
	 writer.close();
	 System.out.println("Audience List successfully retrieved.");
 }
 
 try {
   // Set the property api.adwords.reportDownloadTimeout or call
   // ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
   // for CONNECT and READ in report downloads.
   ReportDownloadResponse response =
       new ReportDownloader(session).downloadReport(query, DownloadFormat.CSVFOREXCEL);
   response.saveToFile(reportFile);
   
   System.out.printf("Report successfully downloaded to: %s%n", reportFile);
 } catch (ReportDownloadResponseException e) {
   System.out.printf("Report was not downloaded due to: %s%n", e);
 }
}
}
