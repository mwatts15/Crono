package crono;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;
import crono.type.Quote;

public abstract class Visitor {
    /**
     * Empty abstract class representing the visitor state at some point of
     * execution.
     */
    public abstract class VisitorState { }
    
    public abstract CronoType visit(Atom atom);
    public abstract CronoType visit(Cons list);
    public abstract CronoType visit(Quote quote);
    public abstract void reset();
    
    /**
     * Should return a VisitorState that can be used by nodes being visited.
     * This is to make it possible to restore state from within functions:
     * such as a (try symbol body catch) function.
     * It would also make it possible to translate interpreter state to a
     * first-class object in Crono.
     */
    public abstract VisitorState getState();
    /**
     * Restores the visitors state using the given state.
     * Since it shouldn't be possible to switch Visitors during execution, it
     * is not important to catch Exceptions from this function, but they are
     * technically possible.
     * @param state The state to restore this visitor to.
     */
    public abstract void setState(VisitorState state);
    
    public abstract Environment getEnv();
    public abstract void pushEnv(Environment env);
    public abstract void popEnv();
}
