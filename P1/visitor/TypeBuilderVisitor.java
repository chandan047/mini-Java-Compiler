
package visitor;
import syntaxtree.*;

import java.util.*;

import com.sun.org.apache.bcel.internal.classfile.LocalVariable;


class FunctionSignature {
	public String methName;
	public String retType;
	public int argnum;
	public LinkedList<String> argTypes = new LinkedList<String>();
	public LinkedHashMap<String,String> localvar = new LinkedHashMap<String,String>();
	
	public FunctionSignature(String identifier){
		methName = identifier;
	}
	
	public boolean signequalto(FunctionSignature s) {
		if(this.argnum==s.argnum){
			Iterator<String> it1 = argTypes.iterator();
			Iterator<String> it2 = s.argTypes.iterator();
			while(it1.hasNext()){
				if( it1.next() == it2.next() ){
					continue;
				}
				else
					return false;
			}
		}
		return this.methName==s.methName;
	}
	
	public String toString() {
		String ret = "Method : " + methName + "\n\tReturn Type = " + retType
				+ "\n\tArgument Types = ";
		Iterator<String> it = argTypes.iterator();
		while(it.hasNext()) {
			ret = ret + " " + it.next() ;
		}
		it = localvar.keySet().iterator();
		ret = ret + "\n  Local Vars:";
		while(it.hasNext()){
			String type = it.next();
			ret = ret + "\n\t" + type + " " + localvar.get(type);
		}
		return ret;
	}
}


/**
 * Provides default methods which visit each node in the tree in depth-first
 * order. Type checker class using Depth First Visitor.
 */
public class TypeBuilderVisitor<R> implements GJNoArguVisitor<R> {
   
	public LinkedHashMap<String, LinkedHashMap<String, FunctionSignature>> functionMap = 
			new LinkedHashMap<String, LinkedHashMap<String, FunctionSignature>>();

	public LinkedHashMap<String,LinkedHashMap<String,String>> classvarMap
	= new LinkedHashMap<String,LinkedHashMap<String,String>>();
	
	public LinkedHashMap<String, String> extendsMap = new LinkedHashMap<String, String>();
	
	LinkedHashMap<String, FunctionSignature> signatureMap;
	LinkedHashMap<String,String> vardecl;
	FunctionSignature fs;
	
	String className;
	boolean infunc = false;
	
	//String func_identifier,curr_class;
	
	
   public R visit(NodeList n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
      return null;
   }

   public R visit(NodeListOptional n) {
      if ( n.present() )
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
            e.nextElement().accept(this);
      return null;
   }

   public R visit(NodeOptional n) {
      if ( n.present() )
         n.node.accept(this);
      return null;
   }

   public R visit(NodeSequence n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
      return null;
   }

   public R visit(NodeToken n) {return null; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public R visit(Goal n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      
	  injectBaseMethods();
      
      //printClassInfo();
      
      return null;
   }

   private void printClassInfo() {
      Set<String> classes = functionMap.keySet();
      Iterator<String> _class = classes.iterator();
      
      System.out.println(classvarMap.toString());
      System.out.println(functionMap.toString());
//      while( _class.hasNext() ) {
//    	  String classname = _class.next();
//	      Collection<FunctionSignature> signatures = functionMap.get(classname).values();
//	      Iterator<FunctionSignature> i = signatures.iterator();
//	      System.out.println("class : " + classname);
//	      while(i.hasNext())
//	    	  System.out.println("  " + i.next().toString());
//      }
   }

   private void injectBaseMethods() {
	   Set<String> extendedClasses = extendsMap.keySet();
	   LinkedList<String> list = new LinkedList<>(extendedClasses);
	   while(!list.isEmpty()){
		   String concreteClass = list.getFirst();

		   inject(list,concreteClass);
//		   
//		   LinkedHashMap<String, FunctionSignature> concreteMethods = functionMap.get(concreteClass);
//		   LinkedHashMap<String, FunctionSignature> baseMethods = functionMap.get(baseClass);
//		   
//		   Set<String> baseMethodNames = baseMethods.keySet();
//		   Iterator<String> baseitr = baseMethodNames.iterator();
//		   
//		   while(baseitr.hasNext()){
//			   String methodName = baseitr.next();
//			   if(!concreteMethods.containsKey(methodName)){
//				   concreteMethods.put(methodName, baseMethods.get(methodName));
//			   }
//			   else {
//				   FunctionSignature cMethod = concreteMethods.get(methodName);
//				   FunctionSignature bMethod = baseMethods.get(methodName);
//				   if(!cMethod.signequalto(bMethod)){
//					   System.out.println("Type error");
//					   System.exit(0);
//				   }
//				   else if(cMethod.retType!=bMethod.retType) {
//					   System.out.println("Type error");
//					   System.exit(0);
//				   }
//			   }
//		   }
		   
	   }
   }


private void inject(LinkedList<String> list, String _class) {
	   String baseClass = extendsMap.get(_class);
	   
	   if(list.contains(baseClass)){
		   inject(list,baseClass);
		   list.remove(baseClass);
	   }
	   
	   classvarMap.get(_class).putAll(classvarMap.get(baseClass));
	   LinkedHashMap<String, FunctionSignature> concreteMethods = functionMap.get(_class);
	   LinkedHashMap<String, FunctionSignature> baseMethods = functionMap.get(baseClass);
	   
	   Set<String> baseMethodNames = baseMethods.keySet();
	   Iterator<String> baseitr = baseMethodNames.iterator();
	   
	   while(baseitr.hasNext()){
		   String methodName = baseitr.next();
		   if(!concreteMethods.containsKey(methodName)){
			   concreteMethods.put(methodName, baseMethods.get(methodName));
		   }
		   else {
			   FunctionSignature cMethod = concreteMethods.get(methodName);
			   FunctionSignature bMethod = baseMethods.get(methodName);
			   if(!cMethod.signequalto(bMethod)){
				   System.out.print("Type error");
				   System.exit(0);
			   }
			   else if(cMethod.retType!=bMethod.retType) {
				   System.out.print("Type error");
				   System.exit(0);
			   }
		   }
	   }
	   
	   list.remove(_class);
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
      n.f10.accept(this);
      n.f11.accept(this);
      n.f12.accept(this);
      n.f13.accept(this);
      n.f14.accept(this);
      n.f15.accept(this);
      n.f16.accept(this);
      
      return null;
   }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public R visit(TypeDeclaration n) {
      n.f0.accept(this);
      return null;
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
	   signatureMap = new LinkedHashMap<>();
      n.f0.accept(this);
      className = (String) n.f1.accept(this); //curr_class = n.f1.f0.toString();
      n.f2.accept(this);
      vardecl = new LinkedHashMap<String,String>();
	  if(!classvarMap.containsKey(className))
		  classvarMap.put(className, vardecl);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      
      functionMap.put(className, signatureMap);
      
      return null;
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
	   signatureMap = new LinkedHashMap<>();
      className = (String) n.f1.accept(this); //curr_class = n.f1.f0.toString();
      n.f1.accept(this);
      n.f2.accept(this);
      String base_class = (String) n.f3.accept(this); //base_class = n.f3.f0.toString();
      n.f4.accept(this);
	  vardecl = new LinkedHashMap<String,String>();
	  if(!classvarMap.containsKey(className))
		  classvarMap.put(className, vardecl);
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
      
      extendsMap.put(className, base_class);
      functionMap.put(className, signatureMap);
      
      return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public R visit(VarDeclaration n) {
      String type = (String) n.f0.accept(this);
      String idtfr = (String) n.f1.accept(this);
      //System.out.println(type + " " + idtfr + " " + infunc);
      n.f2.accept(this);
      if(infunc)
    	  fs.localvar.put(idtfr, type);
      else{
    	  if(classvarMap.containsKey(className))
    		  classvarMap.get(className).put(idtfr, type);
    	  else{
    		  vardecl.put(idtfr, type);
    	  }
      }
      return null;
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
	   
	   //******************************************************
	   //func_identifier = n.f2.f0.toString();
	   //fs = new FunctionSignature(func_identifier);
	   //******************************************************
	   
      n.f0.accept(this);
      String func_identifier = (String) n.f2.accept(this);
      fs = new FunctionSignature(func_identifier);
      fs.retType = (String) n.f1.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this); infunc = true;
      n.f7.accept(this); infunc = false;
      n.f8.accept(this);
      n.f9.accept(this);
      n.f10.accept(this);
      n.f11.accept(this);
      n.f12.accept(this);
      
      if(!signatureMap.containsKey(func_identifier))
    	  signatureMap.put(func_identifier, fs);
      else{
    	  System.err.println("Type error");
    	  System.exit(0);
      }
      
      return null;
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   public R visit(FormalParameterList n) {
      n.f0.accept(this);
      n.f1.accept(this);
      return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public R visit(FormalParameter n) {
	   
	   //************************************************
	   fs.argnum++;
	   //************************************************
	  fs.argTypes.add((String) n.f0.accept(this));
	  fs.localvar.put((String)n.f1.f0.toString(), (String)n.f0.accept(this));
      n.f1.accept(this);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public R visit(FormalParameterRest n) {
      n.f0.accept(this);
      n.f1.accept(this);
      return null;
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
      n.f0.accept(this);
      return null;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public R visit(Block n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public R visit(AssignmentStatement n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      return null;
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
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      return null;
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
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public R visit(WhileStatement n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public R visit(PrintStatement n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      return null;
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
      n.f0.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&"
    * f2 -> PrimaryExpression()
    */
   public R visit(AndExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public R visit(CompareExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public R visit(PlusExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public R visit(MinusExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public R visit(TimesExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public R visit(ArrayLookup n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public R visit(ArrayLength n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
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
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      return null;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public R visit(ExpressionList n) {
      n.f0.accept(this);
      n.f1.accept(this);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public R visit(ExpressionRest n) {
      n.f0.accept(this);
      n.f1.accept(this);
      return null;
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
      n.f0.accept(this);
      return null;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n) {
      n.f0.accept(this);
      return (R)n.f0.toString();
   }

   /**
    * f0 -> "true"
    */
   public R visit(TrueLiteral n) {
      n.f0.accept(this);
      return null;
   }

   /**
    * f0 -> "false"
    */
   public R visit(FalseLiteral n) {
      n.f0.accept(this);
      return null;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Identifier n) {
      n.f0.accept(this);
      return (R)n.f0.toString();
   }

   /**
    * f0 -> "this"
    */
   public R visit(ThisExpression n) {
      n.f0.accept(this);
      return null;
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
      n.f3.accept(this);
      n.f4.accept(this);
      return null;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public R visit(AllocationExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      return null;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public R visit(NotExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      return null;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public R visit(BracketExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return null;
   }

}

