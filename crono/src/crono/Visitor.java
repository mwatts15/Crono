package crono;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;

public abstract class Visitor {
    public abstract CronoType visit(Atom atom);
    public abstract CronoType visit(Cons list);
}
