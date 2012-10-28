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
      if (args.length > 2) {
        err("too many arguments given to CONS: %s", Arrays.toString(args));
      } else if (args.length < 2) {
        err("too few arguments given to CONS: %s", Arrays.toString(args));
      }

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
      return "'";
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
      List<CronoType> body = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
      LambdaFunction lambda = new LambdaFunction(body);
      return Interpreter.run(lambda, env);
    }

    public String toString() {
      return "LET";
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

    public boolean evalArgs() {
      return false;
    }
  }),
  ADD(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      long sum = 0;
      for (CronoType ct : args){
        if (!(ct instanceof CronoNumber)){
          err("%s is not a number", ct);
        }else {
          sum += ((CronoNumber)ct).num;
        }
      }
      return new CronoNumber(sum);
    }

    public String toString() {
      return "+";
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
        if (!(args[0] instanceof CronoNumber)) {
          err("%s is not a number", args[0]);
        }
        return CronoNumber.valueOf(-((CronoNumber)args[0]).num);
      }
      long difference = ((CronoNumber)args[0]).num;
      for (CronoType ct : Arrays.copyOfRange(args, 1, args.length)){
        if (!(ct instanceof CronoNumber)){
          err("%s is not a number", ct);
        }else {
          difference -= ((CronoNumber)ct).num;
        }
      }
      return new CronoNumber(difference);
    }

    public String toString() {
      return "-";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  MULT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      long product = 1;
      for (CronoType ct : args){
        if (!(ct instanceof CronoNumber)){
          err("%s is not a number", ct);
        }else {
          product *= ((CronoNumber)ct).num;
        }
      }
      return new CronoNumber(product);
    }

    public String toString() {
      return "*";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  DIV(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length < 2) {
        err("too few arguments to /: %s", Arrays.toString(args));
      } else if (args.length > 2) {
        err("too many arguments to /: %s", Arrays.toString(args));
      }
      if (!(args[0] instanceof CronoNumber)) {
        err("%s is not a number", args[0]);
      }
      if (!(args[1] instanceof CronoNumber)) {
        err("%s is not a number", args[1]);
      }
      CronoNumber n = (CronoNumber)args[0];
      CronoNumber d = (CronoNumber)args[1];

      return CronoNumber.valueOf(n.num / d.num);
    }

    public String toString() {
      return "/";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  EQ(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length == 0) {
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

    public String toString() {
      return "=";
    }

    public boolean evalArgs() {
      return true;
    }
  }),
  LT(new CronoFunction() {
    public CronoType run(CronoType[] args, Environment environment) {
      if (args.length == 0) {
        err("too few arguments to <: %s", Arrays.toString(args));
      }

      boolean lt = true;
      if (!(args[0] instanceof CronoNumber)) {
        err("%s is not a number", args[0]);
      }
      CronoNumber prev = (CronoNumber)args[0];
      for(int i = 1; lt && i < args.length; i++) {
        if (!(args[i] instanceof CronoNumber)) {
          err("%s is not a number", args[i]);
        }
        CronoNumber n = (CronoNumber)args[i];
        lt = prev.num < n.num;
        prev = n;
      }

      if (lt) {
        return Symbol.valueOf("T");
      } else {
        return NIL;
      }
    }

    public String toString() {
      return "<";
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

    public boolean evalArgs() {
      return true;
    }
  }),
  ;

  public final CronoFunction function;

  private CronoFunctions(CronoFunction function) {
    this.function = function;
  }
}
