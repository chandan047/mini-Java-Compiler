package visitor;
import syntaxtree.*;
import java.util.*;

public class livenessgen<R,A> implements GJVisitor<R,A> {
	
	public LinkedHashMap<String,meth> livenessMap = new LinkedHashMap<String,meth>();
	
	private int NUM = 9;
	private LinkedList<Register> free = new LinkedList<Register>();
	private TreeSet<life> active = new TreeSet<life>(new end_comparator());
	
	private boolean lst=false;
	
	private String currmeth;
	private int offset = 1;
	
	void print(String s) {
		System.out.print(s);
	}
	
	void println(String s) {
		System.out.println(s);
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
	  int _count=0;
	  
      if ( n.present() ) {
         R _ret=null;
         
         Integer flag = (Integer)argu;
         if(flag==1) offset = 0 ;
         
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            if(flag==1) offset = _count + 1;
            e.nextElement().accept(this,argu);
            _count++;
         }
         offset++;
      }
      
      return (R)Integer.valueOf(_count);
   }

   public R visit(NodeOptional n, A argu) {
      if ( n.present() ){
    	  String lable = (String)n.node.accept(this,argu);
    	  if(!livenessMap.get(currmeth).lables.containsKey(lable))
    		  livenessMap.get(currmeth).lables.put((String)n.node.accept(this,argu), new lablelife(lable,offset,offset));
    	  else
    		  livenessMap.get(currmeth).lables.get(lable).start_point = offset;
    	  livenessMap.get(currmeth).lables.get(lable).flag = true;
         return n.node.accept(this,argu);
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

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      currmeth = "MAIN";
      livenessMap.put(currmeth, new meth(currmeth,0));
      n.f1.accept(this, (A)new Integer(1));
      n.f2.accept(this, argu);
      n.f3.accept(this, (A)new Integer(-1));
      n.f4.accept(this, argu);
      
      adjustlife();
      
      reg_alloc();
      
      //println(livenessMap.toString());
      //System.err.println(livenessMap.get("LL_Start").startSort.get(46).reg.toString());
      
      return _ret;
   }
   
   private void adjustlife(){
	   
	   Iterator<String> it = livenessMap.keySet().iterator();
	   while(it.hasNext()) {
		   //it.next();
		   currmeth = it.next();
		   //System.err.println(livenessMap.get(currmeth).startSort.toString());
	   
		   Iterator<life> lifes = livenessMap.get(currmeth).startSort.values().iterator();
		   while(lifes.hasNext()) {
			   life l = lifes.next();
			   
			   Iterator<lablelife> llifes = livenessMap.get(currmeth).lables.values().iterator();
			   while(llifes.hasNext()) {
				   lablelife ll = llifes.next();
				   if(ll.start_point < ll.end_point) {
					   if(l.start_point <= ll.start_point)
						   if(l.end_point >= ll.start_point && l.end_point < ll.end_point){
							   l.end_point = ll.end_point;
						   }
				   }
				   else {
					   if(l.end_point >= ll.start_point)
						   if(l.start_point > ll.start_point && l.start_point <= ll.end_point){
							   l.start_point = ll.start_point;
							   //System.err.println("$$$$$$$$$$$$$$");
						   }
				   }
			   }
		   }
	   }
   }
   
   private void reg_alloc() {
	   
	   Iterator<String> _methods = livenessMap.keySet().iterator();
	   
	   while(_methods.hasNext()) {
		   meth method = livenessMap.get(_methods.next());
		   
		   free.clear();
		   free.add(Register.t0);
		   free.add(Register.t1);
		   free.add(Register.t2);
		   free.add(Register.t3);
		   free.add(Register.t4);
		   free.add(Register.t5);
		   free.add(Register.t6);
		   free.add(Register.t7);
		   free.add(Register.t8);
		   free.add(Register.t9);
		   
		   active.clear();
		   
		   LinkedList<Integer> __lives = new LinkedList<Integer>();
		   Iterator<Integer> it = method.startSort.keySet().iterator();
		   while ( it.hasNext() ) {
			   Integer _in = it.next();
			   if(_in>=20) __lives.add(_in);
		   }
		   
		   Iterator<Integer> _lives = __lives.iterator();
	   
		   while( _lives.hasNext()) {
			   Integer id = _lives.next();
			   expireoldlife(method.startSort.get(id));
			   if( active.size() == NUM) 
				   spillAtInterval(method.startSort.get(id));
			   else {
				   method.startSort.get(id).reg = free.pop();
				   active.add(method.startSort.get(id));
			   }
		   }
		   
	   }
	   
   }
   
   
   private void expireoldlife(life l) {
	   while ( active.size()>=1 ) {
		   life lf = active.first();
		   if ( lf.end_point >= l.start_point ) 
			   return;
		   active.remove(lf);
		   free.push(lf.reg);
	   }
   }
   
   private void spillAtInterval(life l) {
	   life spill = active.last();
	   if (spill.end_point > l.end_point) {
		   l.reg = spill.reg;
		   spill.sloc = livenessMap.get(currmeth).stackl++;
		   active.remove(spill);
		   active.add(l);
	   }
	   else {
		   l.sloc = livenessMap.get(currmeth).stackl++;
	   }
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public R visit(StmtList n, A argu) {
      R _ret=null;
      lst = true;
      n.f0.accept(this, argu);
      lst = false;
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
      currmeth = (String)n.f0.accept(this, argu);
      
      n.f1.accept(this, argu);
      int numargs = Integer.valueOf(((String)n.f2.accept(this, argu)));

      livenessMap.put(currmeth, new meth(currmeth,numargs));
      
      for(int i=0; i<numargs; i++) {
    	  life lf = new life(i,0,0);
    	  lf.reg = Register.valueOf("s" + i);
    	  livenessMap.get(currmeth).startSort.put(i, lf);
    	  livenessMap.get(currmeth).endSort.put(i, lf);
      }
      
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
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n, A argu) {
      R _ret=null;
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
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String l = (String)n.f2.accept(this, argu);
      
      if(!livenessMap.get(currmeth).lables.containsKey(l)){
    	  livenessMap.get(currmeth).lables.put(l, new lablelife(l,offset,offset));
      }
      else{
    	  if( livenessMap.get(currmeth).lables.get(l).flag )
    		  livenessMap.get(currmeth).lables.get(l).end_point = offset;
      }
      
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String l =(String)n.f1.accept(this, argu);
      
      if(!livenessMap.get(currmeth).lables.containsKey(l)){
    	  livenessMap.get(currmeth).lables.put(l, new lablelife(l,offset,offset));
      }
      else{
    	  if( livenessMap.get(currmeth).lables.get(l).flag )
    		  livenessMap.get(currmeth).lables.get(l).end_point = offset;
      }
      
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
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
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
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
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
      n.f0.accept(this, argu);
      return _ret;
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
      n.f0.accept(this, argu);
      n.f1.accept(this, (A)new Integer(1));
      n.f2.accept(this, argu);
      n.f3.accept(this, (A)new Integer(2));
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
   public R visit(Call n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      
      Integer num = (Integer)n.f3.accept(this, (A)new Integer(-1));
      
      if(livenessMap.get(currmeth).maxcall<num){
    	  livenessMap.get(currmeth).maxcall = num;
      }
      
      n.f4.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
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
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
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
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public R visit(Temp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      Integer id = Integer.valueOf((String)n.f1.accept(this, argu));
      
      if(!livenessMap.get(currmeth).startSort.containsKey(id)) {
    	  life newlf = new life(id,offset,1);
    	  livenessMap.get(currmeth).startSort.put(id, newlf);
    	  livenessMap.get(currmeth).endSort.put(id, newlf);
      }
      
      if(livenessMap.get(currmeth).endSort.containsKey(id)) {
    	  life lf = livenessMap.get(currmeth).endSort.get(id);
    	  livenessMap.get(currmeth).endSort.remove(id);
    	  livenessMap.get(currmeth).endSort.put(id, lf);
      }
      
      livenessMap.get(currmeth).startSort.get(id).end_point = offset;
      if((Integer)argu==2) 
    	  livenessMap.get(currmeth).startSort.get(id).status = 2;
      
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return (R)n.f0.toString();
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return (R)n.f0.toString();
   }

}
