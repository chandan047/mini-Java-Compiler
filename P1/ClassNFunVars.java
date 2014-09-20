import syntaxtree.*;
import visitor.*;
import java.util.*;

public class ClassNFunVars {
	public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         //System.out.println("Program parsed successfully");
         root.accept(new ClassNFunVarsVisitor()); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



