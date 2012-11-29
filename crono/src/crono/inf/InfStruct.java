package crono.inf;

public class InfStruct{
  public static final IsA isa = new IsA();
	public static final HasA hasA = new HasA();
  
  /*
	public Rdfs(){
		subClassOf = new SubClassOf();
		Class = new Class();
	}*/
  
	private static enum c{
		isA(-22,"struct:isA"),
		hasA(-23,"struct:hasA"),
    structDef(-24,"struct:structDef"),;
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
	}
	
	public static class HasA extends InfClass{
		public HasA(){
			this.id = c.hasA.id;
			this.name = c.hasA.name;
		}
	}
	
}
