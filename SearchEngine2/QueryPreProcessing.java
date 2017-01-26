import invertedindex.Modifier;

/**
*  This class is to help to deal with to pre-processing the query
*/
class QueryPreProcessing {
	/**
	 *  given a query, this method could pre-processing it and return an array of
	 *  terms only contain normal token and proximity operator such as n(p1 p2).
	 *  @param the free-text query
	 *  @return an array of terms only contain normal token and proximity operator such as n(p1 p2)
	*/
	static String [] getTerms(String query) {
		String [] terms = null;
		String [] strs = query.split("\\s+");
		
		// stpe1: query should be divided into 2 parts: normal terms and n(p1 p2) proximity operator 
		StringBuffer strBuffer1 = new StringBuffer();  // for not proximity operator
		StringBuffer strBuffer2 = new StringBuffer();  // for proximity operator
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].matches("\\d+\\(\\w+")) {  // proximity operator n(phrase1 phrase2)
				strs[i] += " " + strs[i+1];
				strBuffer2.append(strs[i++] + "  ");  // 2 spaces
				continue;
			}
			strBuffer1.append(strs[i] + "  "); // 2 spaces
		}
		
		// step2: modify(stemming and so on) the normal terms, and combine both of them the String array terms
		if (strBuffer1.length() == 0 && strBuffer2.length() != 0) {  // query only contains proximity operator
			terms = strBuffer2.toString().split("\\s{2}");
		} else if (strBuffer1.length() != 0 && strBuffer2.length() == 0) {  // query only contains normal terms
			// modify the terms
			terms = Modifier.modify(strBuffer1.toString().split("\\s{2}"));
		} else if (strBuffer1.length() != 0 && strBuffer2.length() != 0) {  // query contains normal terms and proximity operator form as well
			// modify the terms
			String [] normalTerms = Modifier.modify(strBuffer1.toString().split("\\s{2}"));
			StringBuffer strBuffer3 = new StringBuffer();
			for (String str : normalTerms) {
				strBuffer3.append(str + "  ");  // 2 spaces
			}
			strBuffer3.append(strBuffer2);
			terms = strBuffer3.toString().split("\\s{2}");
		}
		
		/*for (String term : terms) {
			System.out.println("111 " + term);
		}*/
		
		return terms;
	}
}
