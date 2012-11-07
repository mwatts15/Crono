package crono.type;

import crono.Visitor;

public abstract class Function extends Atom {
    public static final TypeId TYPEID = new TypeId(":function", Function.class,
						   Atom.TYPEID);
    
    public enum EvalType {
	FULL("full", 2),
	PARTIAL("partial", 1),
	NONE("none", 0),
	;
	
	public final String name;
	public final int level;
	private EvalType(String s, int l) {
	    this.level = l;
	    this.name = s;
	}
	public String toString() {
	    return name;
	}
    }
    
    /* Supplied defaults here */
    public boolean variadic() {
	return false;
    }
    public EvalType eval() {
	return EvalType.FULL;
    }

    public abstract int arity();
    public abstract CronoType run(Visitor v, CronoType[] args);
    
    public TypeId typeId() {
	return Function.TYPEID;
    }
}
