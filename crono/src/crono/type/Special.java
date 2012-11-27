package crono.type;
import crono.type.*;
import crono.*;

public abstract class Special extends Function {
    public Special(TypeId[] args, TypeId ret, int arity) {
        this(args, ret, arity, false, EvalType.FULL);
    }
    public Special(TypeId[] args, TypeId ret, int arity, boolean variadic) {
        this(args, ret, arity, variadic, EvalType.FULL);
    }
    public Special(TypeId[] args, TypeId ret, int arity, EvalType eval) {
        this(args, ret, arity, false, eval);
    }
    public Special(TypeId[] args, TypeId ret, int arity, boolean variadic,
            EvalType eval)
    {
        super(args,ret,arity,variadic,eval);
    }
};
