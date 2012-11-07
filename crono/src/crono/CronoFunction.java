package crono;

import java.util.List;

import crono.type.Cons;
import crono.type.CronoFloat;
import crono.type.CronoInteger;
import crono.type.CronoNumber;
import crono.type.CronoType;
import crono.type.Function;
import crono.type.Function.EvalType;
import crono.type.LambdaFunction;
import crono.type.Symbol;

public enum CronoFunction {
    LAMBDA(new Function() {
	    public static final String _invalid_arg_type =
		"Invalid argument type to '\\': expects :cons<:symbol> :any";
	    
	    public int arity() {
		return 2;
	    }
	    public boolean variadic() {
		return false;
	    }
	    public EvalType eval() {
		return EvalType.NONE;
	    }
	    public CronoType run(Visitor v, CronoType[] args) {
		if(!(args[0] instanceof Cons)) {
		    throw new RuntimeException(_invalid_arg_type);
		}
		
		List<CronoType> list = ((Cons)args[0]).toList();
		for(CronoType item : list) {
		    if(!(item instanceof Symbol)) {
			throw new RuntimeException(_invalid_arg_type);
		    }
		}
		
		Symbol[] arglist = new Symbol[list.size()];
		return new LambdaFunction(list.toArray(arglist), args[1],
					  v.getEnv());
	    }
	    public String toString() {
		return "\\";
	    }
    }),
    ADD(new Function() {
	public int arity() {
	    return 2;
	}
	public boolean variadic() {
	    return false;
	}
	public EvalType eval() {
	    return EvalType.FULL;
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new RuntimeException("Invalid argument types to '+'");
	    }
	    
	    lhs = (CronoNumber)(args[0]);
	    rhs = (CronoNumber)(args[1]);
	    
	    if(lhs instanceof CronoFloat) {
		double val1, val2;
		val1 = ((CronoFloat)lhs).value;
		if(rhs instanceof CronoFloat) {
		    val2 = ((CronoFloat)rhs).value;
		}else {
		    val2 = (double)(((CronoInteger)rhs).value);
		}
		return new CronoFloat(val1 + val2);
	    }else {
		if(rhs instanceof CronoFloat) {
		    double val1, val2;
		    val1 = (double)(((CronoInteger)lhs).value);
		    val2 = ((CronoFloat)rhs).value;
		    return new CronoFloat(val1 + val2);
		}else {
		    long val1, val2;
		    val1 = ((CronoInteger)lhs).value;
		    val2 = ((CronoInteger)rhs).value;
		    return new CronoInteger(val1 + val2);
		}
	    }
	}
	public String toString() {
	    return "+";
	}
    }),
    SUB(new Function() {
	public int arity() {
	    return 2;
	}
	public boolean variadic() {
	    return false;
	}
	public EvalType eval() {
	    return EvalType.FULL;
	}
	    
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new RuntimeException("Invalid argument types to '+'");
	    }
	    
	    lhs = (CronoNumber)(args[0]);
	    rhs = (CronoNumber)(args[1]);
	    
	    if(lhs instanceof CronoFloat) {
		double val1, val2;
		val1 = ((CronoFloat)lhs).value;
		if(rhs instanceof CronoFloat) {
		    val2 = ((CronoFloat)rhs).value;
		}else {
		    val2 = (double)(((CronoInteger)rhs).value);
		}
		return new CronoFloat(val1 - val2);
	    }else {
		if(rhs instanceof CronoFloat) {
		    double val1, val2;
		    val1 = (double)(((CronoInteger)lhs).value);
		    val2 = ((CronoFloat)rhs).value;
		    return new CronoFloat(val1 - val2);
		}else {
		    long val1, val2;
		    val1 = ((CronoInteger)lhs).value;
		    val2 = ((CronoInteger)rhs).value;
		    return new CronoInteger(val1 - val2);
		}
	    }
	}
	public String toString() {
	    return "-";
	}
    }),
    MUL(new Function() {
	public int arity() {
	    return 2;
	}
	public boolean variadic() {
	    return false;
	}
	public EvalType eval() {
	    return EvalType.FULL;
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new RuntimeException("Invalid argument types to '+'");
	    }
	    
	    lhs = (CronoNumber)(args[0]);
	    rhs = (CronoNumber)(args[1]);
	    
	    if(lhs instanceof CronoFloat) {
		double val1, val2;
		val1 = ((CronoFloat)lhs).value;
		if(rhs instanceof CronoFloat) {
		    val2 = ((CronoFloat)rhs).value;
		}else {
		    val2 = (double)(((CronoInteger)rhs).value);
		}
		return new CronoFloat(val1 * val2);
	    }else {
		if(rhs instanceof CronoFloat) {
		    double val1, val2;
		    val1 = (double)(((CronoInteger)lhs).value);
		    val2 = ((CronoFloat)rhs).value;
		    return new CronoFloat(val1 * val2);
		}else {
		    long val1, val2;
		    val1 = ((CronoInteger)lhs).value;
		    val2 = ((CronoInteger)rhs).value;
		    return new CronoInteger(val1 * val2);
		}
	    }
	}
	public String toString() {
	    return "*";
	}
    }),
    DIV(new Function() {
	public int arity() {
	    return 2;
	}
	public boolean variadic() {
	    return false;
	}
	public EvalType eval() {
	    return EvalType.FULL;
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new RuntimeException("Invalid argument types to '+'");
	    }
	    
	    lhs = (CronoNumber)(args[0]);
	    rhs = (CronoNumber)(args[1]);
	    
	    if(lhs instanceof CronoFloat) {
		double val1, val2;
		val1 = ((CronoFloat)lhs).value;
		if(rhs instanceof CronoFloat) {
		    val2 = ((CronoFloat)rhs).value;
		}else {
		    val2 = (double)(((CronoInteger)rhs).value);
		}
		return new CronoFloat(val1 / val2);
	    }else {
		if(rhs instanceof CronoFloat) {
		    double val1, val2;
		    val1 = (double)(((CronoInteger)lhs).value);
		    val2 = ((CronoFloat)rhs).value;
		    return new CronoFloat(val1 / val2);
		}else {
		    long val1, val2;
		    val1 = ((CronoInteger)lhs).value;
		    val2 = ((CronoInteger)rhs).value;
		    return new CronoInteger(val1 / val2);
		}
	    }
	}
	
	public String toString() {
	    return "/";
	}
    }),
    ;
    
    public final Function function;
    private CronoFunction(Function fun) {
	this.function = fun;
    }
}
