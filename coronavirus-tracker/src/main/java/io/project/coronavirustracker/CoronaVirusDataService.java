package io.project.coronavirustracker;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CoronaVirusDataService{
		private static String Virus_Data_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
		private List<LocationStats> allStats = new ArrayList<>();
		
		public List<LocationStats> getAllStats() {
			return allStats;
		}
		public void setAllStats(List<LocationStats> allStats) {
			this.allStats = allStats;
		}
		@PostConstruct
		@Scheduled(cron = "* * * 5 * *")
		public void fetchVirusData() throws IOException, InterruptedException {
			List<LocationStats> newStats = new ArrayList<>(); // Creating a list of type LocationStats
			HttpClient client = HttpClient.newHttpClient(); // HttpClient --> creating new instance of HttpClient
		HttpRequest request = HttpRequest.newBuilder() // HttpRequest --> ceating new instance of HttpRequest
			.uri(URI.create(Virus_Data_URL)) // fetching the virus data URL
			.build(); // assigning build to --> request
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); // sending the data from CSV to toString()
		StringReader csvBodyReader = new StringReader(httpResponse.body());// StringReader --> converts String to Character Stream
		System.out.println("__________________******************HttpResponseBody***************_______________");
		System.out.println(httpResponse.body());
		@SuppressWarnings("deprecation")
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
		    locationStat.setState(record.get("Province/State"));
		    locationStat.setCountry(record.get("Country/Region"));
		    locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		    int latestCases = Integer.parseInt(record.get(record.size()-1));
		    int prevDayCases = Integer.parseInt(record.get(record.size()-2));
		    locationStat.setLatestTotalCases(latestCases);
		    locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
		    System.out.println(locationStat);
		    newStats.add(locationStat);
		}
		this.allStats = newStats;
		
	}
}
