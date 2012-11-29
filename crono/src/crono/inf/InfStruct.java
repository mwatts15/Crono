package crono.inf;

import java.util.ArrayList;
import java.util.Iterator;

public class InfStruct{
  public static final IsA isa = new IsA();
	public static final HasA hasA = new HasA();
  
  /*
	public Rdfs(){
		subClassOf = new SubClassOf();
		Class = new Class();
	}*/
  
	private static enum c{
		isA(-41,"struct:isA"),
		hasA(-42,"struct:hasA"),
    structDef(-43,"struct:structDef"),;
		public final int id; 
		public final String name;
		private c(int id, String name){this.id=id;this.name=name;}
	}
  public static class structDef extends InfClass{
    public structDef(){
      this.id = c.structDef.id;
      this.name = c.structDef.name;
    }
  }
	
	public static class IsA extends InfClass{
		public IsA(){
			this.id = c.isA.id;
			this.name = c.isA.name;
		}
		
		protected boolean fn(InfTriple cont, ArrayList<InfTriple> db){
		//if (A isa B)
		//then (A hasa (B's fields))
		//b's fields: (B hasa F)
			boolean changed=false;
			InfClass A = (InfClass)cont.getSubject(); 
			InfClass B = (InfClass)cont.getObject();
			ArrayList<InfTriple> fields = InfDatabase.seek(B,new HasA(),null,db);
			for(Iterator i = fields.iterator();i.hasNext();){
				InfTriple t = (InfTriple)i.next();
				//System.out.println("got a triple: "+t);
				changed = db.add(new InfTriple(A,t.getPredicate(),t.getObject(),true))?true:changed;
			}
			return changed;
		}
		
	}
	
	public static class HasA extends InfClass{
		public HasA(){
			this.id = c.hasA.id;
			this.name = c.hasA.name;
		}
		
	}
	
}
