package crono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crono.AbstractSyntax.Atom;
import crono.AbstractSyntax.CronoFunction;
import crono.AbstractSyntax.CronoType;
import crono.AbstractSyntax.Function;
import crono.CronoOptions;

import static crono.Nil.NIL;
import static crono.CronoOptions.err;

public class Interpreter {
  private static final Environment globals = getDefaultEnvironment();

  /**
   * Sets up default environment with fundamental functions in scope.
   */
  public static Environment getDefaultEnvironment() {
    Environment result = new Environment();

    for(CronoFunctions cfs : CronoFunctions.values()) {
      result.put(Symbol.valueOf(cfs.function.toString()), cfs.function);
    }

    return result;
  }

  public static CronoType run(LambdaFunction function) {
    return run(function, function.environment.update(globals));
  }

  public static CronoType run(LambdaFunction function,
      Environment environment) {
    CronoType result = NIL;

    CronoOptions.dprint("[[\n%s\n]]\n", function.toString());
    for(CronoType statement : function.statements) {
      result = eval(statement, environment);
    }

    return result;
  }

  public static CronoType eval(CronoType statement, Environment environment) {

      CronoType result = NIL;
      boolean curry = false;

      /*
       */
      // ATOM
      if (statement instanceof Atom) {
/*
 *          if (CronoOptions.DPRINT_SHOW_ATOM_EVAL) {
 *              CronoOptions.dprint("Evaluating: %s\n", statement);
 *              CronoOptions.DPRINT_I++;
 *
 *              if (CronoOptions.ENVIRONMENT_SHOW) {
 *                  if (environment.toString().length() > 0) {
 *                      CronoOptions.dprint("Environment:\n%s\n", environment);
 *                  } else {
 *                      CronoOptions.dprint("Environment: empty\n");
 *                  }
 *              }
 *          }
 */
          if (statement instanceof Symbol && environment.containsKey((Symbol) statement)) {
              result = environment.get((Symbol)statement);
          } else {
              result = statement;
          }
          /*
           *if (CronoOptions.DPRINT_SHOW_ATOM_EVAL) {
           *    CronoOptions.DPRINT_I--;
           *    CronoOptions.dprint("Result: %s\n", result);
           *}
           */
      }
      else if (statement instanceof Cons)
      {
/*
 *          CronoOptions.dprint("Evaluating: %s\n", statement);
 *          CronoOptions.DPRINT_I++;
 *
 *          if (CronoOptions.ENVIRONMENT_SHOW) {
 *              if (environment.toString().length() > 0) {
 *                  CronoOptions.dprint("Environment:\n%s\n", environment);
 *              } else {
 *                  CronoOptions.dprint("Environment: empty\n");
 *              }
 *          }
 *
 */
          Cons cons = (Cons)statement;
          if (cons != NIL) {
              // Lookup function.
              CronoType f = eval(cons.car(), environment);
              Function function = null;
              if (f instanceof Symbol) {
                  Symbol funcKey = (Symbol)f;
                  CronoType val = environment.get(funcKey);
                  if (val instanceof Function) {
                      function = (Function)val;
                  } else {
                      err("%s is not a function", val);
                  }
              } else if (f instanceof Function) {
                  function = (Function)f;
              } else {
                  err("%s is not a function name.", f);
              }

              // Evaluate members.
              CronoType cdr = cons.cdr();
              List<CronoType> argList = new ArrayList<CronoType>();
              if (cdr instanceof Cons) {
                  Cons args = (Cons)cdr;
                  for(CronoType arg : args) {
                      // Perform argument evaluation.
                      if (function.evalArgs()) {
                          arg = eval(arg, environment);
                          // Perform substitution.
                          if (arg instanceof Symbol &&
                                  environment.containsKey((Symbol)arg))
                          {
                              arg = environment.get((Symbol)arg);
                          }
                      }
                      argList.add(arg);
                  }
              } else {
                  if (function.evalArgs()) {
                      cdr = eval(cdr, environment);
                  }
                  argList.add(cdr);
              }

              /*
               * Call function with arguments.
               */
              if (function instanceof LambdaFunction) {
                  LambdaFunction lf = new LambdaFunction((LambdaFunction)function);
                  Environment env;
                  if (CronoOptions.ENVIRONMENT_DYNAMIC) {
                      // Copy current environment.
                      env = new Environment(environment);
                  } else {
                      // Copy lambda's environment.
                      env = new Environment(lf.environment);
                  }

                  // Add argument mapping.
                  Iterator<Symbol> keyit = lf.args.iterator();
                  Iterator<CronoType> valit = argList.iterator();

                  while(keyit.hasNext() && valit.hasNext()) {
                      env.put(keyit.next(), valit.next());
                      keyit.remove();
                  }
                  if (keyit.hasNext()) {
                      /* Leave partially applied */
                      //err("too few arguments given to function: %s", function);
                      curry = true;
                  }
                  if (valit.hasNext()) {
                      err("too many arguments given to function: %s", function);
                  }
                  /* add the applied arguments */
                  lf.environment.update(env);
                  if (curry)
                  {
                      /*
                       * return the function with arguments stuffed
                       * into its environment
                       */
                      result = lf;
                  }
                  else
                  {
                      result = run(lf);//, env);
                  }
              } else if (function instanceof CronoFunction) {
                  /* TODO these probabli need to be curried too :P */
                  CronoFunction cf = (CronoFunction)function;
                  result = cf.run(argList.toArray(new CronoType[] {}), environment);
              }
          }
          CronoOptions.DPRINT_I--;
          CronoOptions.dprint("Result: %s\n", result);
      } else {
          err("Encountered statement of unknown type: %s\n\t%s\n",
                  statement.getClass().getName(), statement);
      }

      return result;
  }
}
