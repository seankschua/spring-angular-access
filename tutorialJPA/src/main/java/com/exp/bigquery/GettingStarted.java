package com.exp.bigquery;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationLoad;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.common.base.Joiner;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.joda.time.DateTime;


/**
 * Example of authorizing with Bigquery and reading from a public dataset.
 *
 * Specifically, this queries the shakespeare dataset to fetch the 10 of Shakespeare's works with
 * the greatest number of distinct words.
 */
public class GettingStarted {
  /**
   * Creates an authorized Bigquery client service using Application Default Credentials.
   *
   * @return an authorized Bigquery client
   * @throws IOException if there's an error getting the default credentials.
   */
  public static Bigquery createAuthorizedClient() throws IOException {
    // Create the credential
    HttpTransport transport = new NetHttpTransport();
    JsonFactory jsonFactory = new JacksonFactory();
    GoogleCredential credential = GoogleCredential.getApplicationDefault(transport, jsonFactory);

    // Depending on the environment that provides the default credentials (e.g. Compute Engine, App
    // Engine), the credentials may require us to specify the scopes we need explicitly.
    // Check for this case, and inject the Bigquery scope if required.
    if (credential.createScopedRequired()) {
      credential = credential.createScoped(BigqueryScopes.all());
    }

    return new Bigquery.Builder(transport, jsonFactory, credential)
        .setApplicationName("Bigquery Samples").build();
  }

  /**
   * Executes the given query synchronously.
   *
   * @param querySql the query to execute.
   * @param bigquery the Bigquery service object.
   * @param projectId the id of the project under which to run the query.
   * @return a list of the results of the query.
   * @throws IOException if there's an error communicating with the API.
   */
  private static List<TableRow> executeQuery(String querySql, Bigquery bigquery, String projectId)
      throws IOException {
    QueryResponse query = bigquery.jobs().query(
        projectId,
        new QueryRequest().setQuery(querySql))
        .execute();

    // Execute it
    GetQueryResultsResponse queryResult = bigquery.jobs().getQueryResults(
        query.getJobReference().getProjectId(),
        query.getJobReference().getJobId()).execute();

    return queryResult.getRows();
  }

  /**
   * Prints the results to standard out.
   *
   * @param rows the rows to print.
   */
  private static void printResults(List<TableRow> rows) {
    System.out.print("\nQuery Results:\n------------\n");
    for (TableRow row : rows) {
      for (TableCell field : row.getF()) {
        System.out.printf("%-50s", field.getV());
      }
      System.out.println();
    }
  }

  /**
   * Exercises the methods defined in this class.
   *
   * In particular, it creates an authorized Bigquery service object using Application Default
   * Credentials, then executes a query against the public Shakespeare dataset and prints out the
   * results.
   *
   * @param args the first argument, if it exists, should be the id of the project to run the test
   *     under. If no arguments are given, it will prompt for it.
   * @throws IOException if there's an error communicating with the API.
   */
  
  public static final String TABLE_ID = "expedia_sem";
  
  public static void main(String[] args) throws IOException {
    
    String projectId = BigQueryInstalledAuthDemo.PROJECT_NUMBER;
    String storedRefreshToken = BigQueryInstalledAuthDemo.loadRefreshToken();
    //this doesn't check if the refresh token exists...use the Demo class to generate it.
    
    GoogleCredential credential = BigQueryInstalledAuthDemo.createCredentialWithRefreshToken(
    		BigQueryInstalledAuthDemo.HTTP_TRANSPORT, BigQueryInstalledAuthDemo.JSON_FACTORY, new TokenResponse().setRefreshToken(storedRefreshToken));
        credential.refreshToken();

    Bigquery bigquery = BigQueryInstalledAuthDemo.buildService(credential);

    List<TableRow> rows = executeQuery("SELECT count(*) FROM [expedia_sem.adgroup]", bigquery, projectId);

    printResults(rows);
    
    insertRows(BigQueryInstalledAuthDemo.PROJECT_NUMBER,TABLE_ID,"adgroup",
    		new FileContent(MediaType.OCTET_STREAM.toString(), new File("src/main/resources/adwords/adgroup_625-898-2657_20151026_20151027_report_processed.csv")),bigquery);
    
    rows = executeQuery("SELECT count(*) FROM [expedia_sem.adgroup]", bigquery, projectId);

    printResults(rows);
    
  }
  
  public static void insertRows(String projectId, 
          String datasetId, 
          String tableId, 
          AbstractInputStreamContent data, Bigquery bigquery) {
	try {

		// Table reference
		TableReference tableReference = new TableReference()
		   .setProjectId(projectId)
		   .setDatasetId(datasetId)
		   .setTableId(tableId);
		
		// Load job configuration
		JobConfigurationLoad loadConfig = new JobConfigurationLoad()
		   .setDestinationTable(tableReference)
		   // Data in Json format (could be CSV)
		   .setFieldDelimiter("\t")
		   // Table is created if it does not exists
		   .setCreateDisposition("CREATE_IF_NEEDED")
		   // Append data (not override data)
		   .setWriteDisposition("WRITE_APPEND")
		   .setSkipLeadingRows(1);
		// If your data are coming from Google Cloud Storage
		//.setSourceUris(...);
		
		// Load job
		//System.out.println("summer-rope-861:expedia_sem.adgroup".replace(":", "-").replace(".", "-"));
		Job loadJob = new Job()
		   .setJobReference(
		           new JobReference()
		                   .setJobId(Joiner.on("-").join("INSERT", 
		                           tableId, DateTime.now().toString("dd-MM-yyyy_HH-mm-ss-SSS")).replace(":", "-").replace(".", "-"))
		                   .setProjectId(projectId))
		   .setConfiguration(new JobConfiguration().setLoad(loadConfig));
		// Job execution
		Job createTableJob = bigquery.jobs().insert(projectId, loadJob, data).execute();
		// If loading data from Google Cloud Storage
		//createTableJob = bigquery.jobs().insert(projectId, loadJob).execute();
		
		String jobId = createTableJob.getJobReference().getJobId();
		// Wait for job completion
		/*createTableJob = waitForJob(projectId, createTableJob);
		Long rowCount = createTableJob != null ? createTableJob.getStatistics().getLoad().getOutputRows() : 0l;
		log.info("{} rows inserted in table '{}' (dataset: '{}', project: '{}')", rowCount, tableId, datasetId, projectId);
		return rowCount;*/
	}
catch (IOException e) { throw Throwables.propagate(e); }
}

}


