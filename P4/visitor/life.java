package visitor;

public class life implements Comparable<life> {
	public int ID;
	public Register reg;
	public int start_point;
	public int end_point;
	public int sloc;
	public int status; //0-arg 1-local 2-return
	
	public life(int _id, int _start, int x) {
		ID = _id;
		start_point = _start;
		status = x;
		sloc = -1;
		reg = Register.zz;
	}
	
	public String toString() {
		return "   "+ ID + ":" + start_point + ":" + end_point + "   " + reg + "\n" ;
	}

	@Override
	public int compareTo(life ls) {
		if(ls.ID == this.ID)
			return 0;
		if(ls.end_point < this.end_point)
			return 1;
		return -1;
	}
}
