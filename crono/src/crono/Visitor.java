package crono;

import java.util.Stack;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;
import crono.type.Quote;

public abstract class Visitor {
    protected Stack<Environment> env_stack;

    public abstract CronoType visit(Atom atom);
    public abstract CronoType visit(Cons list);
    public abstract CronoType visit(Quote quote);
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
