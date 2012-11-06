package crono.type;

import crono.Visitor;

public abstract class Function extends Atom {
    public static final TypeId TYPEID =
	new TypeId(":function", Function.class);
    
    public enum EvalType {
	FULL,
	PARTIAL,
	NONE,
    }
    
    public abstract int arity();
    public abstract boolean variadic();
    public abstract EvalType eval();
    public abstract CronoType run(Visitor v, CronoType[] args);
    
    public TypeId typeId() {
	return Function.TYPEID;
    }
}
