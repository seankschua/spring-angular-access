package com.exp.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.exp.adwords.BatchProcessing;
import com.exp.adwords.DownloadCriteriaReportWithAwql;
import com.exp.adwords.ReportProcessing;
import com.exp.adwords.Test;
import com.exp.bigquery.GettingStarted;

public class AdwordsController {
	
	public static void oneUpload(String clientId, String type, int startDate, int endDate) throws Exception{
		
		File pathFile = new File("");
		String path = pathFile.getAbsolutePath() + "/src/main/resources/adwords/";
		String rawFile = type + "_" + clientId + "_" + startDate  + "_" + endDate + "_" + "report.csv";
		String processedFile = type + "_" + clientId + "_" + startDate  + "_" + endDate + "_" + "report_processed.csv";
		
		DownloadCriteriaReportWithAwql.downloadReport(clientId, BatchProcessing.generateQuery(type, startDate, endDate), rawFile, path);
		
		System.out.println("Report downloaded.");

		ReportProcessing.cleanReport(rawFile, processedFile, type, path);
		
		System.out.println("Report processed.");
		
		GettingStarted.uploadBQ(type, processedFile, path);
		
		System.out.println("Report uploaded.");
		
	}
	
	public static HashMap<String, String> clientList(){
		
		HashMap<String, String> output = new HashMap<String, String>();
		output.put("107-419-4775", "G:FAR_EAST:PT:$:SD");
		output.put("132-066-5226", "G:SOUTH_PACIFIC:PT:$:SD");
		output.put("186-235-1155", "G:NZL:DL:$:SD");
		output.put("205-085-2785", "G:WEST_ASIA:PT:$:SD");
		output.put("304-175-4936", "G:JP_SOUTH_KOREA:DL:$:SD");
		output.put("494-728-9561", "G:NZL:PT:$:SD");
		output.put("536-619-7879", "G:FAR_EAST:DL:$:SD");
		output.put("614-715-1681", "G:WEST_ASIA:DL:$:SD");
		output.put("625-898-2657", "G:THAILAND:PT:$:SD");
		output.put("650-647-5316", "G:THAILAND:DL:$:SD");
		output.put("652-642-7655", "G:APAC:GT:X:SD");
		output.put("750-364-8530", "G:AUS:DL:$:SD");
		output.put("755-787-7834", "G:AUS:PT:$:SD");
		output.put("785-989-6624", "G:JP_SOUTH_KOREA:PT:$:SD");
		output.put("878-409-7667", "G:SOUTH_PACIFIC:DL:$:SD");		
		
		return output;
	}
	
	public static ArrayList<Integer> monthList(){
		
		ArrayList<Integer> output = new ArrayList<Integer>();
		output.add(20150101);
		output.add(20150201);
		output.add(20150301);
		output.add(20150401);
		output.add(20150501);
		output.add(20150601);
		output.add(20150701);
		output.add(20150801);
		output.add(20150901);
		output.add(20151001);
		
		return output;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*String clientId = "625-898-2657";
		//String type = "audience";
		String type = "adgroup";
		//String type = "criteria";
		int startDate = 20150101;
		int endDate = 20150131;
		
		oneUpload(clientId, type, startDate, endDate);*/
		
		String[] types = {"audience","adgroup"};
		//String type = "audience";
		//String type = "adgroup";
		//String type = "criteria";
		//int startDate = 20150101;
		//int endDate = 20150131;
		HashMap<String, String> clients = clientList();
		ArrayList<Integer> months = monthList();
		int totalJobCount = clients.keySet().size() * months.size();
		int jobCounter = 0;
		
		for (String type:types){
			for (String clientId:clients.keySet()){
				for (int startDate:months){
					int endDate = Test.endMonth(startDate);
					oneUpload(clientId, type, startDate, endDate);
					jobCounter++;
					System.out.println(jobCounter + " out of " + totalJobCount + " complete.");
				}
			}
		}
		
	}

}
