package crono.type;

public class Nil extends Cons {
    public static final TypeId TYPEID = new TypeId(":nil", Nil.class,
                                                   Cons.TYPEID);
    public static final Nil NIL = new Nil();
    
    protected Nil() {
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
        return Nil.TYPEID;
    }
    public String toString() {
        return "Nil";
    }
    
    public boolean equals(Object o) {
        return (o instanceof Nil);
    }
}
