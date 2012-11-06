package crono.type;

public abstract class CronoArray extends Atom {
    public static final TypeId TYPEID = new TypeId(":array", CronoArray.class,
						   Atom.TYPEID);
    
    public abstract int rank();
    public abstract int dim(int n);
    public abstract CronoType get(int[] n);
}
