package visitor;

import java.util.*;

import syntaxtree.*;

public class TypeCheckVisitor<R> implements GJNoArguVisitor<R> {

	private LinkedHashMap<String, LinkedHashMap<String, FunctionSignature>> funcMap;
	private LinkedHashMap<String,LinkedHashMap<String,String>> classvarMap;
	LinkedHashMap<String, String> extendsMap;
	private String className,functionName;
	private boolean inMeth,inClass,inMainClass=false,inNaming;
	List<String> methParams;
	
	public TypeCheckVisitor(LinkedHashMap<String, LinkedHashMap<String,FunctionSignature>> map,
			LinkedHashMap<String,LinkedHashMap<String,String>> classvariables,
			LinkedHashMap<String, String> extendsMap) {
		this.funcMap = map;
		this.classvarMap = classvariables;
		this.extendsMap = extendsMap;
	}
	
	public R visit(NodeList n) {
	      R _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this);
	         _count++;
	      }
	      return _ret;
		}

	   public R visit(NodeListOptional n) {
	      if ( n.present() ) {
	         R _ret=null;
	         int _count=0;
	         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	            e.nextElement().accept(this);
	            _count++;
	         }
	         return _ret;
	      }
	      else
	         return null;
	   }

	   public R visit(NodeOptional n) {
	      if ( n.present() )
	         return n.node.accept(this);
	      else
	         return null;
	   }

	   public R visit(NodeSequence n) {
	      R _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this);
	         _count++;
	      }
	      return _ret;
	   }

	   public R visit(NodeToken n) { return null; }

	   //
	   // User-generated visitor methods below
	   //

	   /**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
	   public R visit(Goal n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> "public"
	    * f4 -> "static"
	    * f5 -> "void"
	    * f6 -> "main"
	    * f7 -> "("
	    * f8 -> "String"
	    * f9 -> "["
	    * f10 -> "]"
	    * f11 -> Identifier()
	    * f12 -> ")"
	    * f13 -> "{"
	    * f14 -> PrintStatement()
	    * f15 -> "}"
	    * f16 -> "}"
	    */
	   public R visit(MainClass n) {
		   inMainClass = true;
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      n.f5.accept(this);
	      n.f6.accept(this);
	      n.f7.accept(this);
	      n.f8.accept(this);
	      n.f9.accept(this);
	      n.f10.accept(this); inNaming = true;
	      n.f11.accept(this); inNaming = false;
	      n.f12.accept(this);
	      n.f13.accept(this);
	      n.f14.accept(this);
	      n.f15.accept(this);
	      n.f16.accept(this);
	      inMainClass = false;
	      return _ret;
	   }

	   /**
	    * f0 -> ClassDeclaration()
	    *       | ClassExtendsDeclaration()
	    */
	   public R visit(TypeDeclaration n) {
	      R _ret=null;
	      n.f0.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> ( VarDeclaration() )*
	    * f4 -> ( MethodDeclaration() )*
	    * f5 -> "}"
	    */
	   public R visit(ClassDeclaration n) {
		   inClass = true;
	      R _ret=null;
	      n.f0.accept(this); inNaming = true;
	      className = (String) n.f1.accept(this); inNaming = false;
	      n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      n.f5.accept(this);
	      inClass = false;
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "extends"
	    * f3 -> Identifier()
	    * f4 -> "{"
	    * f5 -> ( VarDeclaration() )*
	    * f6 -> ( MethodDeclaration() )*
	    * f7 -> "}"
	    */
	   public R visit(ClassExtendsDeclaration n) {
		   inClass = true;
	      R _ret=null;
	      n.f0.accept(this); inNaming = true;
	      className = (String) n.f1.accept(this);
	      n.f2.accept(this);
	      n.f3.accept(this); inNaming = false;
	      n.f4.accept(this);
	      n.f5.accept(this);
	      n.f6.accept(this);
	      n.f7.accept(this);
	      inClass = false;
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	   public R visit(VarDeclaration n) {
	      R _ret=null;
	      n.f0.accept(this); inNaming = true;
	      n.f1.accept(this); inNaming = false;
	      n.f2.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> "public"
	    * f1 -> Type()
	    * f2 -> Identifier()
	    * f3 -> "("
	    * f4 -> ( FormalParameterList() )?
	    * f5 -> ")"
	    * f6 -> "{"
	    * f7 -> ( VarDeclaration() )*
	    * f8 -> ( Statement() )*
	    * f9 -> "return"
	    * f10 -> Expression()
	    * f11 -> ";"
	    * f12 -> "}"
	    */
	   public R visit(MethodDeclaration n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this); inNaming = true;
	      functionName = (String) n.f2.accept(this); inNaming = false;
	      n.f3.accept(this);
	      n.f4.accept(this);
		   inMeth = true;
	      n.f5.accept(this);
	      n.f6.accept(this);
	      n.f7.accept(this);
	      n.f8.accept(this);
	      n.f9.accept(this);
	      String returntype = (String) n.f10.accept(this);
	      n.f11.accept(this);
	      n.f12.accept(this);
	      
	      if( !isSubTypeOf(returntype,funcMap.get(className).get(functionName).retType) ) {
    		  System.out.print("Type error");
    		  System.exit(0);
    	  }
	      
	      inMeth = false;
	      return _ret;
	   }

	   /**
	    * f0 -> FormalParameter()
	    * f1 -> ( FormalParameterRest() )*
	    */
	   public R visit(FormalParameterList n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    */
	   public R visit(FormalParameter n) {
	      R _ret=null;
	      n.f0.accept(this); inNaming = true;
	      n.f1.accept(this); inNaming = false;
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> FormalParameter()
	    */
	   public R visit(FormalParameterRest n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> ArrayType()
	    *       | BooleanType()
	    *       | IntegerType()
	    *       | Identifier()
	    */
	   public R visit(Type n) {
	      return (R)n.f0.accept(this);
	   }

	   /**
	    * f0 -> "int"
	    * f1 -> "["
	    * f2 -> "]"
	    */
	   public R visit(ArrayType n) {
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      return (R)"int[]";
	   }

	   /**
	    * f0 -> "boolean"
	    */
	   public R visit(BooleanType n) {
	      n.f0.accept(this);
	      return (R)"boolean";
	   }

	   /**
	    * f0 -> "int"
	    */
	   public R visit(IntegerType n) {
	      n.f0.accept(this);
	      return (R)"int";
	   }
	   
	   /**
	    * f0 -> Block()
	    *       | AssignmentStatement()
	    *       | ArrayAssignmentStatement()
	    *       | IfStatement()
	    *       | WhileStatement()
	    *       | PrintStatement()
	    */
	   public R visit(Statement n) {
	      R _ret=null;
	      n.f0.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> "{"
	    * f1 -> ( Statement() )*
	    * f2 -> "}"
	    */
	   public R visit(Block n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> Identifier()
	    * f1 -> "="
	    * f2 -> Expression()
	    * f3 -> ";"
	    */
	   public R visit(AssignmentStatement n) {
	      R _ret=null;
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String right = (String) n.f2.accept(this);
	      n.f3.accept(this);
	      if(left=="int" || left=="int[]" || left=="boolean"){
	    	  if(left!=right){
	    		  System.out.print("Type error");
	    		  System.exit(0);
	    	  }
	      }
	      else if(right!="int" && right!="int[]" && right!="boolean") {
	    	  if(!isSubTypeOf(left,right)){
	    		  //System.out.println(left + " " + right);
	    		  System.out.print("Type error");
	    		  System.exit(0);
	    	  }
	      }
	      else {
	    	  System.out.print("Type error");
    		  System.exit(0);
	      }
	      
	      return _ret;
	   }

	   private boolean isSubTypeOf(String left, String right) {
		   if(left==right)
			   return true;
		   //System.out.println(extendsMap.toString());
		   String buff = right;
		   while(extendsMap.containsKey(buff)){
			   buff = extendsMap.get(right);
			   if(buff==left){
				   return true;
			   }
			   System.out.print(0);
		   }
		   return false;
	   }

	/**
	    * f0 -> Identifier()
	    * f1 -> "["
	    * f2 -> Expression()
	    * f3 -> "]"
	    * f4 -> "="
	    * f5 -> Expression()
	    * f6 -> ";"
	    */
	   public R visit(ArrayAssignmentStatement n) {
	      R _ret=null;
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String lookuptype = (String) n.f2.accept(this);
	      n.f3.accept(this);
	      n.f4.accept(this);
	      String right = (String) n.f5.accept(this);
	      n.f6.accept(this);
	      if(left=="int[]"){
	    	  if(lookuptype=="int"){
	    		  if(right=="int"){
	    			  
	    		  }
	    		  else{
		    		  System.out.print("Type error");
		    		  System.exit(0);
		    	  }
	    	  }
	    	  else{
	    		  System.out.print("Type error");
	    		  System.exit(0);
	    	  }
	      }
	      else{
    		  System.out.print("Type error");
    		  System.exit(0);
    	  }
	      return _ret;
	   }

	   /**
	    * f0 -> "if"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    * f5 -> "else"
	    * f6 -> Statement()
	    */
	   public R visit(IfStatement n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      String type = (String) n.f2.accept(this);
	      if(type!="boolean"){
    		  System.out.print("Type error");
    		  System.exit(0);
    	  }
	      n.f3.accept(this);
	      n.f4.accept(this);
	      n.f5.accept(this);
	      n.f6.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> "while"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    */
	   public R visit(WhileStatement n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      String type = (String) n.f2.accept(this);
	      if(type!="boolean"){
    		  System.out.print("Type error");
    		  System.exit(0);
    	  }
	      n.f3.accept(this);
	      n.f4.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> "System.out.println"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> ";"
	    */
	   public R visit(PrintStatement n) {
	      R _ret=null;
	      n.f0.accept(this);
	      n.f1.accept(this);
	      String type = (String) n.f2.accept(this);
	      if(type!="int"){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      n.f3.accept(this);
	      n.f4.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> AndExpression()
	    *       | CompareExpression()
	    *       | PlusExpression()
	    *       | MinusExpression()
	    *       | TimesExpression()
	    *       | ArrayLookup()
	    *       | ArrayLength()
	    *       | MessageSend()
	    *       | PrimaryExpression()
	    */
	   public R visit(Expression n) {
	      return (R) n.f0.accept(this);
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "&"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(AndExpression n) {
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String right = (String) n.f2.accept(this);
	      if(!(left=="boolean") || !(right=="boolean")){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      return (R) "boolean";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "<"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(CompareExpression n) {
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String right = (String) n.f2.accept(this);
	      if(left!="int" || right!="int"){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      return (R)"boolean";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "+"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(PlusExpression n) {
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String right = (String) n.f2.accept(this);
	      if(left!="int" || right!="int"){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      return (R)"int";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "-"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(MinusExpression n) {
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String right = (String) n.f2.accept(this);
	      if(left!="int" || right!="int"){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      return (R) "int";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "*"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(TimesExpression n) {
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String right = (String) n.f2.accept(this);
	      if(left!="int" || right!="int"){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      return (R)"int";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "["
	    * f2 -> PrimaryExpression()
	    * f3 -> "]"
	    */
	   public R visit(ArrayLookup n) {
	      String arrName = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      String lookuptype = (String) n.f2.accept(this);
	      n.f3.accept(this);
	      String type=null;
	      if(lookuptype!="int"){
	    	  System.out.println("Type error");
	    	  System.exit(0);
	      }
	      if(lookuptype=="int" && arrName!="int" && arrName!="int[]" && arrName!="boolean" && !funcMap.containsKey(arrName)){
	    	  if(funcMap.get(className).get(functionName).localvar.containsKey(arrName)){
	    		  type = funcMap.get(className).get(functionName).localvar.get(arrName);
	    	  }
	    	  else if(classvarMap.get(className).containsKey(arrName)){
	    		  type = classvarMap.get(className).get(arrName);
	    	  }
	    	  else {
	    		  System.out.print("Type error");
	    		  System.exit(0);
	    	  }
	    	  if(type!="int"){
	    		  System.out.print("Type error");
	    		  System.exit(0);
	    	  }
	      }
	      return (R)"int";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> "length"
	    */
	   public R visit(ArrayLength n) {
	      String left = (String) n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      if(left!="int[]"){
    		  System.out.print("Type error");
    		  System.exit(0);
    	  }
	      return (R)"int";
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> Identifier()
	    * f3 -> "("
	    * f4 -> ( ExpressionList() )?
	    * f5 -> ")"
	    */
	   public R visit(MessageSend n) {
	      R _ret=null;
	      methParams = new ArrayList<String>();
	      String _class = (String) n.f0.accept(this);
	      n.f1.accept(this); inNaming = true;
	      String funcName = (String) n.f2.accept(this); inNaming = false;
	      n.f3.accept(this);
	      n.f4.accept(this);
	      n.f5.accept(this);
	      
	      //System.out.println(_class + " " + funcName);
	      List<String> actualParams = funcMap.get(_class).get(funcName).argTypes;
	      
	      int paramsCount = actualParams.size();
	      if(paramsCount!=funcMap.get(_class).get(funcName).argnum){
    		  System.out.print("Type error");
    		  System.exit(0);
    	  }
	      
	      //System.out.println(paramsCount + " " + funcMap.get(_class).get(funcName).argnum);
	      //System.out.println(methParams.toString());
	      for(int i=0; i<methParams.size(); i++){
	    	  if(methParams.get(i)=="int" || methParams.get(i)=="int[]" || methParams.get(i)=="boolean"){
		    	  if(methParams.get(i)!=actualParams.get(i)){
		    		  System.out.print("Type error");
		    		  System.exit(0);
		    	  }
		      }
		      else if(actualParams.get(i)!="int" && actualParams.get(i)!="int[]" && actualParams.get(i)!="boolean") {
		    	  if(!isSubTypeOf(actualParams.get(i),methParams.get(i))){
		    		  System.out.print("Type error");
		    		  System.exit(0);
		    	  }
		      }
		      else {
		    	  System.out.print("Type error");
	    		  System.exit(0);
		      }
	      }
	      
	      return (R) funcMap.get(_class).get(funcName).retType;
	   }

	   /**
	    * f0 -> Expression()
	    * f1 -> ( ExpressionRest() )*
	    */
	   public R visit(ExpressionList n) {
	      R _ret=null;
	      methParams.add((String)n.f0.accept(this));
	      n.f1.accept(this);
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> Expression()
	    */
	   public R visit(ExpressionRest n) {
	      R _ret=null;
	      n.f0.accept(this);
	      methParams.add((String)n.f1.accept(this));
	      return _ret;
	   }

	   /**
	    * f0 -> IntegerLiteral()
	    *       | TrueLiteral()
	    *       | FalseLiteral()
	    *       | Identifier()
	    *       | ThisExpression()
	    *       | ArrayAllocationExpression()
	    *       | AllocationExpression()
	    *       | NotExpression()
	    *       | BracketExpression()
	    */
	   public R visit(PrimaryExpression n) {
	      String type = (String) n.f0.accept(this);
	      return (R)type;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public R visit(IntegerLiteral n) {
	      n.f0.accept(this);
	      return (R)"int";
	   }

	   /**
	    * f0 -> "true"
	    */
	   public R visit(TrueLiteral n) {
	      n.f0.accept(this);
	      return (R)"boolean";
	   }

	   /**
	    * f0 -> "false"
	    */
	   public R visit(FalseLiteral n) {
	      n.f0.accept(this);
	      return (R)"boolean";
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public R visit(Identifier n) {
	      String idtfr = n.f0.toString() ;
	      //System.out.println(idtfr + " " + className + " " + functionName);
	      //System.out.println(className + " " + functionName);
	      if(inNaming)
	    	  return (R) idtfr;
	      
		   n.f0.accept(this);
	      
	      String type = null;
	      if(inMeth && funcMap.get(className).get(functionName).localvar.containsKey(idtfr)){
    		  type = funcMap.get(className).get(functionName).localvar.get(idtfr);
    	  }
    	  else if(inMeth && inClass && classvarMap.get(className).containsKey(idtfr)){
    		  type = classvarMap.get(className).get(idtfr);
    	  }
	      return (R)type;
	   }

	   /**
	    * f0 -> "this"
	    */
	   public R visit(ThisExpression n) {
	      n.f0.accept(this);
	      return (R)className;
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> "int"
	    * f2 -> "["
	    * f3 -> Expression()
	    * f4 -> "]"
	    */
	   public R visit(ArrayAllocationExpression n) {
	      n.f0.accept(this);
	      n.f1.accept(this);
	      n.f2.accept(this);
	      String type = (String) n.f3.accept(this);
	      n.f4.accept(this);
	      if(type != "int") {
	    	  System.out.print("Type error");
	    	  System.exit(0);
	    	  return null;
	      }
	      return (R)"int[]";
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> Identifier()
	    * f2 -> "("
	    * f3 -> ")"
	    */
	   public R visit(AllocationExpression n) {
	      n.f0.accept(this);
	      inNaming = true;
	      String type = (String) n.f1.accept(this);
	      inNaming = false;
	      n.f2.accept(this);
	      n.f3.accept(this);
	      if(!funcMap.containsKey(type)){
	    	  System.out.println("Type error");
	    	  System.exit(0);
	      }
	      return (R)type;
	   }

	   /**
	    * f0 -> "!"
	    * f1 -> Expression()
	    */
	   public R visit(NotExpression n) {
	      n.f0.accept(this);
	      String type = (String) n.f1.accept(this);
	      if(type != "boolean"){
	    	  System.out.print("Type error");
	    	  System.exit(0);
	      }
	      return (R)type;
	   }

	   /**
	    * f0 -> "("
	    * f1 -> Expression()
	    * f2 -> ")"
	    */
	   public R visit(BracketExpression n) {
	      n.f0.accept(this);
	      String type = (String) n.f1.accept(this);
	      n.f2.accept(this);
	      return (R)type;
	   }
	
}
