package crono;

import java.util.Stack;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;
import crono.type.Quote;
import crono.type.FunctionApplication;

public abstract class Visitor {
    protected Stack<Environment> env_stack;

    //public abstract CronoType visit(Atom atom);
    //public abstract CronoType visit(Cons list);
    //public abstract CronoType visit(Quote quote);
    //public abstract CronoType visit(FunctionApplication fa);

    /* This method should just dispatch on type.
     * It's not java-style, but it works
     */
    public abstract CronoType visit(CronoType o);

    public abstract void reset();

    public Environment getEnv() {
        return env_stack.peek();
    }
    public void pushEnv(Environment env) {
        env_stack.push(env);
    }
    public void popEnv() {
        env_stack.pop();
    }
}
