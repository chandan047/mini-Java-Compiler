package visitor;

import java.util.Comparator;

public class end_comparator implements Comparator<life> {

	@Override
	public int compare(life o1, life o2) {
		return o1.compareTo(o2);
	}
	
}
