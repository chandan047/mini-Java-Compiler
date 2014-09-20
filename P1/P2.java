import syntaxtree.*;
import visitor.*;

public class P2 {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         TypeBuilderVisitor builder = new TypeBuilderVisitor();
         root.accept(builder);
         TypeCheckVisitor checker = new TypeCheckVisitor<>(builder.functionMap,builder.classvarMap,builder.extendsMap);
         root.accept(checker);
         System.out.println("Program type checked successfully");
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 

