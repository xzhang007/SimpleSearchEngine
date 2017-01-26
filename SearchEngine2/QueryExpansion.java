import invertedindex.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class QueryExpansion {
	private Map<String, FreqAndLists> map = null;
	private int dr;
	private ScoringFunction sf;
	private Map<Double, List<String>> scoreMap = null;
	
	/**
	 *  Construct a new QueryExpansion
	 *  @param map is the inverted index
	 *  @param sf is the ScoringFunction reference
	 *  @param dr is the relevant docID
	*/
	QueryExpansion(Map<String, FreqAndLists> map, ScoringFunction sf, int dr) {
		this.map = map;
		this.sf = sf;
		this.dr = dr;
		scoreMap = new HashMap<Double, List<String>>();
	}
	
	/**
	 *  to get the expanded query
	 *  @param parameterX is the top X parameter
	 *  @param originalQuery is the original query
	 *  @return the expanded query
	*/
	String getExpandedQuery(int parameterX, String originalQuery) {
		String [] terms = getTopX(parameterX, originalQuery);
		StringBuffer strBuffer = new StringBuffer(originalQuery);
		for (String phrase : terms) {
			strBuffer.append(" " + phrase);
		}
		return strBuffer.toString();
	}
	
	/**
	 *  to get the top X terms array
	 *  @param parameterX is the top X parameter
	 *  @param originalQuery is the original query
	 *  @return the top X terms array
	*/
	private String [] getTopX(int parameterX, String originalQuery) {
		getScoreMap();
		Double [] scores = scoreMap.keySet().toArray(new Double[1]);
		Arrays.sort(scores, new DoubleComparator());  // in descending order
		
		StringBuffer strBuffer = new StringBuffer();
		int numberOfTops = 1;
		for (Double score : scores) {
			if (numberOfTops > parameterX) {
				break;
			}
			List<String> list = scoreMap.get(score);
			for (String term : list) {
				if (numberOfTops > parameterX) {
					break;
				}
				if (term.equals(originalQuery)) {  // the top X terms shall not include the original query
					continue;
				}
				strBuffer.append(term + " ");
				numberOfTops++;
			}
		}
		
		return strBuffer.toString().split("\\s");
	}
	
	/**
	 *  to get the score map (which is the scores of all the terms in dr)
	 *  Map.Entry: <score, list of terms in dr>
	 *  @return the score map (which is the scores of all the terms in dr)
	*/
	private Map<Double, List<String>> getScoreMap() {
		for (Map.Entry<String, FreqAndLists> entry: map.entrySet()) {
			String term = entry.getKey();
			List<PositionList> postingList = entry.getValue().getPostingList();
			for (PositionList positionList : postingList) {
				if (positionList.getDocID() != dr) {
					continue;
				}
				double score = sf.getScore(term, dr);
				if (scoreMap.containsKey(score)) {
					scoreMap.get(score).add(term);
				} else {
					List<String> list = new ArrayList<>();
					list.add(term);
					scoreMap.put(score, list);
				}
				break;
			}
		}
		
		/*for (Map.Entry<Double, List<String>> entry : scoreMap.entrySet()) {
			System.out.println("000 " + entry.getKey());
			List<String> list = entry.getValue();
			for (String term : list) {
				System.out.println("        " + term);
			}
		}*/
		
		return scoreMap;
	}
}