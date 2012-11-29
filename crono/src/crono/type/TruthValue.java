package crono.type;

public class TruthValue extends Cons {
    public static final TypeId TYPEID = new TypeId(":t", TruthValue.class,
                                                   Cons.TYPEID);
    public static final TruthValue T = new TruthValue();
    
    protected TruthValue() {
        this.car = null;
        this.cdr = null;
    }
    
    public CronoType car() {
        return this;
    }
    public CronoType cdr() {
        return this;
    }
    
    public TypeId typeId() {
        return TruthValue.TYPEID;
    }
    public String toString() {
        return "#t";
    }
    
    public boolean equals(Object o) {
        return (o instanceof TruthValue);
    }
}