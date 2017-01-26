import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
*  This class is to rank all documents with a non-0 score
*  using the class ScoringFunction and print the result to a file.
*/
public class Rank {
	private ScoringFunction sf;
	private int numberOfDocuments;
	
	/**
	 *  Construct a new Rank
	 *  @param fileName is the given file we want to deal with
	*/
	Rank(String fileName) {
		sf = new ScoringFunction(fileName);
		numberOfDocuments = sf.getNumberOfDocuments();
	}
	
	/**
	 *  print the rank for the given query to a file
	 *  @param query is the free-text query
	*/
	void printRankedResultList(String query) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		Map<Double, List<Integer>> resultMap = new HashMap<>();
		
		for (int index = 1; index <= numberOfDocuments; index++) {
			double result = sf.getScore(query, index);
			if (result == 0) {  // filtered doc
				continue;
			}
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
			fw = new FileWriter("Ranked Result list for " + query + ".txt");
			bw = new BufferedWriter(fw);
			
			bw.write("Rank	docID	score");
			bw.newLine();
			
			int rank = 1;
			for (Double score : scores) {
				List<Integer> list = resultMap.get(score);
				for (int docID : list) {
					String line = rank + "	" + docID + "	" + score;
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
	*  This class is the comparator class for descending order
	*  which override the compare() method.
	*/
	private class DoubleComparator implements Comparator<Double> {
		@Override
		public int compare(Double d1, Double d2) {
			return d2.compareTo(d1);
		}
	}
}
