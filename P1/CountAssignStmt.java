import syntaxtree.*;
import visitor.*;

public class CountAssignStmt {
	public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         //System.out.println("Program parsed successfully");
         root.accept(new CountAssignStmtVisitor()); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}