package visitor;

public enum Register {
	s0("s",0), s1("s",1), s2("s",2), s3("s",3), s4("s",4), s5("s",5), s6("s",6), s7("s",7),	// callee-saved registers
	t0("t",0), t1("t",1), t2("t",2), t3("t",3), t4("t",4), t5("t",5), t6("t",6), t7("t",7), t8("t",8), t9("t",9),	// caller-saved registers
	a0("a",0), a1("a",1), a2("a",2), a3("a",3),		// arguments reserved registers
	zz("z",-1);
	
	private String reg_type;
	private int reg_id;
	
	Register(String type, int id) {
		reg_type = type;
		reg_id = id;
	}
	
}

