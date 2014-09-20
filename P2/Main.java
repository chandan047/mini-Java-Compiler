import syntaxtree.*;
import visitor.*;

public class Main {
   @SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public static void main(String [] args) {
      try {
		Node root = new MiniJavaParser(System.in).Goal();
         TypeBuilderVisitor builder = new TypeBuilderVisitor();
         root.accept(builder);
         miniIRGenerator mIRgen = new miniIRGenerator<>(builder.classTofunctionMap, builder.classToVarMap, builder.extendsMap);
         root.accept(mIRgen);
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 

