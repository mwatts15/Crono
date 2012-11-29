package crono.inf;
import java.util.*;

public class Rdf{
	//private Rdfs rdfs = new Rdfs();
	//public final Property Property = new Property();
	//public final Type type = new Type();
  
  /*
	public Rdf(){
		Property = new Property();
		type = new Type();
	}*/
	private static enum c{
		type(-1,"rdf:type"),
		Property(-2,"rdf:Property");
		public final int id; 
		public final String name;
		private c(int id, String name){this.id=id;this.name=name;}
	}
	
	public static class Property extends InfClass{
		public Property(){
			this.id = c.Property.id;
			this.name = c.Property.name;
		}
	}	
	
	public static class Type extends InfClass{
		public Type(){
			this.id = c.type.id;
			this.name = c.type.name;
		}
		
		protected boolean fn(InfTriple cont, ArrayList<InfTriple> db){
			InfClass A = (InfClass)cont.getSubject(); 
			InfClass B = (InfClass)cont.getObject();
			return db.add(new InfTriple(B,new Rdf.Type(),new Rdfs.Class(),true));
		}
	}
	
}
