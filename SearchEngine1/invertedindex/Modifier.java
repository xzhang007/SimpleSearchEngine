package invertedindex;

import org.lemurproject.kstem.KrovetzStemmer;
import java.util.HashSet;
import java.lang.StringBuffer;

/**
*  This class is to modify (case normaloziation, stemming, remove stop words) 
*  for the given term or term array.
*/
public class Modifier {
	private static KrovetzStemmer kStemmer = new KrovetzStemmer();;
	private static final HashSet<String> STOPWORDS = new HashSet<>();
	
	static {
		STOPWORDS.add("the");
		STOPWORDS.add("is");
		STOPWORDS.add("at");
		STOPWORDS.add("of");
		STOPWORDS.add("on");
		STOPWORDS.add("and");
		STOPWORDS.add("a");
	}
	
	/**
	 *  modify a term (case normalizatin and stemming)
	 *  @param token
	 *  @return token
	*/
	public static String modify(String token) {
		token = token.toLowerCase();   // case normalization
		token = kStemmer.stem(token); // Stemming
		
		return token;
	}
	
	/**
	 *  modify a term array (case normalizatin and stemming as well as removing the stop words)
	 *  @param tokens array
	 *  @return tokens array
	*/
	public static String [] modify(String [] tokens) {
		StringBuffer strs = new StringBuffer();
		for (String str : tokens) {
			str = modify(str);
			// remove stopwords
			/*if (STOPWORDS.contains(str)) {
				continue;
			}*/
			strs.append(str + " ");
		}
		return strs.toString().split("\\s+");
	}
}
