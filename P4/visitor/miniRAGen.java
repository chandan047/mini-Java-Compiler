
package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class miniRAGen<R,A> implements GJVisitor<R,A> {
   
	private LinkedHashMap<String,meth> livemap;
	
	private meth currm;
	private String meth;
	private Integer numargs,argnum;
	private boolean callarg = false;
	
	private Integer zero = new Integer(0),one = new Integer(1);
	
	public miniRAGen (LinkedHashMap<String,meth> map){
		livemap = map;
	}
	
   public R visit(NodeList n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeListOptional n, A argu) {
       LinkedList<String> s;
      if ( n.present() ) {
         R _ret=null;
         
         s = new LinkedList<String>();
         
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
             _ret = e.nextElement().accept(this,argu);
             if(callarg) s.add(_ret.toString());
            _count++;
         }
         //System.err.print("&&&&&&&&&&&&&&");
         return (R)s;
      }
      else
         return null;
   }

   public R visit(NodeOptional n, A argu) {
      if ( n.present() ){
         return n.node.accept(this,(A)one);
      }
      else
         return null;
   }

   public R visit(NodeSequence n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeToken n, A argu) { return null; }

   void print(String s) {
		System.out.print(s);
	}
	
	void println(String s) {
		System.out.println(s);
	}
	
	Register getreg(R tmp) {
		return currm.startSort.get(Integer.valueOf((String)tmp)).reg;
	}
	
	Register getreg(String tmp) {
		return currm.startSort.get(Integer.valueOf(tmp)).reg;
	}

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
      
      currm = livemap.get("MAIN");
      
      println("MAIN [" + currm.numargs + "][" + 0 + "][" + currm.maxcall + "]" );
      n.f0.accept(this, argu);
      
      n.f1.accept(this, argu);
      
      println("END");
      n.f2.accept(this, argu);
      
      n.f3.accept(this, argu);
      
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public R visit(StmtList n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public R visit(Procedure n, A argu) {
      R _ret=null;
      
      meth = (String)n.f0.accept(this, (A)zero);
      currm = livemap.get(meth);
      println(meth + "[" + currm.numargs + "][" + (18 + currm.stackl + ((currm.numargs>4)?(currm.numargs-4):0)) + "][" + currm.maxcall + "]");
      
      n.f1.accept(this, argu);
      
      //numargs = (Integer)n.f2.accept(this, (A)zero);
      
      n.f3.accept(this, argu);
      
      n.f4.accept(this, argu);
      
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public R visit(Stmt n, A argu) {
      R _ret=null;
      
      n.f0.accept(this, argu);
      
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public R visit(NoOpStmt n, A argu) {
      R _ret=null;
      
      println("\tNOOP");
      n.f0.accept(this, argu);
      
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n, A argu) {
      R _ret=null;
      
      println("\tERROR");
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public R visit(CJumpStmt n, A argu) {
      R _ret=null;
      
      print("\tCJUMP ");
      n.f0.accept(this, argu);
      
      n.f1.accept(this, (A)one);
      
      n.f2.accept(this, (A)one);
      
      print("\n");
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n, A argu) {
      R _ret=null;
      
      print("\tJUMP ");
      n.f0.accept(this, argu);
      
      n.f1.accept(this, (A)one);
      
      print("\n");
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public R visit(HStoreStmt n, A argu) {
      R _ret=null;
      
      print("\tHSTORE ");
      n.f0.accept(this, argu);
      
      n.f1.accept(this, (A)one);
      
      n.f2.accept(this, (A)one);
      
      n.f3.accept(this, (A)one);
      
      print("\n");
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public R visit(HLoadStmt n, A argu) {
      R _ret=null;
      
      print("\tHLOAD ");
      n.f0.accept(this, argu);
      
      n.f1.accept(this, (A)one);
      
      n.f2.accept(this, (A)one);
      
      n.f3.accept(this, (A)one);
      print("\n");
      
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n, A argu) {
      R _ret=null;
      
      String func = null;
      if(n.f2.f0.which==0){
    	  func = (String)n.f2.accept(this, (A)one);
    	  println("\tCALL " + func );
      }
      
      if(n.f2.f0.which==3)
    	  func = (String)n.f2.accept(this, (A)zero);
      
      print("\tMOVE ");
      n.f0.accept(this, argu);
      n.f1.accept(this, (A)one);
      
      if(n.f2.f0.which==0)
    	  print("v0");
      
      if(n.f2.f0.which==3)
    	  print(func);
      
      if(n.f2.f0.which==2 || n.f2.f0.which==1 )
    	  n.f2.accept(this, (A)one);
      
      print("\n");
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n, A argu) {
      R _ret=null;
      
      String s = (String)n.f1.accept(this, (A)zero); 
      
      print("\tPRINT ");
      n.f0.accept(this, argu);
      print(s);
      
      print("\n");
      return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public R visit(Exp n, A argu) {
      R _ret=null;
      return n.f0.accept(this, argu);
      //return _ret;
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public R visit(StmtExp n, A argu) {
      R _ret=null;
      meth m = currm;
      int i = (m.numargs>4)?(m.numargs-4):0;
      int j = 0;
      for(j=0; j<8; j++,i++) {
    	  println("\tASTORE SPILLEDARG " + i +" s" + j);
      }
      for( ; j<18; j++,i++) {
    	  println("\tASTORE SPILLEDARG " + i +" t" + (j-8));
      }
      
      for(i=0;i< ((m.numargs>4)?4:m.numargs); i++ ) {
    	  println("\tMOVE s" + i + " a" + i);
      }
      for(;i<m.numargs;i++) {
    	  println("\tALOAD s" + i + " SPILLEDARG " + (i-4));
      }
      
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      
      println("\tMOVE v0 " + n.f3.accept(this, (A)zero));
      
      i = (m.numargs>4)?(m.numargs-4):0;
      for(j=0; j<8; j++,i++) {
    	  println("\tALOAD s" + j + " SPILLEDARG " + i );
      }
      for( ; j<18; j++,i++) {
    	  println("\tALOAD t" + (j-8) + " SPILLEDARG " + i );
      }
      
      println("END");
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public R visit(Call n, A argu) {//System.err.println("CALL*************");
      R _ret=null;
      n.f0.accept(this, argu);
      String func = (String)n.f1.accept(this, (A)zero);
      //System.err.println("func = " + func);
      n.f2.accept(this, argu);
      callarg = true;
      argnum = 0;
      LinkedList<String> regs = (LinkedList<String>)n.f3.accept(this, (A)one);
      callarg = false;
      
      //print("CALL " + func + " ");
      
      n.f4.accept(this, argu);
      //System.err.println("%%%%%%%%%%%%%%%%%%%%%%"+regs);
      return (R)func;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n, A argu) {
      R _ret=null;
      print("HALLOCATE ");
      n.f0.accept(this, argu);
      n.f1.accept(this, (A)one);
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, (A)one);
      n.f2.accept(this, (A)one);
      return _ret;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public R visit(Operator n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      print(n.f0.choice.toString() + " ");
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n, A argu) {
      R _ret=null;
      return n.f0.accept(this, argu);
      //return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public R visit(Temp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      if(((Integer)argu).equals(one)){//System.err.println(11111111);
    	  Register r = getreg(n.f1.accept(this,(A)zero));
    	  if(callarg){
    		  if(argnum<4)
    			  println("\tMOVE " + "a" + argnum + " " + r);
    		  else
    			  println("\tPASSARG " + (argnum-3) + " " + r);
    		  argnum++;
    		  return (R)r;
    	  }
    	  else
    		  print(r +" ");
    	  return _ret;
      }
      else if(((Integer)argu).equals(zero)) {
    	  Register r = getreg(n.f1.accept(this, (A)zero));
    	  if(r == Register.zz) {
    		  println("\tALOAD v1 SPILLEDARG " + ( (Integer.valueOf((String)n.f1.accept(this,(A)zero))) - 4 ) );
    		  return (R)"v1";
    	  }
    	  return (R)r.name();
      }
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      if(((Integer)argu).equals(one)){
    	  print(n.f0.toString()+ " ");
    	  return _ret;
      }
      else if(((Integer)argu).equals(zero)){
    	  //System.err.println("+++++++++"+n.f0.toString());
    	  return (R)n.f0.toString();
      }
      
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      if(argu==null)
    	  return (R)n.f0.toString();
      if(((Integer)argu).equals(one)){
    	  print(n.f0.toString() + " ");
    	  return _ret;
      }
      else
    	  return (R)n.f0.toString();
      
   }

}
