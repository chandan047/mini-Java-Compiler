import syntaxtree.*;
import visitor.*;

public class P5 {
public static void main(String [] args) {
      try {
         Node root = new microIRParser(System.in).Goal();
         
         livenessgen livegen = new livenessgen();
         root.accept(livegen,null);
         
         miniRAGen ragen = new miniRAGen(livegen.livenessMap);
         root.accept(ragen,null);
         
         System.err.println("Program parsed succesfully");
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 

