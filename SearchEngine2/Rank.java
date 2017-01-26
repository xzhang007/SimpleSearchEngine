import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import invertedindex.*;

/**
*  This class is to rank all documents with a non-0 score
*  using the class ScoringFunction and print the result to a file.
*/
public class Rank {
	private ScoringFunction sf;
	private int numberOfDocuments;
	private int dr;
	
	/**
	 *  Construct a new Rank
	 *  @param sf is the ScoringFunction reference
	 *  @param numberOfDocuments is the total number of documents
	*/
	Rank(ScoringFunction sf, int numberOfDocuments) {
		this.sf = sf;
		this.numberOfDocuments = numberOfDocuments;
	}
	
	/**
	 *  print the rank for the given query to a file
	 *  @param qryID
	 *  @param query is the free-text query
	*/
	void printRankedResultList(int qryID, String query, int parameterX) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		Map<Double, List<Integer>> resultMap = new HashMap<>();
		
		for (int index = 1; index <= numberOfDocuments; index++) {
			double result = sf.getScore(query, index);
			
			if (resultMap.containsKey(result)) {
				resultMap.get(result).add(index);
			} else {
				List<Integer> list = new ArrayList<>();
				list.add(index);
				resultMap.put(result, list);
			}
		}
		
		Double [] scores = resultMap.keySet().toArray(new Double[1]);
		Arrays.sort(scores, new DoubleComparator());  // in descending order
		
		try {
			fw = new FileWriter("qrels-" + parameterX + ".txt", true);
			bw = new BufferedWriter(fw);
			
			int rank = 1;
			for (Double score : scores) {
				List<Integer> list = resultMap.get(score);
				for (int docID : list) {
					// get the dr
					if (rank == 1) {
						dr = docID;
					}
					String line = qryID + " 0 " + docID + " "+ rank + " " + score + " tfidf";
					bw.write(line);
					bw.newLine();
				}
				rank++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 *  get the relevant docID (rank 1)
	*/
	int getDr() {
		return dr;
	}
}
