
public class Test {
	public static void main(String [] args) {
		Rank rank = new Rank("documents.txt");
		String query = "tablet";
		rank.printRankedResultList(query);
	}
}
