package crono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import crono.type.Cons;
import crono.type.CronoCharacter;
import crono.type.CronoFloat;
import crono.type.CronoInteger;
import crono.type.CronoNumber;
import crono.type.CronoPrimitive;
import crono.type.CronoString;
import crono.type.CronoType;
import crono.type.Function;
import crono.type.Function.EvalType;
import crono.type.Nil;
import crono.type.LambdaFunction;
import crono.type.Symbol;
import crono.type.TruthValue;
import crono.type.TypeId;

/**
 * Define all builtins here. If the function is not variadic, don't define the
 * boolean variadic() method (defaults to false).
 * If the function fully evaluates its arguments don't define
 * EvalType eval() (defaults to EvalType.FULL)
 * The length of TypeId[] args() must be at least as large as int arity(), but
 * may be larger if the function is variadic (used for structs).
 */
public enum CronoFunction {
    CONS(new Function(new TypeId[]{CronoType.TYPEID, CronoType.TYPEID},
		      Cons.TYPEID, 2)
    {
	public CronoType run(Visitor v, CronoType[] args) {
	    return new Cons(args[0], args[1]);
	}
	public String toString() {
	    return "cons";
	}
    }),
    CAR(new Function(new TypeId[]{Cons.TYPEID}, CronoType.TYPEID, 1)
    {
	private static final String _not_cons = "%s is not a cons cell";
	
        public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof Cons)) {
		throw new InterpreterException(_not_cons, args[0]);
	    }
	    return ((Cons)args[0]).car();
	}
	public String toString() {
	    return "car";
	}
    }),
    CDR(new Function(new TypeId[]{Cons.TYPEID}, CronoType.TYPEID, 1)
    {
	private static final String _not_cons = "%s is not a cons cell";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof Cons)) {
		throw new InterpreterException(_not_cons, args[0]);
	    }
	    return ((Cons)args[0]).cdr();
	}
	public String toString() {
	    return "cdr";
	}
    }),
    DEFINE(new Function(new TypeId[]{Symbol.TYPEID, CronoType.TYPEID},
			CronoType.TYPEID, 2, EvalType.NONE)
    {
	private static final String _bad_type =
	    "DEFINE: expected :symbol, got %s";
	
	public CronoType run(Visitor v, CronoType[]args) {
	    if(!(args[0] instanceof Symbol)) {
		throw new InterpreterException(_bad_type, args[0].typeId());
	    }
	    CronoType value = args[1].accept(v);
	    v.getEnv().put(((Symbol)args[0]), value);
	    return value;
	}
	public String toString() {
	    return "define";
	}
    }),
    UNDEFINE(new Function(new TypeId[]{Symbol.TYPEID}, Nil.TYPEID, 1,
			  EvalType.NONE)
    {
	private static final String _bad_type =
	    "UNDEF: expected :symbol, got %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    StringBuilder builder = new StringBuilder();
	    boolean errors = false;
	    for(CronoType ct : args) {
		if(!(ct instanceof Symbol)) {
		    builder.append(String.format(_bad_type, ct.typeId()));
		    builder.append('\n');
		    errors = true;
		    continue;
		}
		v.getEnv().remove((Symbol)ct);
	    }
	    
	    if(errors) {
		/* Remove last newline */
		builder.deleteCharAt(builder.length() - 1);
		throw new InterpreterException(builder.toString());
	    }
	    return Nil.NIL;
	}
	public String toString() {
	    return "undef";
	}
    }),
    LAMBDA(new Function(new TypeId[]{Cons.TYPEID, CronoType.TYPEID},
			Function.TYPEID, 2, true, EvalType.NONE)
    {
	private static final String _bad_type =
	    "\\: expected :cons :any, got %s, %s";
	private static final String _bad_arg =
	    "\\: arguments must be :symbol, got %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof Cons)) {
		throw new InterpreterException(_bad_type,
					       args[0].typeId(),
					       args[1].typeId());
	    }
	    
	    List<CronoType> list = ((Cons)args[0]).toList();
	    for(CronoType item : list) {
		if(!(item instanceof Symbol)) {
		    throw new InterpreterException(_bad_arg,item.typeId());
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
    LET(new Function(new TypeId[]{Cons.TYPEID, CronoType.TYPEID},
		     CronoType.TYPEID, 2, true, EvalType.NONE)
    {
	private static final String _subst_list_type =
	    "LET: substitution list must be :cons, got %s";
	private static final String _subst_not_cons =
	    "LET: expected :cons in substitution list, got %s";
	private static final String _subst_not_sym = 
	    "LET: argument names numst be :symbol, got %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof Cons)) {
		throw new InterpreterException(_subst_list_type,
					       args[0].typeId());
	    }
	    
	    List<Symbol> symlist = new LinkedList<Symbol>();
	    List<CronoType> arglist = new LinkedList<CronoType>();
	    for(CronoType ct : ((Cons)args[0])) {
		if(!(ct instanceof Cons)) {
		    throw new InterpreterException(_subst_not_cons,
						   ct.typeId());
		}
		
		CronoType car = ((Cons)ct).car();
		CronoType cdr = ((Cons)ct).cdr();
		if(!(car instanceof Symbol)) {
		    throw new InterpreterException(_subst_not_sym,
						   car.typeId());
		}
		
		cdr = cdr.accept(v);
		symlist.add((Symbol)car);
		arglist.add(cdr);
	    }
	    
	    Symbol[] lsyms = new Symbol[symlist.size()];
	    lsyms = symlist.toArray(lsyms);
	    
	    List<CronoType> bodylist = new LinkedList<CronoType>();
	    for(int i = 1; i < args.length; ++i) {
		bodylist.add(args[i]);
	    }
	    CronoType body = Cons.fromList(bodylist);
	    CronoType[] largs = new CronoType[arglist.size()];
	    largs = arglist.toArray(largs);
	    
	    LambdaFunction lambda = new LambdaFunction(lsyms,body,v.getEnv());
	    return lambda.run(v, largs); /*< -. - */
	}
	public String toString() {
	    return "let";
	}
    }),
    IF(new Function(new TypeId[]{CronoType.TYPEID, CronoType.TYPEID,
				 CronoType.TYPEID},
	    CronoType.TYPEID, 3, EvalType.NONE)
    {
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoType check = args[0].accept(v);
	    if(check != Nil.NIL) {
		return args[1].accept(v);
	    }
	    return args[2].accept(v);
	}
        public String toString() {
	    return "if";
	}
    }),
    EQ(new Function(new TypeId[]{CronoType.TYPEID, CronoType.TYPEID},
		    Cons.TYPEID, 2)
    {
	public CronoType run(Visitor v, CronoType[] args) {
	    return (args[0].equals(args[1])) ?
		TruthValue.T : Nil.NIL;
	}
	public String toString() {
	    return "=";
	}
    }),
    LT(new Function(new TypeId[]{CronoPrimitive.TYPEID, CronoPrimitive.TYPEID},
		    Cons.TYPEID, 2)
    {
	private static final String _bad_type =
	    "<: expected :primitive :primitive, got %s %s";
	
	private Number resolve(CronoType type) {
	    if(type instanceof CronoInteger) {
		return ((Long)((CronoInteger)type).value);
	    }else if(type instanceof CronoFloat) {
		return  ((Double)((CronoFloat)type).value);
	    }else if(type instanceof CronoCharacter) {
		return ((Long)((long)((CronoCharacter)type).ch));
	    }
	    return null;
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof CronoPrimitive &&
		 args[1] instanceof CronoPrimitive)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[1].typeId());
	    }
	    
	    Number lhs = resolve(args[0]);
	    Number rhs = resolve(args[1]);
	    if(lhs instanceof Double || rhs instanceof Double) {
		return (lhs.doubleValue() < rhs.doubleValue()) ?
		    TruthValue.T : Nil.NIL;
	    }
	    return (lhs.longValue() < rhs.longValue()) ?
		TruthValue.T : Nil.NIL;
	}
	public String toString() {
	    return "<";
	}
    }),
    GT(new Function(new TypeId[]{CronoPrimitive.TYPEID, CronoPrimitive.TYPEID},
		    Cons.TYPEID, 2)
    {
	private static final String _bad_type =
	    ">: expected :primitive :primitive, got %s %s";
	
	private Number resolve(CronoType type) {
	    if(type instanceof CronoInteger) {
		return ((Long)((CronoInteger)type).value);
	    }else if(type instanceof CronoFloat) {
		return  ((Double)((CronoFloat)type).value);
	    }else if(type instanceof CronoCharacter) {
		return ((Long)((long)((CronoCharacter)type).ch));
	    }
	    return null;
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof CronoPrimitive &&
		 args[1] instanceof CronoPrimitive)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[1].typeId());
	    }
	    
	    Number lhs = resolve(args[0]);
	    Number rhs = resolve(args[1]);
	    if(lhs instanceof Double || rhs instanceof Double) {
		return (lhs.doubleValue() > rhs.doubleValue()) ?
		    TruthValue.T : Nil.NIL;
	    }
	    return (lhs.longValue() > rhs.longValue()) ?
		TruthValue.T : Nil.NIL;
	}
	public String toString() {
	    return ">";
	}
    }),
    ADD(new Function(new TypeId[]{CronoNumber.TYPEID, CronoNumber.TYPEID},
		     CronoNumber.TYPEID, 2)
    {
	private static final String _bad_type =
	    "+: expected types :number :number, got %s %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[0].typeId());
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
    SUB(new Function(new TypeId[]{CronoNumber.TYPEID, CronoNumber.TYPEID},
		     CronoNumber.TYPEID, 2)
    {
	private static final String _bad_type =
	    "-: expected types :number :number, got %s %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[1].typeId());
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
    MUL(new Function(new TypeId[]{CronoNumber.TYPEID, CronoNumber.TYPEID},
		     CronoNumber.TYPEID, 2)
    {
	private static final String _bad_type =
	    "*: expected types :number :number, got %s %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[1].typeId());
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
    DIV(new Function(new TypeId[]{CronoNumber.TYPEID, CronoNumber.TYPEID},
		     CronoNumber.TYPEID, 2)
    {
	private static final String _bad_type =
	    "/: expected types :number :number, got %s %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    CronoNumber lhs = null, rhs = null;
	    if(!(args[0] instanceof CronoNumber &&
		 args[1] instanceof CronoNumber)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[1].typeId());
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
    INT(new Function(new TypeId[]{CronoPrimitive.TYPEID},CronoInteger.TYPEID,1)
    {
	public static final String _bad_type =
	    "INT: Excepted one of :char, :float, or :int; got %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    if(args[0] instanceof CronoCharacter) {
		return new CronoInteger(((long)((CronoCharacter)args[0]).ch));
	    }else if(args[0] instanceof CronoFloat) {
		return new CronoInteger(((long)((CronoFloat)args[0]).value));
	    }else if(args[0] instanceof CronoInteger) {
		return args[0];
	    }
	    throw new InterpreterException(_bad_type, args[0].typeId());
	}
	public String toString() {
	    return "int";
	}
    }),
    CHAR(new Function(new TypeId[]{CronoPrimitive.TYPEID},
		      CronoCharacter.TYPEID, 1)
    {
	private static final String _bad_type =
	    "CHAR: expected one of :int, or :char; got %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    if(args[0] instanceof CronoInteger) {
		return new CronoCharacter((char)((CronoInteger)args[0]).value);
	    }else if(args[0] instanceof CronoFloat) {
		return new CronoCharacter((char)((CronoFloat)args[0]).value);
	    }else if(args[0] instanceof CronoCharacter) {
		return args[0];
	    }
	    throw new InterpreterException(_bad_type, args[0].typeId());
	}
	public String toString() {
	    return "char";
	}
    }),
    FLOAT(new Function(new TypeId[]{CronoPrimitive.TYPEID},CronoFloat.TYPEID,1)
    {
	private static final String _bad_type =
	    "FLOAT: expected one of :int, or :float; got %s";
	
	public CronoType run(Visitor v, CronoType[] args) {
	    if(args[0] instanceof CronoInteger) {
		return new CronoFloat((double)((CronoInteger)args[0]).value);
	    }else if(args[0] instanceof CronoCharacter) {
		return new CronoFloat((double)((CronoCharacter)args[0]).ch);
	    }else if(args[0] instanceof CronoFloat) {
		return args[0];
	    }
	    
	    throw new InterpreterException(_bad_type, args[0].typeId());
	}
	public String toString() {
	    return "float";
	}
    }),
    LOAD(new Function(new TypeId[]{CronoString.TYPEID}, CronoType.TYPEID, 1)
    {
	private static final String _bad_type =
	    "LOAD: expected :string, got %s";
	private static final String _file_not_found =
	    "LOAD: could not open file %s";
	private static final String _bad_parse =
	    "LOAD: error parsing file:\n%s";
	
        public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof CronoString)) {
		throw new InterpreterException(_bad_type, args[0].typeId());
	    }
	    InputStream is;
	    try {
		is = new FileInputStream(((CronoString)args[0]).image());
	    }catch(FileNotFoundException fnfe) {
		throw new InterpreterException(_file_not_found,
					       fnfe.getMessage());
	    }
	    
	    Parser p = new Parser(is);
	    CronoType program;
	    try {
		program = p.program(); /*< Fetch entire program */
	    }catch(ParseException pe) {
		throw new InterpreterException(_bad_parse, pe.getMessage());
	    }
	    
	    System.err.printf("LOAD: interpreting file %s\n", args[0]);
	    return program.accept(v);
        }
        public String toString() {
	    return "load";
        }
    }),
    PRINT(new Function(new TypeId[]{CronoType.TYPEID}, Nil.TYPEID, 1, true)
    {
        public CronoType run(Visitor v, CronoType[] args) {
	    for(int i = 0; i < args.length; ++i) {
		if(args[i] instanceof CronoString) {
		    System.out.print(((CronoString)args[i]).image());
		}else if(args[i] instanceof CronoCharacter) {
		    System.out.print(((CronoCharacter)args[i]).ch);
		}else {
		    System.out.print(args[i]);
		}
	    }
	    return Nil.NIL;
        }
        public String toString() {
	    return "print";
        }
    }),
    PRINTLN(new Function(new TypeId[]{CronoType.TYPEID}, Nil.TYPEID, 1, true)
    {
	public CronoType run(Visitor v, CronoType[] args) {
	    PRINT.function.run(v, args);
	    System.out.println();
	    return Nil.NIL;
	}
	public String toString() {
	    return "println";
	}
    }),
	/*
    STRUCT(new Function() {
        public int arity() {
	    return 1;
        }
        public CronoType run(Visitor v, CronoType[] args) {
	    
        }
        public String toString() {
        }
    }),
    SUBSTRUCT(new Function() {
        public int arity() {
        }
        public CronoType run(Visitor v, CronoType[] args) {
        }
        public String toString() {
        }
    }),
    NEWSTRUCT(new Function() {
	public static final String _bad_type =
	    "NEWSTRUCT: expected types :symbol :cons, got %s %s";
        public int arity() {
	    return 2;
        }
        public CronoType run(Visitor v, CronoType[] args) {
	    if(!(args[0] instanceof Symbol && args[1] instanceof Cons)) {
		throw new InterpreterException(_bad_type, args[0].typeId(),
					       args[1].typeId());
	    }
	    
	    
        }
        public String toString() {
        }
    }),
	*/
    EVAL(new Function(new TypeId[]{CronoString.TYPEID}, Cons.TYPEID, 1)
    {
        public CronoType run(Visitor v, CronoType[] args) {
	    StringReader reader;
	    reader = new StringReader(((CronoString)args[0]).image());
	    
	    Parser p = new Parser(reader);
	    
	    try {
		return p.program().accept(v);
	    }catch(ParseException pe) {
		throw new InterpreterException("EVAL: parse error\n%s",
					       pe.getMessage());
	    }
        }
        public String toString() {
	    return "eval";
        }
    }),
    ;
    
    public final Function function;
    private CronoFunction(Function fun) {
	this.function = fun;
    }
}
