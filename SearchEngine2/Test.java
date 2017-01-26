import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import invertedindex.*;
import java.util.Map;
import java.util.HashMap;

public class Test {
	private static Map<Integer, String> queryMap = new HashMap<>();
	//private static Map<Integer, Integer> drMap = new HashMap<>();
	
	public static void main(String [] args) {
		// get the query map
		String queryFile = "queries.xml";
		getQueryMap(queryFile);
		
		// tokenize the given file and get the inverted index (map) and the number of documents
		String fileName = "documents.txt";
		Tokenizer tnz = new Tokenizer();
		tnz.tokenize(fileName);
		Map<String, FreqAndLists> map = tnz.getMap();
		int numberOfDocuments = tnz.getNumberOfDocuments();
		
		// computer the score, rank and expand the query and rank again
		int parameterX = 5;
		ScoringFunction sf = new ScoringFunction(map, numberOfDocuments);
		Rank rank = new Rank(sf, numberOfDocuments);
		
		for (Map.Entry<Integer, String> entry : queryMap.entrySet()) {
			int qryID = entry.getKey();
			String originalQuery = entry.getValue();
			rank.printRankedResultList(qryID, originalQuery, 0);
			
			int dr = rank.getDr();
			QueryExpansion queryExpansion = new QueryExpansion(map, sf, dr);
			String expandedQuery = queryExpansion.getExpandedQuery(parameterX, originalQuery);
			rank.printRankedResultList(qryID, expandedQuery, parameterX);
		}
	}
	
	/**
	 *  to get the query map by reading the query.xml
	 *  @param queryFile is the name of query file (such as "query.xml")
	 *  @return the query map (<qryID, original query> for each entry)
	*/
	static private Map<Integer, String> getQueryMap(String queryFile) {
		FileReader fr = null;
		BufferedReader br = null;
		Pattern pattern = Pattern.compile("\\<query\\>\\<number\\>(\\d+)\\<\\/number\\>"
				                        + "\\<text\\>(\\w+)\\<\\/text\\>\\<narrative\\>"
				                        + "(\\w+\\s)*(\\w+)\\<\\/narrative\\>\\<\\/query\\>");
		
		try {
			fr = new FileReader(queryFile);
			br = new BufferedReader(fr);
			
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				
				Matcher matcher = pattern.matcher(line);
				
				int qryID;
				String query;
				if (matcher.matches()) {
					qryID = Integer.parseInt(matcher.group(1));
					query = Modifier.modify(matcher.group(2));
					queryMap.put(qryID, query);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return queryMap;
	}
}
