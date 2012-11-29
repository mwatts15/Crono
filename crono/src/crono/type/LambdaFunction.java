package crono.type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import crono.Environment;
import crono.Visitor;

public class LambdaFunction extends Function {
    public final Symbol[] arglist;
    public final CronoType body[]; /*< AST head node */
    public final Environment environment; /*< for scoping */
    
    public LambdaFunction(Symbol[] args, CronoType body[], Environment env) {
        super(new TypeId[args.length], CronoType.TYPEID, args.length);
        
        for(int i = 0; i < args.length; ++i) {
            this.args[i] = CronoType.TYPEID;
        }
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
        CronoType ret = null;
        for(int i = 0; i < body.length; ++i) {
            ret = body[i].accept(v);
        }
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
        
        String bodystr =
            (body.length > 1) ? Arrays.toString(body) : body[0].toString();
        return String.format("(\\ %s %s)", builder.toString(), bodystr);
    }
    /*
    public final Symbol[] arglist;
    public final CronoType body[];
    public final Environment environment;
     */
    public boolean equals(Object o) {
        if(!(o instanceof LambdaFunction)) {
            return false;
        }
        LambdaFunction fun = (LambdaFunction)o;
        if(fun.arglist.length != arglist.length ||
           fun.body.length != body.length) {
            return false;
        }
        
        for(int i = 0; i < arglist.length; ++i) {
            if(!(arglist[i].equals(fun.arglist[i]))) {
                return false;
            }
        }
        for(int i = 0; i < body.length; ++i) {
            if(!(body[i].equals(fun.body[i]))) {
                return false;
            }
        }
        return true;
    }
}
