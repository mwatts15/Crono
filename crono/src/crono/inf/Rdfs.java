package crono.inf;
//import java.util.*;

public class Rdfs{
	//public final SubClassOf subClassOf = new SubClassOf();
	//public final Class Class = new Class();
  /*
	public Rdfs(){
		subClassOf = new SubClassOf();
		Class = new Class();
	}*/
	private static enum c{
		subClassOf(-20,"rdfs:subClassOf"),
		Class(-21,"rdfs:Class");
		public final int id; 
		public final String name;
		private c(int id, String name){this.id=id;this.name=name;}
	}
	
	public static class Class extends InfClass{
		public Class(){
			this.id = c.Class.id;
			this.name = c.Class.name;
		}
	}
	
	public static class SubClassOf extends InfClass{
		public SubClassOf(){
			this.id = c.subClassOf.id;
			this.name = c.subClassOf.name;
		}
	}
	
}
