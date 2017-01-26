import java.util.Comparator;

/**
	*  This class is the comparator class for descending order
	*  which override the compare() method.
	*/
	class DoubleComparator implements Comparator<Double> {
		@Override
		public int compare(Double d1, Double d2) {
			return d2.compareTo(d1);
		}
	}