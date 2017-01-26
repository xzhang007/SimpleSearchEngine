package invertedindex;

import java.util.List;
import java.util.LinkedList;

/**
*  This class is to define the class for the PositionList.
*  Because I use [docID | tf | position list] as each list in the posting list.
*/
public class PositionList {
	private int docID;
	private int tf = 1;
	private List<Integer> positionList;
	
	/**
	 *  Construct a new PositionList
	*/
	PositionList() {
		positionList = new LinkedList<Integer>();
	}
	
	/**
	 *  Construct a new PositionList
	 *  @param docID as the the docID
	 *  @param tf is the term frequency
	 *  @param is the position should be the first one in the positionList for that docID
	*/
	PositionList(int docID, int tf, int posId) {
		this();
		this.docID = docID;
		this.tf = tf;
		positionList.add(posId);
	}
	
	/**
	 *  Construct a new PositionList for ScoringFunction
	 *  @param docID as the the docID
	 *  @param tf is the term frequency
	 *  @param positionList as the list in this structure
	*/
	public PositionList(int docID, int tf, List<Integer> positionList) {  // for ScoringFunction
		this();
		this.docID = docID;
		this.tf = tf;
		this.positionList = positionList;
	}
	
	public int getDocID() {
		return docID;
	}
	
	public int getTf() {
		return tf;
	}
	
	public List<Integer> getPositionList() {
		return positionList;
	}
	
	void setTf(int tf) {
		this.tf = tf;
	}
}
