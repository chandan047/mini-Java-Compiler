package visitor;

public class lablelife {
	public String lable;
	public int start_point;
	public int end_point;
	public boolean flag;
	
	public lablelife(String s, int i, int j){
		lable = s;
		this.start_point = i;
		this.end_point = j;
		flag = false;
	}
	
	@Override
	public String toString(){
		return start_point + " : " + end_point;
	}
}
