package crono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crono.AbstractSyntax.CronoType;
import crono.AbstractSyntax.CronoFunction;
import crono.AbstractSyntax.Function;
import crono.CronoOptions;
import crono.Environment;
import crono.Symbol;

/**
 * A dynamically created function.
 *
 * Contains a list of potentionally unexpanded Crono objects which are arguments
 * and a list of statements to be executed.
 */
public class LambdaFunction implements Function {
  public final List<Symbol> args;
  /* Crono functions allow for a list of statements
   * without a begin block */
  public final List<CronoType> statements;
  public final Environment environment;

  public LambdaFunction(List<CronoType> body){
    this(body, new ArrayList<Symbol>());
  }

  public LambdaFunction(List<CronoType> body, List<Symbol> args) {
    this(body, args, new Environment());
  }

  public LambdaFunction(List<CronoType> body, Environment environment) {
    this(body, new ArrayList<Symbol>(), environment);
  }

  public LambdaFunction(List<CronoType> body, List<Symbol> args, Environment environment) {
    this.statements = body;
    this.args = args;
    this.environment = environment;
  }
  /* clones the passed in function */
  public LambdaFunction(LambdaFunction f)
  {
      /* statements shouldn't change, so doesn't need to be copied */
      /* copy the args list */
      List<Symbol> new_args = new ArrayList<Symbol>();
      for (Symbol e: f.args)
      {
          new_args.add(e);
      }
      this.statements = f.statements;
      this.args = new_args;
      this.environment = new Environment(f.environment);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("(closure (");
    /* un-bound arguments */
    Iterator<Symbol> argit = args.iterator();
    while(argit.hasNext()) {
      sb.append(argit.next().toString());
      if (argit.hasNext()) {
        sb.append(" ");
      }
    }
    sb.append(") ");
    Iterator<CronoType> sit = statements.iterator();
    while(sit.hasNext()) {
      sb.append(sit.next().toString());
      if (sit.hasNext()) {
        sb.append(" ");
      }
    }
    sb.append(")");

    /* bound arguments and inherited environment
     * should be SHOW_EVIRONMENT, but whatevers*/
    if (CronoOptions.LAMBDA_SHOW_CLOSURE) {
      sb.append(" [");
      /* The environment */
      sb.append(closure());
      //environment.toString();
      sb.append("]");
    }

    return sb.toString();
  }

  public boolean evalArgs() {
    return true;
  }

  /**
   * Essentially copies Environment.toString() but saves us from infinite
   * recursion if this LambdaFunction is in its own environment.
   */
  // TODO: move this toString technique to Environment.toString()
  private String closure() {
    StringBuilder result = new StringBuilder();

    Iterator<Symbol> it = environment.iterator();
    List<Symbol> keys = new ArrayList<Symbol>();
    List<CronoType> vals = new ArrayList<CronoType>();
    while(it.hasNext()) {
      Symbol key = it.next();
      CronoType val = environment.get(key);
      if (!(val instanceof CronoFunction) ||
          CronoOptions.ENVIRONMENT_SHOW_BUILTIN) {
        keys.add(key);
        vals.add(val);
      }
    }

    Iterator<Symbol> keyit = keys.iterator();
    Iterator<CronoType> valit = vals.iterator();

    while(keyit.hasNext()) {
      Symbol key = keyit.next();
      CronoType val = valit.next();
      result.append(key.toString());
      result.append(": ");

      if (val instanceof LambdaFunction) {
        boolean prev = CronoOptions.LAMBDA_SHOW_CLOSURE;
        CronoOptions.LAMBDA_SHOW_CLOSURE = false;
        result.append(val.toString());
        CronoOptions.LAMBDA_SHOW_CLOSURE = prev;
      } else {
        result.append(val.toString());
      }
      if (CronoOptions.ENVIRONMENT_SHOW_TYPES) {
        result.append(" [");
        result.append(val.getClass().getName());
        result.append("]");
      }
      if (keyit.hasNext()) {
        result.append(", ");
      }
    }

    return result.toString();
  }
}


