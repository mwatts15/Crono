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

    /* Argument types */
    public final TypeId[] argument_types;
    public final TypeId returntype;
    public final boolean variadic;
    public final EvalType eval;
    public final int arity;

    protected Function() {
        this(null, null, 0, false, EvalType.FULL);
    }
    public Function(TypeId[] argument_types, TypeId ret, int arity) {
        this(argument_types, ret, arity, false, EvalType.FULL);
    }
    public Function(TypeId[] argument_types, TypeId ret, int arity, boolean variadic)
    {
        this(argument_types, ret, arity, variadic, EvalType.FULL);
    }

    public Function(TypeId[] argument_types, TypeId ret, int arity, EvalType eval) {
        this(argument_types, ret, arity, false, eval);
    }
    public Function(TypeId[] argument_types, TypeId ret, int arity, boolean variadic,
            EvalType eval)
    {
        this.argument_types = argument_types;
        this.returntype = ret;
        this.arity = arity;
        this.variadic = variadic;
        this.eval = eval;
    }

    public abstract CronoType run(Visitor v, CronoType[] argument_types);

    public TypeId typeId() {
        return Function.TYPEID;
    }
}
