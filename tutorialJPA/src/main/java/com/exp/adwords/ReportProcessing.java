package com.exp.adwords;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;

public class ReportProcessing {
	
	public static void cleanReport(String rawFile, String processedFile, String type, String path) throws IOException{
		
		String filePath = path + rawFile;
		String output = path + processedFile;
		String outputSchemeFile = path + type + "_scheme.json";
		
		TsvParserSettings settings = new TsvParserSettings();
	    //the file used in the example uses '\n' as the line separator sequence.
	    //the line separator sequence is defined here to ensure systems such as MacOS and Windows
	    //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
	    settings.getFormat().setLineSeparator("\n");

	    // creates a CSV parser
	    TsvParser parser = new TsvParser(settings);

	    // parses all rows in one go.
	    parser.beginParsing(new InputStreamReader(new FileInputStream(filePath), "UTF-16"));
	    
	    // As with the CsvWriter, all you need is to create an instance of TsvWriter with the default TsvWriterSettings.
	    TsvWriter writer = new TsvWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"), new TsvWriterSettings());
		
	    //cleaning header row
		String[] headerRow = parser.parseNext();
		int columnCount = headerRow.length;
	    for(int i=0;i<columnCount;i++){
	    	String[] tempArray = headerRow[i].split(" ");
	    	String temp = "";
	    	for(String s:tempArray){
	    		temp += StringUtils.capitalize(s);
	    	}
	    	headerRow[i] = temp.replace(".", "").replace("/", "");
	    }
	    
	    //adding audienceId to end if audience type
	    if(type.contentEquals("audience")){
	    	ArrayList<String> addAud = new ArrayList<String>(Arrays.asList(headerRow));
	    	addAud.add("AudienceID");
	    	headerRow = addAud.toArray(new String[addAud.size()]);
	    }
	    
	    writer.writeHeaders(headerRow);
	    generateBQHeaderScheme(headerRow, outputSchemeFile);
	    
	  //preparing for audience id swap to audience name
	    boolean audiencePresent = Arrays.asList(headerRow).contains("Audience");
		HashMap<String,String> audienceHash = new HashMap<String,String>();
		
		if(audiencePresent){
			CsvParserSettings csvSettings = new CsvParserSettings();
		    settings.getFormat().setLineSeparator("\n");
		    CsvParser csvParser = new CsvParser(csvSettings);

		    csvParser.beginParsing(new InputStreamReader(new FileInputStream(path + "/audience_names.csv"), "UTF-8"));
		    String[] audienceListRow;
		    while ((audienceListRow = csvParser.parseNext()) != null) {
		    	//System.out.println(audienceListRow[0] + "~" + audienceListRow[1]);
		    	audienceHash.put(audienceListRow[0], audienceListRow[1]);
		    }
		}
	    
	    //cleaning data
	    String[] row = null;
	    while((row = parser.parseNext()) != null){
	    	
	    	row = cleanRow(headerRow, row, audienceHash);
	    	writer.writeRow(row);
	    	//System.out.println(Arrays.toString(row));
	    }
	    writer.close();
	    
	    Random r = new Random();
	    System.out.println(Arrays.toString(headerRow));
	    System.out.println("Processing succeeded for " + output);
	    //System.out.println(Arrays.toString(row));
		
	}
	
	public static String[] cleanRow(String[] headerRow, String[] row, HashMap<String,String> audienceHash) throws UnsupportedEncodingException, FileNotFoundException{
		
		String[] perc = {"CTR", "ConvRate", "BidAdj"};
		String[] values = {"AvgCPC", "Cost", "DefaultMaxCPC"};
		String[] valuesComma = {"TotalConvValue", "ValueConv"};
		String[] typeTime = {"Day"};
		
		String audienceId = "";
		
		//make row match headerRow length
		if(!audienceHash.isEmpty()){
	    	ArrayList<String> addAud = new ArrayList<String>(Arrays.asList(row));
	    	//the name string is currently sitting at where ID should be
	    	addAud.add("");
	    	row = addAud.toArray(new String[addAud.size()]);
	    }
		
		for(int i=0;i<row.length;i++){
			//dashes
			row[i] = row[i].replace("--", "");
			//percentage signs
			if(Arrays.asList(perc).contains(headerRow[i])){
				row[i] = row[i].replace("%", "");
			}
			//revenue/costs
			if(Arrays.asList(values).contains(headerRow[i])){
				if(row[i].contentEquals("")){
					continue;
				}
				float cellValue = Float.parseFloat(row[i]);
				row[i] = Float.toString(cellValue/1000000);
			}
			if(Arrays.asList(valuesComma).contains(headerRow[i])){
				row[i] = row[i].replace(",", "");
			}
			//time
			if(Arrays.asList(typeTime).contains(headerRow[i])){
				row[i] = row[i] + " 00:00";
			}
			//audienceList names
			if(headerRow[i].contentEquals("Audience")){
				audienceId = row[i].split("::")[1];
				row[i] = audienceHash.get(audienceId);
			}
			if(headerRow[i].contentEquals("AudienceID")){
				row[i] = audienceId;
			}
			
			
			
		}
		
		return row;
	    
	}
	
	public static void generateBQHeaderScheme(String[] headerRow, String outputSchemeFile) throws IOException{
		
		ArrayList<ColumnProperties> scheme = new ArrayList<ColumnProperties>();
		String[] typeInt = {"CustomerID", "CampaignID", "AdGroupID", "CriterionID", "Impressions", "Clicks", "AudienceID"};
		String[] typeFloat = {"AvgPosition", "CTR", "ConvRate", "MaxCPC", "AvgCPC", "CostConv", "ValueConv", "Conversions",
				"Cost", "TotalConvValue", "BidAdj", "DefaultMaxCPC"};
		String[] typeTime = {"Day"};
		String[] typeBoolean = {"IsRestricting"};
		String[] modeNull = {"MaxCPC", "ConversionOptimizerBidType", "DefaultMaxCPC", "LabelIDs", "Labels", "BidAdj"};
		
		for(String s:headerRow){
			
			String name = s;
			String type = "STRING";
			String mode = "REQUIRED";
			
			if(Arrays.asList(typeInt).contains(s)){
				type = "INTEGER";
			} else if(Arrays.asList(typeFloat).contains(s)){
				type = "FLOAT";
			} else if(Arrays.asList(typeTime).contains(s)){
				type = "TIMESTAMP";
			} else if(Arrays.asList(typeBoolean).contains(s)){
				type = "BOOLEAN";
			}
			
			if(Arrays.asList(modeNull).contains(s)){
				mode = null;
			}
			
			scheme.add(new ColumnProperties(name, type, mode));
			
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String output = gson.toJson(new Schema(scheme));
		//System.out.println(output);
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(outputSchemeFile), "UTF-8");
		writer.write(output);
		writer.close();
		System.out.println("generateBQHeaderScheme() succeeded for " + outputSchemeFile);
	}

}
