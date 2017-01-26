package invertedindex;

import java.util.List;
import java.util.LinkedList;

/**
*  This class is to define the value class for the TreeMap.
*  Because I use the String as the key, and the <doc freq | posting List> as the value
*  in the TreeMap.
*/
public class FreqAndLists {
	private int docFrequence;
	private List<PositionList> postingList;
	
	/**
	 *  Construct a new FreqAndLists
	*/
	FreqAndLists() {
		postingList = new LinkedList<PositionList>();
	}
	
	/**
	 *  Construct a new FreqAndLists
	 *  @param docFrequence as the the doc frequence
	 *  @param docID should be added to the posting list as the first document on the list
	*/
	FreqAndLists(int docFrequence, PositionList positionList) {
		this();
		this.docFrequence = docFrequence;
		postingList.add(positionList);
	}
	
	/**
	 *  Construct a new FreqAndLists for ScoringFunction
	 *  @param docFrequence as the the doc frequence
	 *  @param posingList as the list
	*/
	public FreqAndLists(int docFrequence, List<PositionList> positionList) {  // for ScoringFunction
		this();
		this.docFrequence = docFrequence;
		this.postingList = positionList;
	}
	
	public int getDocFrequence() {
		return docFrequence;
	}
	
	public List<PositionList> getPostingList() {
		return postingList;
	}
	
	void setDocFrequence(int docFrequence) {
		this.docFrequence = docFrequence;
	}
}
