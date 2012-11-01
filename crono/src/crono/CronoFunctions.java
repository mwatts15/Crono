package crono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crono.AbstractSyntax.CronoFunction;
import crono.AbstractSyntax.CronoType;
import crono.Cons;
import crono.Environment;
import crono.Interpreter;
import crono.Parser;

import static crono.CronoOptions.err;
import static crono.Nil.NIL;

/**
 * Container class for all built in functions.
 */
public enum CronoFunctions {
 CONS(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      /*
      if (args.length > 2) {
        err("too many arguments given to CONS: %s", Arrays.toString(args));
      } else if (args.length < 2) {
        err("too few arguments given to CONS: %s", Arrays.toString(args));
      }
      */

      CronoType car = args[0];
      CronoType cdr = args[1];
      CronoType result;

      if (cdr instanceof Cons) {
        result = Cons.cons(car, (Cons)cdr);
      } else {
        result = new Cons(car, cdr);
      }
      return result;
    }

    public String toString() {
      return "CONS";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return true;
    }
  }),
  CAR(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length > 1) {
        err("too many arguments given to CAR: %s", Arrays.toString(args));
      } else if (args.length < 1) {
        err("too few arguments given to CAR: %s", Arrays.toString(args));
      }

      CronoType arg = args[0];

      if (!(arg instanceof Cons)) {
        err("%s is not a list", arg);
      }

      Cons cons = (Cons)arg;
      return cons.car();
    }

    public String toString() {
      return "CAR";
    }

    public int arity()
    {
        return 1;
    }
    public boolean evalArgs() {
      return true;
    }
  }),
  CDR(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length > 1) {
        err("too many arguments given to CDR: %s", Arrays.toString(args));
      } else if (args.length < 1) {
        err("too few arguments given to CDR: %s", Arrays.toString(args));
      }

      CronoType arg = args[0];

      if (!(arg instanceof Cons)) {
        err("%s is not a list", arg);
      }

      Cons cons = (Cons)arg;
      return cons.cdr();
    }

    public String toString() {
      return "CDR";
    }
    public int arity()
    {
        return 1;
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  QUOTE(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length > 1) {
        err("too many arguments given to QUOTE: %s", Arrays.toString(args));
      } else if (args.length < 1) {
        err("too few arguments given to QUOTE: %s", Arrays.toString(args));
      }
      return args[0];
    }

    public String toString() {
      return "QUOTE";
    }
    public int arity()
    {
        return 1;
    }

    public boolean evalArgs() {
      return false;
    }
  }),
  DEFINE(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 3) {
        err("too few arguments given to DEFINE: %s", Arrays.toString(args));
      }
      if (!(args[0] instanceof Symbol)) {
        err("DEFINE: the name of a function must be a symbol, not %s", args[0]);
      }
      Symbol name = (Symbol)args[0];
      LambdaFunction lambda = (LambdaFunction)LAMBDA.function.run(
        Arrays.copyOfRange(args,1,args.length), environment);
      environment.put(name, lambda);
      return lambda;
    }

    public String toString() {
      return "DEFINE";
    }
    public int arity()
    {
        return 3;
    }

    public boolean evalArgs() {
      return false;
    }
  }),
  LAMBDA(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments given to LAMBDA: %s", Arrays.toString(args));
      }
      if (!(args[0] instanceof Cons)) {
        err("LAMBDA: lambda list must be a cons, not %s", args[0]);
      }
      List<Symbol> argList = new ArrayList<Symbol>();
      Cons largs = (Cons)args[0];
      for(CronoType ct : largs) {
        if (ct instanceof Symbol) {
          argList.add((Symbol)ct);
        } else {
          err("LAMBDA: a lambda list may only contain symbols, not %s", ct);
        }
      }
      List<CronoType> body = new ArrayList<CronoType>();
      for(int i = 1; i < args.length; i++) {
        body.add(args[i]);
      }
      return new LambdaFunction(body, argList, environment);
    }

    public String toString() {
      return "\\";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return false;
    }
  }),
  LET(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments to LET: %s", Arrays.toString(args));
      }
      if (!(args[0] instanceof Cons)) {
        err("LET: substitution list must be a cons, not %s", args[0]);
      }
      Cons subList = (Cons)args[0];
      // Clone current environment and put in LambdaFunction to return.
      Environment env = new Environment(environment);
      for(CronoType ct : subList) {
        if (!(ct instanceof Cons)) {
          err("LET: expected Cons in substitution list, not %s", args[0]);
        }
        Cons sub = (Cons)ct;
        CronoType car = sub.car();
        if (!(car instanceof Symbol)) {
          err("LET: can only substitute symbols, found %s", args[0]);
        }
        Symbol key = (Symbol)car;
        if (key.equals(Symbol.valueOf("t"))) {
          err("LET: T is a constant, may not be used as a variable");
        }
        CronoType cdr = sub.cdr();
        if (!(cdr instanceof Cons)) {
          err("LET: substitution list must have two elements");
        }
        CronoType val = Interpreter.eval(((Cons)cdr).car(), environment);
        env.put(key, val);
      }
      CronoOptions.dprint("(Converting LET to LAMBDA)\n");
      List<CronoType> body = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
      LambdaFunction lambda = new LambdaFunction(body);
      return Interpreter.run(lambda, env);
    }

    public String toString() {
      return "LET";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return false;
    }
  }),
  IF(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 3) {
        err("too few arguments to IF: %s", Arrays.toString(args));
      }
      if (args.length > 3) {
        err("too many arguments to IF: %s", Arrays.toString(args));
      }
      if (Interpreter.eval(args[0], environment) == NIL) {
        return Interpreter.eval(args[2], environment);
      }else{
        return Interpreter.eval(args[1], environment);
      }
    }

    public String toString() {
      return "IF";
    }
    public int arity()
    {
        return 3;
    }

    public boolean evalArgs() {
      return false;
    }
  }),
  ADD(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if(args.length <= 1) {
	  err("too few arguments to SUM: %s", Arrays.toString(args));
      }
      long lsum = 0;
      double dsum = 0.0;
      boolean promote = false;
      for (CronoType ct : args){
	if(ct instanceof CronoNumber) {
	    lsum += ((CronoNumber)ct).num;
	}else if(ct instanceof CronoFloat) {
	    promote = true;
	    dsum += ((CronoFloat)ct).num;
	}else {
	    err("%s is not a number", ct);
	}
      }
      if(promote) {
	  return new CronoFloat(((double)lsum) + dsum);
      }
      return new CronoNumber(lsum);
    }

    public String toString() {
      return "+";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return true;
    }
  }),
  SUB(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 1) {
        err("too few arguments to -: %s", Arrays.toString(args));
      } else if (args.length == 1) {
	if(args[0] instanceof CronoNumber) {
	  return new CronoNumber(-((CronoNumber)args[0]).num);
	}else if(args[0] instanceof CronoFloat) {
	  return new CronoFloat(-((CronoFloat)args[0]).num);
	}else {
	  err("%s is not a number", args[0]);
	}
      }

      double ddiff = 0.0;
      long ldiff = 0;
      boolean promote = false;
      if(args[0] instanceof CronoNumber) {
	  ldiff += ((CronoNumber)args[0]).num;
      }else if(args[0] instanceof CronoFloat) {
	  promote = true;
	  ddiff += ((CronoFloat)args[0]).num;
      }else {
	  err("%s is not a number", args[0]);
      }

      for(int i = 1; i < args.length; ++i) {
	if(args[i] instanceof CronoNumber) {
	  ldiff -= ((CronoNumber)args[i]).num;
	}else if(args[i] instanceof CronoFloat) {
	  promote = true;
	  ddiff -= ((CronoFloat)args[i]).num;
	}else {
	  err("%s is not a number", args[i]);
	}
      }
      if(promote) {
	  return new CronoFloat(((double)ldiff) + ddiff);
      }
      return new CronoNumber(ldiff);
    }

    public String toString() {
      return "-";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return true;
    }
  }),
  MULT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      long lprod = 1;
      double dprod = 1.0;
      boolean promote = false;
      for (CronoType ct : args){
	if(ct instanceof CronoNumber) {
	  lprod *= ((CronoNumber)ct).num;
	}else if(ct instanceof CronoFloat) {
	  promote = true;
	  dprod *= ((CronoFloat)ct).num;
	}else {
	  err("%s is not a number", ct);
	}
      }
      if(promote) {
	return new CronoFloat(((double)lprod) * dprod);
      }
      return new CronoNumber(lprod);
    }

    public String toString() {
      return "*";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return true;
    }
  }),
  DIV(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments to /: %s", Arrays.toString(args));
      }

      double result = 1.0;
      boolean promote = false;
      if(args[0] instanceof CronoNumber) {
	result = ((double)((CronoNumber)args[0]).num);
      }else if(args[0] instanceof CronoFloat) {
	  promote = true;
	result = ((CronoFloat)args[0]).num;
      }else {
	err("%s is not a number", args[0]);
      }

      for(int i = 1; i < args.length; ++i) {
	if(args[i] instanceof CronoNumber) {
	  result /= ((double)((CronoNumber)args[i]).num);
	}else if(args[i] instanceof CronoFloat) {
	  promote = true;
	  result /= ((CronoFloat)args[i]).num;
	}else {
	  err("%s is not a number", args[i]);
	}
      }
      if(promote) {
	  return new CronoFloat(result);
      }
      return new CronoNumber((long)result);
    }

    public String toString() {
      return "/";
    }

    public int arity()
    {
        return 2;
    }
    public boolean evalArgs() {
      return true;
    }
  }),
  EQ(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments to =: %s", Arrays.toString(args));
      }

      boolean eq = true;
      CronoType prev = args[0];
      for(int i = 1; eq && i < args.length; i++) {
        eq = prev.equals(args[i]);
        prev = args[i];
      }

      if (eq) {
        return Symbol.valueOf("T");
      } else {
        return NIL;
      }
    }

    public int arity()
    {
        return 2;
    }
    public String toString() {
      return "=";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  LT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments to <: %s", Arrays.toString(args));
      }

      boolean lt = true;
      if (!(args[0] instanceof CronoNumber || args[0] instanceof CronoFloat)) {
        err("%s is not a number", args[0]);
      }
      boolean cmpdouble = false;
      CronoType prev = (CronoType)args[0];
      if(prev instanceof CronoFloat) {
	  cmpdouble = true;
      }
      for(int i = 1; lt && i < args.length; i++) {
	if(args[i] instanceof CronoNumber) {
	  if(cmpdouble) {
	    lt = ((CronoFloat)prev).num < ((double)((CronoNumber)args[i]).num);
	  }else {
	    lt = ((CronoNumber)prev).num < ((CronoNumber)args[i]).num;
	  }
	}else if(args[i] instanceof CronoFloat) {
	  if(cmpdouble) {
	    lt = ((CronoFloat)prev).num < ((CronoFloat)args[i]).num;
	  }else {
	    lt = ((double)((CronoNumber)prev).num) < ((CronoFloat)args[i]).num;
	  }
	}else {
	    err("%s is not a number", args[i]);
	}
        prev = args[i];
      }

      if (lt) {
        return Symbol.valueOf("T");
      } else {
        return NIL;
      }
    }
    public int arity()
    {
        return 2;
    }

    public String toString() {
      return "<";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  GT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments to >: %s", Arrays.toString(args));
      }

      boolean gt = true;
      if (!(args[0] instanceof CronoNumber || args[0] instanceof CronoFloat)) {
        err("%s is not a number", args[0]);
      }
      boolean cmpdouble = false;
      CronoType prev = (CronoType)args[0];
      if(prev instanceof CronoFloat) {
	  cmpdouble = true;
      }
      for(int i = 1; gt && i < args.length; i++) {
	if(args[i] instanceof CronoNumber) {
	  if(cmpdouble) {
	    gt = ((CronoFloat)prev).num > ((double)((CronoNumber)args[i]).num);
	  }else {
	    gt = ((CronoNumber)prev).num > ((CronoNumber)args[i]).num;
	  }
	}else if(args[i] instanceof CronoFloat) {
	  if(cmpdouble) {
	    gt = ((CronoFloat)prev).num > ((CronoFloat)args[i]).num;
	  }else {
	    gt = ((double)((CronoNumber)prev).num) > ((CronoFloat)args[i]).num;
	  }
	}else {
	  err("%s is not a number", args[i]);
	}
        prev = args[i];
      }

      if (gt) {
        return Symbol.valueOf("T");
      } else {
        return NIL;
      }
    }

    public int arity()
    {
        return 2;
    }

    public String toString() {
      return ">";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  SET(new CronoFunction() {
      public CronoType run(CronoType[] args, Environment environment) {
          if (args.length < 2) {
              err("too few arguments to SET: %s", Arrays.toString(args));
          }
          if (args.length > 2) {
              err("too many arguments to SET: %s", Arrays.toString(args));
          }
          if (!(args[0] instanceof Symbol)) {
              err("SET: can only use symbols as keys, got %s", args[0]);
          }
          String key = ((Symbol)args[0]).toString();
          CronoType condition = Interpreter.eval(args[1], environment);
          boolean value = (condition != NIL);

          // TODO: possibly turn CronoOptions into an enum and use reflection to
          //       set values.
          // Wow, this is annoying to update.
          if ("dprint_enable".equals(key)) {
              CronoOptions.DPRINT_ENABLE = value;
          } else if ("dprint_indent".equals(key)) {
              CronoOptions.DPRINT_INDENT = value;
          } else if ("dprint_show_atom_eval".equals(key)) {
              CronoOptions.DPRINT_SHOW_ATOM_EVAL = value;
          } else if ("environment_show".equals(key)) {
              CronoOptions.ENVIRONMENT_SHOW = value;
          } else if ("environment_show_builtin".equals(key)) {
              CronoOptions.ENVIRONMENT_SHOW_BUILTIN = value;
          } else if ("environment_dynamic".equals(key)) {
              CronoOptions.ENVIRONMENT_DYNAMIC = value;
          } else if ("environment_show_types".equals(key)) {
              CronoOptions.ENVIRONMENT_SHOW_TYPES = value;
          } else if ("environment_multiline".equals(key)) {
              CronoOptions.ENVIRONMENT_MULTILINE = value;
          } else if ("lambda_show_closure".equals(key)) {
              CronoOptions.LAMBDA_SHOW_CLOSURE = value;
          } else if ("parser_dprint".equals(key)) {
              CronoOptions.PARSER_DPRINT = value;
          }

          return NIL;
      }

      public String toString() {
          return "SET";
      }

    public int arity()
    {
        return 2;
    }
      public boolean evalArgs() {
          return false;
      }
  }),
  LOAD(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 0) {
        err("too few arguments to LOAD: %s", Arrays.toString(args));
      }
      for(CronoType ct : args) {
        if (!(ct instanceof Symbol)) {
          err("LOAD: %s is not a symbol", ct);
        }

        try {
          String filename = ((Symbol)ct).toString();
          InputStream in = new FileInputStream(filename);
          Parser parser = new Parser(in);
          parser.prog(environment);
        } catch (FileNotFoundException e) {
          // Do nothing.
        } catch (ParseException e) {
        }
      }

      return NIL;
    }

    public String toString() {
      return "LOAD";
    }
    public int arity()
    {
        return 1;
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  PRINT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      for(int i = 0; i < args.length; i++) {
        System.out.print(args[i]);
        if (i < args.length - 1) {
          System.out.print(" ");
        }
      }
      System.out.print("\n");

      return NIL;
    }

    public String toString() {
      return "PRINT";
    }
    public int arity()
    {
        return 2;
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  PRINTSTR(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment env) {
	if(args.length != 1) { /*TODO: Remove this if/when numArgs is used */
	    err("Wrong number of args (%d != 1) for PRINTSTR", args.length);
	}
	if(args[0] instanceof Symbol) {
	    /* Hack Hack Hack Hack Hack */
	    args[0] = env.get((Symbol)args[0]);
	}
	if(!(args[0] instanceof Cons)) {
	    err("%s is not a cons of characters", args[0]);
	}
	StringBuilder builder = new StringBuilder();
	for(CronoType t : ((Cons)args[0])) {
	    if(!(t instanceof CronoCharacter)) {
		err("Type Error: %s is not a CronoCharacter", args[0]);
	    }
	    builder.append(((CronoCharacter)t).ch);
	}
	System.out.print(builder.toString());
	return Nil.NIL;
    }
    public String toString() {
      return "PRINTSTR";
    }
    public int numArgs() {
	return 1;
    }
    public boolean canCurry() {
	return false;
    }
    public boolean evalArgs() {
	return false;
    }
  }),
  STRUCT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment env) {
	/* Define a new struct */
	if(!(args[0] instanceof Symbol)) {
	    err("STRUCT: %s is not a valid struct symbol", args[0]);
	}
	CronoStruct struct = new CronoStruct(args[0].toString());
	for(int i = 1; i < args.length; ++i) {
	    if(args[i] instanceof Symbol) {
		struct.put(args[i].toString(), Nil.NIL);
	    }else if(args[i] instanceof Cons) {
		Cons c = (Cons)args[i];
		CronoType sym = c.car();
		if(!(sym instanceof Symbol)) {
		    err("%s is not a valid field symbol", sym);
		}
		CronoType val = c.cdr();
		if(val instanceof Cons) {
		    val = ((Cons)val).car();
		}
		struct.put(sym.toString(), Interpreter.eval(val, env));
	    }else {
		err("%s is not a valid field symbol", args[i]);
	    }
	}
	
	env.put((Symbol)args[0], struct);
	
	return struct; /*< You can actually modify the struct definition */
    }
    public String toString() {
      return "STRUCT";
    }
    public int numArgs() {
      return -2;
    }
    public boolean canCurry() {
      return false;
    }
    public boolean evalArgs() {
      return false;
    }
  }),
  SUBSTRUCT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment env) {
	if(!(args[0] instanceof Symbol)) {
	    err("SUBSTRUCT: %s is not a valid struct symbol", args[0]);
	}
	if(!(args[1] instanceof Symbol)) {
	    err("SUBSTRUCT: %s is not a valid struct symbol", args[1]);
	}
	
	CronoStruct parent = (CronoStruct)env.get((Symbol)args[1]);
	CronoStruct struct = new CronoStruct(args[0].toString(), parent);
	for(int i = 2; i < args.length; ++i) {
	    if(args[i] instanceof Symbol) {
		struct.put(args[i].toString(), Nil.NIL);
	    }else if(args[i] instanceof Cons) {
		Cons c = (Cons)args[i];
		CronoType sym = c.car();
		if(!(sym instanceof Symbol)) {
		    err("%s is not a valid field symbol", sym);
		}
		CronoType val = c.cdr();
		if(val instanceof Cons) {
		    val = ((Cons)val).car();
		}
		struct.put(sym.toString(), Interpreter.eval(val, env));
	    }else {
		err("%s is not a valid field symbol", args[i]);
	    }
	}
	
	env.put((Symbol)args[0], struct);
	
	return struct; /*< You can actually modify the struct definition */
    }
    public String toString() {
      return "SUBSTRUCT";
    }
    public int numArgs() {
      return -3;
    }
    public boolean canCurry() {
      return false;
    }
    public boolean evalArgs() {
      return false;
    }
  }),
  NEWSTRUCT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment env) {
	if(!(args[0] instanceof Symbol)) {
	    err("NEWSTRUCT: %s is not a valid struct symbol", args[0]);
	}
	CronoStruct struct = ((CronoStruct)env.get((Symbol)args[0])).copy();
	for(int i = 1; i < args.length; ++i) {
	    if(args[i] instanceof Symbol) {
		struct.put(args[i].toString(), Nil.NIL);
	    }else if(args[i] instanceof Cons) {
		Cons c = (Cons)args[i];
		CronoType sym = c.car();
		if(!(sym instanceof Symbol)) {
		    err("%s is not a valid field symbol", sym);
		}
		CronoType val = c.cdr();
		if(val instanceof Cons) {
		    val = ((Cons)val).car();
		}
		struct.put(sym.toString(), Interpreter.eval(val, env));
	    }else {
		err("%s is not a valid field symbol", args[i]);
	    }
	}
	
	return struct;
    }
    public String toString() {
      return "NEWSTRUCT";
    }
    public int numArgs() {
      /* Newstruct just takes the name of the struct to make and a list
       * of field value pairs */
      return 2;
    }
    public boolean canCurry() {
      /* In general, currying one arg functions shouldn't happen */
      return false;
    }
    public boolean evalArgs() {
      return false;
    }
  }),
  ;

  public final CronoFunction function;

  private CronoFunctions(CronoFunction function) {
    this.function = function;
  }
}
