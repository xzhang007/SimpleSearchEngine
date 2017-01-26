import invertedindex.*;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.StringBuffer;

/**
*  This class is to get the score for a given query and docID in tf-idf way
*/
public class ScoringFunction {
	private String [] terms;
	private Tokenizer tnz;
	private Map<String, FreqAndLists> map = null;
	private int numberOfDocuments;
	
	/**
	 *  Construct a new ScoreingFunction
	 *  @param fileName is the given file we want to deal with
	*/
	ScoringFunction(String fileName) {
		tnz = new Tokenizer();
		tnz.tokenize(fileName);
		map = tnz.getMap();
		numberOfDocuments = tnz.getNumberOfDocuments();
	}
	
	/**
	 *  to get the score for a given query and docID
	 *  @param query is the free-text query
	 *  @param docId is the given docID
	 *  @return the double score
	*/
	double getScore(String query, int docID) {
		terms = QueryPreProcessing.getTerms(query);
		
		double score = 0;
		for (String term : terms) {
			if (term.matches("\\d+\\(\\w+\\s\\w+\\)")) {  // for proximity operator
				score += dealWithProximityOperation(term, docID);
			} else {
				score += dealWithNormalTerms(term, docID);
			}
		}
		
		return score;
	}
	
	/**
	 *  private method to deal with the proximity term such as n(p1 p2)
	 *  @param term as n(p1 p2)
	 *  @param docID
	 *  @return the double score for term n(p1 p2) in docID
	*/
	private double dealWithProximityOperation(String term, int docID) {
		FreqAndLists fl = getFreqAndLists(term);  // get the FreqAndLists structure for the combine phrases
		// filtering
		if (fl == null || getRawTf(fl, docID) == 0) {  // documents do not contain the term or the term does not show in the given docID
			return 0;
		}
		return getTfIdf(fl, docID);
	}
	
	/**
	 *  private method to deal with the normal terms
	 *  @param term as "repair"
	 *  @param docID
	 *  @return the double score for the term in docID
	*/
	private double dealWithNormalTerms(String term, int docID) {
		// filtering
		if (!map.containsKey(term) || getRawTf(term, docID) == 0) { // documents do not contain the term or the term does not show in the given docID
			return 0;
		}
		return getTfIdf(term, docID);
	}
	
	/**
	 *  private method to extract the phrase1, phrase2 and proximity window to help generating 
	 *  the FreqAndLists for the term n(p1 p2)
	 *  @param term as n(p1 p2)
	 *  @return FreqAndLists
	*/
	private FreqAndLists getFreqAndLists(String term) {  // get the FreqAndLists structure for the combine phrases
		Pattern pattern = Pattern.compile("(\\d+)\\((\\w+)\\s(\\w+)\\)");
		Matcher matcher = pattern.matcher(term);
		
		int proximityWindow;
		String phrase1, phrase2;
		FreqAndLists fl = null;
		if (matcher.matches()) {
			proximityWindow = Integer.parseInt(matcher.group(1));
			phrase1 = Modifier.modify(matcher.group(2)); // case normalization & stemming
			phrase2 = Modifier.modify(matcher.group(3));  // case normalization & stemming
			fl = getPositionalInvertedIndexForProximity(phrase1, phrase2, proximityWindow);
		}
		
		return fl;
	}
	
	/**
	 *  private method to generate the FreqAndLists 
	 *  (or we can say the inverted index) for the term n(p1 p2)
	 *  @param phrase1 in n(p1 p2)
	 *  @param phrase2 in n(p1 p2)
	 *  @param proximityWindow is the n in n(p1 p2)
	 *  @return the FreqAndLists (or the inverted index) for the term n(p1 p2) 
	*/
	private FreqAndLists getPositionalInvertedIndexForProximity(String phrase1, String phrase2, int proximityWindow) {  // generate a FreqAndLists structure for the combine phrases
		if (!map.containsKey(phrase1) || !map.containsKey(phrase2)) {
			return null;
		}
		List<PositionList> postingList1 = map.get(phrase1).getPostingList();
		List<PositionList> postingList2 = map.get(phrase2).getPostingList();
		
		// Positional Intersect
		List<PositionList> postingList = new LinkedList<>();  // for the result
		int i = 0, j = 0;
		while (i < postingList1.size() && j < postingList2.size()) {
			PositionList list1 = postingList1.get(i);
			PositionList list2 = postingList2.get(j);
			int docID1 = list1.getDocID();
			int docID2 = list2.getDocID();
			if (docID1 == docID2) {
				List<Integer> l1 = list1.getPositionList();
				List<Integer> l2 = list2.getPositionList();
				List<Integer> list = new LinkedList<>(); // for the result
				int k = 0, l = 0;
				while (k < l1.size() && l < l2.size()) {
					int pos1 = l1.get(k);
					int pos2 = l2.get(l);
					if (pos1 < pos2 && pos2 - pos1 <= proximityWindow + 1) {
						list.add(pos1);
						k++;
						while (k < l1.size() && l1.get(k) < pos2) {
							k++;
						}
						l++;
					} else if (pos1 < pos2) {
						k++;
					} else {  // pos1 > pos2
						l++;
					}
				}
				if (list.size() > 0) {  // important, no match on that docID
					postingList.add(new PositionList(docID1, list.size(), list));
				}
				i++;
				j++;
			} else if (docID1 < docID2) {
				i++;
			} else {
				j++;
			}
		}
		
		if (postingList.size() == 0) {  // important, no match on all documents
			return null;
		}
		return new FreqAndLists(postingList.size(), postingList);
	}
	
	/**
	 *  private method to the tf-wt for normal term
	 *  @param normal term
	 *  @param docID
	 *  @return the double tf-wt
	*/
	private double getTfIdf(String term, int docID) {
		double idf = getIdf(term);
		return getTf(term, docID) * idf;
	}
	
	/**
	 *  private method to get the tf-wt for term n(p1 p2)
	 *  @param FreqAndLists we just generate
	 *  @param docID
	 *  @return the double tf-wt
	*/
	private double getTfIdf(FreqAndLists fl, int docID) {  // for proximity operator
		double idf = getIdf(fl);
		return getTf(fl, docID) * idf;
	}
	
	/**
	 *  private method to get idf for normal term
	 *  @param normal term
	 *  @return idf
	*/
	private double getIdf(String term) {
		int rawDf = getRawDf(term);
		return Math.log10(numberOfDocuments * 1.0 / rawDf);
	}
	
	/**
	 *  private method to get idf for term n(p1 p2)
	 *  @param FreqAndLists we just generate
	 *  @return idf
	*/
	private double getIdf(FreqAndLists fl) {  // for proximity operator
		int rawDf = getRawDf(fl);
		return Math.log10(numberOfDocuments * 1.0 / rawDf);
	}
	
	/**
	 *  private method to get log tf for normal term
	 *  @param normal term
	 *  @param docID
	 *  @return log tf
	*/
	private double getTf(String term, int docID) {
		int rawTf = getRawTf(term, docID);
		return 1 + Math.log10(rawTf * 1.0);
	}
	
	/**
	 *  private method to get log tf for n(p1 p2)
	 *  @param FreqAndLists we just generate
	 *  @param docID
	 *  @return log tf
	*/
	private double getTf(FreqAndLists fl, int docID) {  // for proximity operator
		int rawTf = getRawTf(fl, docID);
		return 1 + Math.log10(rawTf * 1.0);
	}
	
	/**
	 *  private method to get raw df for normal term
	 *  @param normal term
	 *  @return raw df
	*/
	private int getRawDf(String phrase) {
		FreqAndLists value = map.get(phrase);
		return value.getDocFrequence();
	}
	
	/**
	 *  private method to get raw df for n(p1 p2)
	 *  @param FreqAndLists we just generate
	 *  @return raw df
	*/
	private int getRawDf(FreqAndLists fl) {  // for proximity operator
		return fl.getDocFrequence();
	}
	
	/**
	 *  private method to get raw tf for normal terms in the given docID
	 *  @param phrase is the term
	 *  @param docID
	 *  @return raw tf
	*/
	private int getRawTf(String phrase, int docID) {
		FreqAndLists value = map.get(phrase);
		List<PositionList> positionList = value.getPostingList();
		for (PositionList list : positionList) {
			if (list.getDocID() == docID) {
				return list.getTf();
			}
		}
		return 0;  // the phrase can't be found in the given docID
	}
	
	/**
	 *  private method to get raw tf for n(p1 p2) in the given docID
	 *  @param FreqAndLists we just generate
	 *  @param docID
	 *  @return raw tf
	*/
	private int getRawTf(FreqAndLists fl, int docID) {  // for proximity operator
		List<PositionList> positionList = fl.getPostingList();
		for (PositionList list : positionList) {
			if (list.getDocID() == docID) {
				return list.getTf();
			}
		}
		return 0;  // the term can't be found in the given docID
	}
	
	/**
	 *  get the number of Documents in the given file
	*/
	int getNumberOfDocuments() {
		return numberOfDocuments;
	}
}
