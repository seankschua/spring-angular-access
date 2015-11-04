package com.exp.adwords;

import java.io.File;

public class BatchProcessing {
	
	public static void main(String[] args){
		
		String clientId = "625-898-2657";
		//String type = "audience";
		String type = "adgroup";
		//String type = "criteria";
		int startDate = 20151026;
		int endDate = 20151027;
		
		
		try {
			
			File pathFile = new File("");
			String path = pathFile.getAbsolutePath() + "/src/main/resources/adwords/";
			String fileName = type + "_" + clientId + "_" + startDate  + "_" + endDate + "_" + "report";
			
			DownloadCriteriaReportWithAwql.downloadReport(clientId, generateQuery(type, startDate, endDate), fileName, path);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String generateQuery(String type, int startDate, int endDate){
		
		String output = "";
		
		switch(type){
			case "audience":
				output = "SELECT Date, ExternalCustomerId, CampaignId, AdGroupId, Id, "
						+ "CustomerDescriptiveName, CampaignName, AdGroupName, Criteria, Status, AdGroupStatus, "
						+ "AveragePosition, Ctr, ConversionRate, CpcBid, AverageCpc, CostPerConversion, ValuePerConversion, "
						+ "Impressions, Clicks, Conversions, "
						+ "Cost, ConversionValue, "
						+ "BidModifier, IsRestrict, BidType, Device "
						+ "FROM AUDIENCE_PERFORMANCE_REPORT "
						+ "WHERE AdGroupStatus IN [ENABLED, PAUSED] "
						+ "DURING " + startDate + ","  + endDate;
				break;
			case "adgroup":
				output = "SELECT Date, ExternalCustomerId, CampaignId, AdGroupId, "
						+ "CustomerDescriptiveName, CampaignName, AdGroupName, AdGroupStatus, "
						+ "AveragePosition, Ctr, ConversionRate, CpcBid, AverageCpc, CostPerConversion, ValuePerConversion, "
						+ "Impressions, Clicks, Conversions, "
						+ "Cost, ConversionValue, "
						+ "BidType, Device, "
						+ "LabelIds, Labels "
						+ "FROM ADGROUP_PERFORMANCE_REPORT "
						+ "WHERE AdGroupStatus IN [ENABLED, PAUSED] "
						+ "DURING " + startDate + ","  + endDate;
				break;
			case "criteria":
				output = "SELECT Date, ExternalCustomerId, CampaignId, AdGroupId, Id, Criteria "
						+ "FROM CRITERIA_PERFORMANCE_REPORT "
						+ "WHERE AdGroupStatus IN [ENABLED, PAUSED] "
						+ "DURING " + startDate + ","  + endDate;
				break;	
		}
		return output;
		
	}
	
	
	
}
