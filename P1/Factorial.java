class Factorial{
    public static void main(String[] a){
        System.out.println(new Fac().ComputeFac(10));
    }
}

class Fac extends fact {
	int z;
    public int ComputeFc(int num){
        int num_aux ;
        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        return num_aux ;
    }
}

class fact extends face{
	int x;
	int y;
	public int ComputeFac(int num){
		int a;
		a = 0;
		return a;
	}
}

class face {
	int x;
	public int Compute(int num,boolean k){
		fact x;
		int a;
		int b;
		a = 0;b = 0;
		x = new fact();
		return a+b;
	}
}
