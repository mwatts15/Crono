package crono.type;

import java.util.LinkedList;
import java.util.List;

import crono.Environment;
import crono.Visitor;

public class LambdaFunction extends Function {
    public final Symbol[] arglist;
    public final CronoType body; /*< AST head node */
    public final Environment environment; /*< for scoping */
    
    public LambdaFunction(Symbol[] args, CronoType body, Environment env) {
	this.arglist = args;
	this.body = body;
	this.environment = new Environment(env);
    }
    
    public LambdaFunction(LambdaFunction fun) {
	this(fun.arglist, fun.body, fun.environment);
    }
    
    public int arity() {
	return arglist.length;
    }
    
    public boolean variadic() {
	return false;
    }
    
    public EvalType eval() {
	return Function.EvalType.FULL;
    }
    
    public CronoType run(Visitor v, CronoType[] args) {
	Environment env = new Environment(environment);
	for(int i = 0; i < args.length; ++i) {
	    env.put(arglist[i], args[i]);
	}
	
	v.pushEnv(env);
	CronoType ret = body.accept(v);
	v.popEnv();
	
	return ret;
    }
    
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("(");
	if(arglist.length > 0) {
	    for(int i = 0; i < arglist.length - 1; ++i) {
		builder.append(arglist[i]);
		builder.append(" ");
	    }
	    builder.append(arglist[arglist.length - 1]);
	}
	builder.append(")");
	
	return String.format("(\\ %s %s)", builder.toString(), body);
    }
}
