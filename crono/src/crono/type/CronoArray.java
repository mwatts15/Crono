package crono.type;

public abstract class CronoArray extends Atom {
    public static final TypeId TYPEID = new TypeId(":array", CronoArray.class,
						   Atom.TYPEID);
    
    protected TypeId accept;
    public CronoArray(TypeId accept) {
	this.accept = accept;
    }
    
    public abstract int rank();
    public abstract int dim(int n);
    public abstract CronoType get(int[] n);
    public abstract CronoType put(int[] n, CronoType item);
    
    public TypeId accept() {
	return accept;
    }
}
