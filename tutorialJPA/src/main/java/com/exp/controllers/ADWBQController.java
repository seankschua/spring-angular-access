package com.exp.controllers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.exp.adwords.BatchProcessing;
import com.exp.adwords.DownloadCriteriaReportWithAwql;
import com.exp.adwords.ReportProcessing;
import com.exp.adwords.Test;
import com.exp.bigquery.GettingStarted;
import com.exp.bigquery.ManualUploads;

@Component
public class ADWBQController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Scheduled(cron="0 0 4 * * *", zone="UTC")
    public void uploadDaily() throws IOException {
		
		String[] types = {"audience","adgroup"};
		HashMap<String, String> clients = ManualUploads.clientList();
		
		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		today.add(Calendar.DAY_OF_MONTH, -2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int startDate = Integer.parseInt(sdf.format(today.getTime()));
		int endDate = startDate;
		
		int totalJobCount = types.length * clients.keySet().size();
		int jobCounter = 0;
		int errorJobs = 0;
		
		log.info("uploadDaily(): Initialised for " + startDate + ", " + totalJobCount +  " jobs.");
		
		File pathFile = new File("");
		String path = pathFile.getAbsolutePath() + "/src/main/resources/adwords/";
		
		for (String type:types){
			for (String clientId:clients.keySet()){

				String rawFile = type + "_" + clientId + "_" + startDate  + "_" + endDate + "_" + "report.csv";
				String processedFile = type + "_" + clientId + "_" + startDate  + "_" + endDate + "_" + "report_processed.csv";
				
				try {
					
					DownloadCriteriaReportWithAwql.downloadReport(clientId, BatchProcessing.generateQuery(type, startDate, endDate), rawFile, path);
					
					log.info(rawFile + " downloaded.");

					ReportProcessing.cleanReport(rawFile, processedFile, type, path);
					
					log.info(processedFile + " processed.");
					
					GettingStarted.uploadBQ(type, processedFile, path);
					
					log.info(processedFile + " uploaded.");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					errorJobs++;
					log.error(e.getMessage());
				}
				jobCounter++;
				log.info("uploadDaily(): " + startDate + " Job " + jobCounter + "/" + totalJobCount + " complete.");
			}
		}
		FileUtils.cleanDirectory(new File(path));
		log.info("uploadDaily(): Completed with " + errorJobs + " errors encountered.");
    }

}
