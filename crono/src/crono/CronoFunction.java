package crono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import crono.type.*;

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
        public CronoType run(Visitor v, List<CronoType> args)
        {
            CronoType lhs = args.remove(0);
            CronoType rhs = args.remove(0);
            return new Cons(lhs, rhs);
        }
        public String toString()
        {
            return "cons";
        }
    }),
    CAR(new Function(new TypeId[]{Cons.TYPEID}, CronoType.TYPEID, 1)
    {
        public CronoType run(Visitor v, List<CronoType> args)
        {
            Cons list = (Cons) args.remove(0);
            return list.car();
        }
        public String toString()
        {
            return "car";
        }
    }),
    CDR(new Function(new TypeId[]{Cons.TYPEID}, CronoType.TYPEID, 1)
    {
        public CronoType run(Visitor v, List<CronoType> args)
        {
            Cons list = (Cons) args.remove(0);
            return list.cdr();
        }
        public String toString()
        {
            return "cdr";
        }
    }),
    LIST(new Function(new TypeId[]{CronoType.TYPEID}, Cons.TYPEID, 1, true)
    {
        public CronoType run(Visitor v, List<CronoType> args) {
            return Cons.fromList(args);
        }
        public String toString() {
            return "list";
        }
    }),
    GET(new Function(new TypeId[]{CronoArray.TYPEID, CronoInteger.TYPEID},
		     CronoType.TYPEID, 2)
    {
        public CronoType run(Visitor v, List<CronoType> args) {
            CronoArray a = (CronoArray) args.remove(0);
            int index = (int)((CronoInteger)args.remove(0)).value;
            return a.get(index);
        }
        public String toString() {
            return "get";
        }
    }),
    PUT(new Function(new TypeId[]{CronoArray.TYPEID, CronoInteger.TYPEID,
				  CronoType.TYPEID},
	    CronoType.TYPEID, 3)
    {
        public CronoType run(Visitor v, List<CronoType> args) {
            CronoArray a = (CronoArray) args.remove(0);
            int index = (int)((CronoInteger)args.remove(0)).value;
            CronoType new_val = args.remove(0);
            /* Maybe return nil instead? */
            a.put(index, new_val);
            return a;
        }
        public String toString() {
            return "put";
        }
    }),
    APPEND(new Function(new TypeId[]{CronoArray.TYPEID, CronoType.TYPEID},
			CronoType.TYPEID, 2)
    {
        public CronoType run(Visitor v, List<CronoType> args) {
            CronoArray a = (CronoArray) args.remove(0);
            a.append(args.remove(0));
            return a;
        }
        public String toString() {
            return "append";
        }
    }),
    EQ(new Function(new TypeId[]{CronoType.TYPEID, CronoType.TYPEID},
            Cons.TYPEID, 2)
    {
        public CronoType run(Visitor v, List<CronoType> args) {
            CronoType lhs = args.remove(0);
            CronoType rhs = args.remove(0);
            return (lhs.equals(rhs)) ? TruthValue.T : Nil.NIL;
        }
        public String toString() {
            return "=";
        }
    }),
    LT(new Function(new TypeId[]{CronoPrimitive.TYPEID, CronoPrimitive.TYPEID},
            Cons.TYPEID, 2)
    {
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
        public CronoType run(Visitor v, List<CronoType> args) {
            Number lhs = resolve(args.remove(0));
            Number rhs = resolve(args.remove(0));
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
    ADD(new Function(new TypeId[]{CronoNumber.TYPEID, CronoNumber.TYPEID},
             CronoNumber.TYPEID, 2)
    {
        public CronoType run(Visitor v, List<CronoType> args)
        {
            CronoNumber lhs = (CronoNumber) args.remove(0);
            CronoNumber rhs = (CronoNumber) args.remove(0);

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
    MUL(new Function(new TypeId[]{CronoNumber.TYPEID, CronoNumber.TYPEID},
             CronoNumber.TYPEID, 2)
    {
        public CronoType run(Visitor v, List<CronoType> args)
        {
            CronoNumber lhs = (CronoNumber) args.remove(0);
            CronoNumber rhs = (CronoNumber) args.remove(0);
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
        public CronoType run(Visitor v, List<CronoType> args) {
            CronoNumber lhs = (CronoNumber) args.remove(0);
            CronoNumber rhs = (CronoNumber) args.remove(0);

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
    //INT(new Function(new TypeId[]{CronoPrimitive.TYPEID},CronoInteger.TYPEID,1)
    //{
	//public static final String _bad_type =
		//"INT: Excepted one of :char, :float, or :int; got %s";

	//public CronoType run(Visitor v, List<CronoType> args) {
		//if(args[0] instanceof CronoCharacter) {
		//return new CronoInteger(((long)((CronoCharacter)args[0]).ch));
		//}else if(args[0] instanceof CronoFloat) {
		//return new CronoInteger(((long)((CronoFloat)args[0]).value));
		//}else if(args[0] instanceof CronoInteger) {
		//return args[0];
		//}
		//throw new InterpreterException(_bad_type, args[0].typeId());
	//}
	//public String toString() {
		//return "int";
	//}
    //}),
    //CHAR(new Function(new TypeId[]{CronoPrimitive.TYPEID},
			  //CronoCharacter.TYPEID, 1)
    //{
	//private static final String _bad_type =
		//"CHAR: expected one of :int, or :char; got %s";

	//public CronoType run(Visitor v, List<CronoType> args) {
		//if(args[0] instanceof CronoInteger) {
		//return new CronoCharacter((char)((CronoInteger)args[0]).value);
		//}else if(args[0] instanceof CronoFloat) {
		//return new CronoCharacter((char)((CronoFloat)args[0]).value);
		//}else if(args[0] instanceof CronoCharacter) {
		//return args[0];
		//}
		//throw new InterpreterException(_bad_type, args[0].typeId());
	//}
	//public String toString() {
		//return "char";
	//}
    //}),
    //FLOAT(new Function(new TypeId[]{CronoPrimitive.TYPEID},CronoFloat.TYPEID,1)
    //{
	//private static final String _bad_type =
		//"FLOAT: expected one of :int, or :float; got %s";

	//public CronoType run(Visitor v, List<CronoType> args) {
		//if(args[0] instanceof CronoInteger) {
		//return new CronoFloat((double)((CronoInteger)args[0]).value);
		//}else if(args[0] instanceof CronoCharacter) {
		//return new CronoFloat((double)((CronoCharacter)args[0]).ch);
		//}else if(args[0] instanceof CronoFloat) {
		//return args[0];
		//}

		//throw new InterpreterException(_bad_type, args[0].typeId());
	//}
	//public String toString() {
		//return "float";
	//}
    //}),
    //LOAD(new Function(new TypeId[]{CronoString.TYPEID}, CronoType.TYPEID, 1)
    //{
	//private static final String _bad_type =
		//"LOAD: expected :string, got %s";
	//private static final String _file_not_found =
		//"LOAD: could not open file %s";
	//private static final String _bad_parse =
		//"LOAD: error parsing file:\n%s";

	//private CronoType loadLisp(Visitor v, String fname) {
		//InputStream is = null;
		//try {
		//is = new FileInputStream(fname);
		//}catch(FileNotFoundException fnfe) {
		//throw new InterpreterException(_file_not_found,
						   //fnfe.getMessage());
		//}
		//Parser p = new Parser(is);
		//CronoType program = null;
		//try {
		//program = p.program();
		//}catch(ParseException pe) {
		//throw new InterpreterException(_bad_parse, pe.getMessage());
		//}
		//return program.accept(v);
	//}
	//private CronoType loadPackage(Visitor v, String fname) {
		//CronoPackage pack = CronoPackage.load(fname);
		//Environment env = v.getEnv();
		//Function[] funcs = pack.functions();
		//if(funcs != null) {
		//for(Function f : funcs) {
			//env.put(new Symbol(f.toString()), f);
		//}
		//}
		//TypeId[] types = pack.types();
		//if(types != null) {
		//for(TypeId t : types) {
			//env.put(t);
		//}
		//}

		//CronoPackage.SymbolPair[] syms = pack.symbols();
		//if(syms != null) {
		//for(CronoPackage.SymbolPair s : syms) {
			//env.put(s.sym, s.type);
		//}
		//}

		//return TruthValue.T;
	//}

        //public CronoType run(Visitor v, List<CronoType> args) {
		//if(!(args[0] instanceof CronoString)) {
		//throw new InterpreterException(_bad_type, args[0].typeId());
		//}
		//String fname = ((CronoString)args[0]).toString();
		//String lname = fname.toLowerCase();
		//if(lname.endsWith(".lisp") || lname.endsWith(".crono")) {
		//return loadLisp(v, fname);
		//}
		//return loadPackage(v, fname);
        //}
        //public String toString() {
		//return "load";
        //}
    //}),
    //PRINT(new Function(new TypeId[]{CronoType.TYPEID}, Nil.TYPEID, 1, true)
    //{
        //public CronoType run(Visitor v, List<CronoType> args) {
		//for(int i = 0; i < args.length; ++i) {
		//System.out.print(args[i].toString());
		//}
		//return Nil.NIL;
        //}
        //public String toString() {
		//return "print";
        //}
    //}),
    //PRINTLN(new Function(new TypeId[]{CronoType.TYPEID}, Nil.TYPEID, 1, true)
    //{
	//public CronoType run(Visitor v, List<CronoType> args) {
		//PRINT.function.run(v, args);
		//System.out.println();
		//return Nil.NIL;
	//}
	//public String toString() {
		//return "println";
	//}
    //}),
    //STRUCT(new Function(new TypeId[]{Symbol.TYPEID}, CronoStruct.TYPEID, 1,
			//false, EvalType.NONE)
    //{
	//private static final String _name = "struct";
	//private static final String _no_struct_in_scope =
		//"struct: No structure definition for %s in scope";
        //public CronoType run(Visitor v, List<CronoType> args) {
		//CronoStruct struct = v.getEnv().getStruct((Symbol)args[0]);
		//if(struct == null) {
		//throw new InterpreterException(_no_struct_in_scope, args[0]);
		//}

		//return struct.copy();
        //}
        //public String toString() {
		//return _name;
        //}
    //}),
    //SUBSTRUCT(new Function(new TypeId[]{Symbol.TYPEID, Symbol.TYPEID,
					//Cons.TYPEID},
					   //Nil.TYPEID, 3, false, EvalType.NONE)
    //{
	//private static final String _name = "substruct";
	//private static final String _no_struct_in_scope =
		//"substruct: no structure definition for %s in scope";

        //public CronoType run(Visitor v, List<CronoType> args) {
		//CronoStruct par = v.getEnv().getStruct((Symbol)args[1]);
		//if(par == null) {
		//throw new InterpreterException(_no_struct_in_scope, args[1]);
		//}
		//Map<String, CronoStruct.Field> fields;
		//fields = CronoStruct.BuildFieldMap(_name, (Cons)args[2]);
		//v.getEnv().put(new CronoStruct(args[0].toString(), fields, par));
		//return Nil.NIL;
        //}
        //public String toString() {
		//return _name;
        //}
    //}),
    //DEFSTRUCT(new Function(new TypeId[]{Symbol.TYPEID, Cons.TYPEID},
			   //Nil.TYPEID, 2, false, EvalType.NONE)
    //{
	//private static final String _name = "defstruct";
        //public CronoType run(Visitor v, List<CronoType> args) {
		//Map<String, CronoStruct.Field> fields;
		//fields = CronoStruct.BuildFieldMap(_name, (Cons)args[1]);
		//v.getEnv().put(new CronoStruct(args[0].toString(), fields));
		//return Nil.NIL;
        //}
        //public String toString() {
		//return _name;
        //}
    //}),
    //EVAL(new Function(new TypeId[]{CronoString.TYPEID}, Cons.TYPEID, 1)
    //{
        //public CronoType run(Visitor v, List<CronoType> args) {
		//StringReader reader;
		//reader = new StringReader(((CronoString)args[0]).toString());

		//Parser p = new Parser(reader);

		//try {
		//return p.program().accept(v);
		//}catch(ParseException pe) {
		//throw new InterpreterException("EVAL: parse error\n%s",
						   //pe.getMessage());
		//}
    //}),
    ;

    public final Function function;
    private CronoFunction(Function fun) {
	this.function = fun;
    }
}
