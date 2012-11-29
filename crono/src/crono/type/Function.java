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
    
    public final TypeId[] args;
    public final TypeId returntype;
    public final boolean variadic;
    public final EvalType eval;
    public final int arity;
    
    protected Function() {
        this(null, null, 0, false, EvalType.FULL);
    }
    public Function(TypeId[] args, TypeId ret, int arity) {
        this(args, ret, arity, false, EvalType.FULL);
    }
    public Function(TypeId[] args, TypeId ret, int arity, boolean variadic) {
        this(args, ret, arity, variadic, EvalType.FULL);
    }
    public Function(TypeId[] args, TypeId ret, int arity, EvalType eval) {
        this(args, ret, arity, false, eval);
    }
    public Function(TypeId[] args, TypeId ret, int arity, boolean variadic,
                    EvalType eval)
    {
        this.args = args;
        this.returntype = ret;
        this.arity = arity;
        this.variadic = variadic;
        this.eval = eval;
    }
    
    public abstract CronoType run(Visitor v, CronoType[] args);
    
    public TypeId typeId() {
        return Function.TYPEID;
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof Function)) {
            return false;
        }
        if(o instanceof LambdaFunction) {
            return o.equals(this);
        }
        Function fun = (Function)o;
        if((!(returntype.equals(fun.returntype)) || variadic != fun.variadic ||
            !(eval.equals(fun.eval)) || arity != fun.arity ||
            args.length != fun.args.length ||
            !(toString().equals(fun.toString()))))
        {
            return false;
        }
        
        for(int i = 0; i < args.length; ++i) {
            if(!(args[i].equals(fun.args[i]))) {
                return false;
            }
        }
        return true;
    }
}
