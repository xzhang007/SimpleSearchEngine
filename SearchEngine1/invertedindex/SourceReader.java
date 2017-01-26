package invertedindex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
*  This class is to help Tokenizer to read the file.
*  Since in the java there might be some "try catch" cases that may throw exceptions 
*  as well as all readers should be closed finally,
*  I define a seperate class to deal with all of these situations, 
*  then in other parts of the programs, we don't need to pay attention to those stuff.
*/
class SourceReader {
	private FileReader fr;
	private BufferedReader br;
	private String line;
	
	/**
	 *  Construct a new SourceReader
	 *  @param fileName the String describing the user's source file
	*/
	SourceReader(String fileName) {
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
		} catch (IOException e) {
			e.printStackTrace();
			closeReaders();
		}
	}
	
	String readLine() {
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			closeReaders();
		}
		
		return line;
	}
	
	void closeReaders() {
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
