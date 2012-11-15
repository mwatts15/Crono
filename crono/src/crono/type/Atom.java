package crono.type;

import crono.Visitor;

public abstract class Atom extends CronoType {
    public static final TypeId TYPEID = new TypeId(":atom", Atom.class,
            CronoType.TYPEID);

    public CronoType accept(Visitor v) {
        return v.visit(this);
    }
}
