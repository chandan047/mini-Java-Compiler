package visitor;

import java.util.LinkedHashMap;

public class meth 
{
	public String meth_name;
	public int numargs;
	public int maxcall;
	public int stackl;
	public LinkedHashMap<Integer,life> startSort;
	public LinkedHashMap<Integer,life> endSort;
	public LinkedHashMap<String, lablelife> lables;
	
	
	public meth(String _name, int _num) 
	{
		meth_name = _name;
		numargs = _num;
		maxcall = 0;
		stackl = 0;
		startSort = new LinkedHashMap<Integer,life>();
		endSort = new LinkedHashMap<Integer,life>();
		lables = new LinkedHashMap<String,lablelife>();
	}
	
	public String toString() 
	{
		return meth_name + ":\n   " + startSort.toString() + "\n   " + endSort.toString();
	}
	
	
}
