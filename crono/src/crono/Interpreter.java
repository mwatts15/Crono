package crono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;
import crono.type.Function;
import crono.type.Function.EvalType;
import crono.type.LambdaFunction;
import crono.type.Nil;
import crono.type.Quote;
import crono.type.Symbol;
import crono.type.CronoTypeId;
import crono.type.TypeId;

public class Interpreter extends Visitor {
    public class InterpreterState extends Visitor.VisitorState {
        public boolean showEnv, rShowEnv;
        public boolean showClosure, rShowClosure;
        public boolean dynamic, rDynamic;
        public boolean trace, rTrace;
        public boolean printAST, rPrintAST;
        public Stack<Environment> envStack;
        
        public InterpreterState() {
            showEnv = Interpreter.this.showEnv;
            rShowEnv = Interpreter.this.rShowEnv;
            showClosure = Interpreter.this.showClosure;
            rShowClosure = Interpreter.this.rShowClosure;
            dynamic = Interpreter.this.dynamic;
            rDynamic = Interpreter.this.rDynamic;
            trace = Interpreter.this.trace;
            rTrace = Interpreter.this.rTrace;
            printAST = Interpreter.this.printAST;
            rPrintAST = Interpreter.this.rPrintAST;
            
            envStack = new Stack<Environment>();
            envStack.addAll(Interpreter.this.envStack);
            int size = envStack.size();
            for(int i = 0; i < size; ++i) {
                envStack.set(i, new Environment(envStack.get(i)));
            }
        }
    }
    
    private static final String _scope_err = "No object %s in scope";
    private static final String _too_many_args =
        "Too many arguments to %s: %d/%d recieved";
    private static final String _type_scope_err = "No type %s in scope";
    private static final String _type_mismatch =
        "Function '%s' expected arguments %s; got %s";
    private static final String _indent_level = "  ";
    
    protected boolean showEnv, rShowEnv;
    protected boolean showClosure, rShowClosure;
    protected boolean dynamic, rDynamic;
    protected boolean trace, rTrace;
    protected boolean printAST, rPrintAST;
    protected EvalType eval;
    
    protected StringBuilder indent;
    protected Stack<Environment> envStack;
    
    /**
     * Creates a new Interpreter with default option values.
     */
    public Interpreter() {
        showEnv(false);
        showClosure(false);
        dynamic(false);
        trace(false);
        printAST(false);
        
        indent = new StringBuilder();
        eval = Function.EvalType.FULL;
        
        envStack = new Stack<Environment>();
        reset(); /*< Set up initial environment and types */
    }
    
    /**
     * Turns environment reporting on or off.
     * @param on If the Interpreter should report the contents of the
     *           Environment.
     */
    public void showEnv(boolean on) {
        showEnv = on;
        rShowEnv = on;
    }
    /**
     * Turns printing of closures on or off.
     * Not currently implemented.
     * @param on If closures should be shown.
     */
    public void showClosure(boolean on) {
        showClosure = on;
        rShowClosure = on;
    }
    /**
     * Turns dynamic scoping on or off.
     * TODO: add a description of dynamic scoping.
     */
    public void dynamic(boolean on) {
        dynamic = on;
        rDynamic = on;
    }
    /**
     * Turns operation tracing on or off.
     * @param on If the interpreter should perform operation tracing.
     */
    public void trace(boolean on) {
        trace = on;
        rTrace = on;
    }
    /**
     * Turn AST printing on or off.
     * @param on If the Interpreter should print the AST node it is at.
     */
    public void printAST(boolean on) {
        printAST = on;
        rPrintAST = on;
    }
    
    /**
     * Helper method to indent correctly.
     */
    protected void indent() {
        indent.append(_indent_level);
    }
    /**
     * Helper method to de-indent correctly.
     */
    protected void deindent() {
        int size = indent.length();
        if(size < 2) {
            indent = new StringBuilder();
        }else {
            indent.deleteCharAt(size - 1);
            indent.deleteCharAt(size - 2);
        }
    }
    
    /**
     * Resets interpreter state to it's default state.
     */
    protected void resetOptions() {
        /* Restore defaults that may have been changed */
        showEnv = rShowEnv;
        showClosure = rShowClosure;
        dynamic = rDynamic;
        trace = rTrace;
        printAST = rPrintAST;
    }
    /**
     * Turns off all options that only affect information that is printed.
     */
    protected void optionsOff() {
        showEnv = false;
        showClosure = false;
        trace = false;
        printAST = false;
    }
    
    /**
     * Prints the trace notification of a node visit.
     * This function only prints a trace if the Interpreter.trace(boolean)
     * method was called with a value of true.
     * @param node The node that is being visited.
     */
    protected void traceVisit(CronoType node) {
        if(trace) {
            System.out.printf("%sVisiting %s\n", indent, node.repr());
        }
    }
    /**
     * Prints the trace results of evaluating a node.
     * This function only prints a trace if the Interpreter.trace(boolean)
     * method was called with a value of true.
     * @param result The results to report
     */
    protected void traceResult(CronoType result) {
        if(trace) {
            System.out.printf("%sResult: %s [%s]\n", indent, result.repr(),
                              result.typeId());
        }
    }
    /**
     * Prints out a message that a node was visited on the AST.
     * This function only prints a trace if the Interpreter.printAST(boolean)
     * method was called with a value of true.
     * @param nodeName The name of the node type that is being visited.
     */
    protected void printASTNode(String nodeName) {
        if(printAST) {
            System.out.printf("%sAST: %s\n", indent, nodeName);
        }
    }
    protected void printEnvironment() {
        if(showEnv) {
            System.out.printf("%sEnv: %s\n", indent, getEnv());
        }
    }
    
    /**
     * On exceptions, instead of throwing and forgetting, it should call except
     * to reset the evaluation level, reset all options and then throw. This
     * ensures that the interpreter doesn't get into an invalid state.
     */
    protected void except(RuntimeException e) {
        resetOptions();
        eval = EvalType.FULL;
        indent = new StringBuilder();
        throw e;
    }
    
    /**
     * Resets the Interpreter to it's default state, including the environment.
     */
    public void reset() {
        envStack.clear();
        pushEnv(new Environment());
        resetOptions();
    }
    
    /**
     * Visit a cons node.
     * There are a few cases the interpreter has to deal with:
     *   1. The evaluation type is set to NONE:
     *      * The node is a 'list', and is processed by the sub-node in some
     *      * interpreter unknown way.
     *   2. The node is Nil:
     *      * Return Nil
     *   3. The node is a function application:
     *      a. The evaluation type is PARTIAL:
     *         * Perform name substitution by visiting all sub nodes.
     *      b. The evaluation type is FULL:
     *         * Number of arguments statisfys the function requirments:
     *            * Run the function
     *         * Number of arguments is less than required:
     *            * Curry the function
     *         * Too many arguments:
     *            * Throw an exception
     * The Nil case could be seperated out by giving Nil.java an accept method,
     * and adding a visit method to Visitor.java and Interpreter.java.
     * @param c The Cons node to visit.
     * @return The value obtained by visiting this node.
     */
    public CronoType visit(Cons c) {
        if(eval == EvalType.NONE) {
            printASTNode("List Node");
            traceVisit(c);
            traceResult(c);
            printEnvironment();
            return c;
        }
        
        Iterator<CronoType> iter = c.iterator();
        if(!(iter.hasNext())) {
            printASTNode("Nil Node");
            traceVisit(Nil.NIL);
            traceResult(Nil.NIL);
            printEnvironment();
            return c; /*< C is an empty list (may be Nil or T) */
        }
        
        printASTNode("Function Application Node");
        traceVisit(c);
        indent();
        CronoType value = iter.next().accept(this);
        if(value instanceof Function) {
            Function fun = ((Function)value);
            
            EvalType reserve = eval;
            
            eval = fun.eval;
            if(eval.level > reserve.level) {
                eval = reserve;
            }
            List<CronoType> args = new ArrayList<CronoType>();
            while(iter.hasNext()) {
                args.add(iter.next().accept(this));
            }
            eval = reserve;
            
            int arglen = args.size();
            int nargs = fun.arity;
            if(arglen < nargs) {
                if(arglen == 0) {
                    /* Special case -- we don't have to do anything to the
                     * function to return it properly. */
                    deindent();
                    traceResult(fun);
                    printEnvironment();
                    return fun;
                }
                
                /* Curry it */
                if(fun instanceof LambdaFunction) {
                    LambdaFunction lfun = ((LambdaFunction)fun);
                    Environment env = getEnv();
                    if(!dynamic) {
                        /* Use the lambda's stored environment */
                        env = lfun.environment;
                    }
                    /* We want to preserve the current environment */
                    env = new Environment(false);
                    
                    /* Put known values into the new environment */
                    for(int i = 0; i < arglen; ++i) {
                        env.put(lfun.arglist[i], args.get(i));
                    }
                    /* Create new argument list and remove remaining args from
                     * the new environment */
                    List<Symbol> largs = new ArrayList<Symbol>();
                    for(int i = arglen; i < lfun.arglist.length; ++i) {
                        largs.add(lfun.arglist[i]);
                        env.remove(lfun.arglist[i]);
                    }
                    
                    /* Evaluate the body as much as possible */
                    reserve = eval;
                    
                    CronoType[] lbody = new CronoType[lfun.body.length];
                    optionsOff(); /*< Extra indent for clarity */
                    {
                        eval = EvalType.PARTIAL;
                        pushEnv(env);
                        for(int i = 0; i < lfun.body.length; ++i) {
                            lbody[i] = lfun.body[i].accept(this);
                        }
                        eval = reserve;
                        popEnv();
                    }
                    resetOptions(); /*< Set options back to what they were */
                    
                    /* Return the new, partially evaluated lambda */
                    Symbol[] arglist = new Symbol[largs.size()];
                    
                    LambdaFunction clfun;
                    clfun = new LambdaFunction(largs.toArray(arglist), lbody,
                                               lfun.environment);
                    deindent();
                    traceResult(clfun);
                    printEnvironment();
                    
                    return clfun;
                }
                /* Builtin partial evaluation */
                List<CronoType> body = new LinkedList<CronoType>();
                body.add(fun);
                body.addAll(args); /*< Dump args in order into the new cons */
                
                /* Add symbols for missing args */
                List<Symbol> arglist = new ArrayList<Symbol>();
                Symbol sym;
                for(int i = arglen, n = 0; i < nargs; ++i, ++n) {
                    sym = new Symbol(String.format("_i?%d!_", n));
                    body.add(sym);
                    arglist.add(sym);
                }
                
                /* Create a new lambda */
                Symbol[] narglist = new Symbol[arglist.size()];
                LambdaFunction blfun;
                CronoType[] barr = new CronoType[] {Cons.fromList(body)};
                blfun = new LambdaFunction(arglist.toArray(narglist), barr,
                                           getEnv());
                deindent();
                traceResult(blfun);
                printEnvironment();
                
                return blfun;
            }
            if(arglen > nargs && !fun.variadic) {
                eval = reserve;
                except(new InterpreterException(_too_many_args, fun, arglen,
                                                nargs));
            }
            
            /* Full evaluation */
            if(eval == EvalType.FULL) {
                if(fun instanceof LambdaFunction && dynamic) {
                    /* We have to trick the lambda function if we want dynamic
                     * scoping. I hate making so many objects left and right,
                     * but this is the easiest way to do what I want here. */
                    LambdaFunction lfun = ((LambdaFunction)fun);
                    lfun = new LambdaFunction(lfun.arglist, lfun.body,
                                              getEnv());
                    CronoType[] argarray = new CronoType[args.size()];
                    
                    optionsOff();
                    CronoType lresult = null;
                    try {
                        lresult = lfun.run(this, args.toArray(argarray));
                    }catch(RuntimeException e) {
                        except(e);
                    }
                    resetOptions();
                    
                    deindent();
                    traceResult(lresult);
                    printEnvironment();
                    
                    return lresult;
                }
                
                CronoType[] argarray = new CronoType[args.size()];
                argarray = args.toArray(argarray);
                TypeId[] types = new TypeId[args.size()];
                for(int i = 0; i < types.length; ++i) {
                    types[i] = argarray[i].typeId();
                }
                int check = Math.min(argarray.length,fun.args.length);
                for(int i = 0; i < check; ++i) {
                    if(!(fun.args[i].isType(argarray[i]))) {
                        String argstr = Arrays.toString(types);
                        String expected = Arrays.toString(fun.args);
                        except(new InterpreterException(_type_mismatch, fun,
                                                        expected, argstr));
                    }
                }
                
                optionsOff();
                CronoType fresult = null;
                Function fun2 = (Function)value;
                try {
                    fresult = fun2.run(this, args.toArray(argarray));
                }catch(RuntimeException re) {
                    except(re);
                }
                resetOptions();
                
                deindent();
                traceResult(fresult);
                printEnvironment();
                
                return fresult;
            }else {
                args.add(0, value);
                deindent();
                Cons cresult = Cons.fromList(args);
                traceResult(cresult);
                printEnvironment();
                
                return cresult;
            }
        }else if(eval == EvalType.PARTIAL) {
            List<CronoType> args = new ArrayList<CronoType>();
            args.add(value);
            while(iter.hasNext()) {
                args.add(iter.next().accept(this));
            }
            CronoType conres = Cons.fromList(args);
            traceResult(conres);
            return conres;
        }
        deindent();
        /* The initial value is not a function */
        except(new InterpreterException("Invalid Function Application: %s is not a function in %s", value, c));
        return null;
    }
    
    /**
     * Visits an atom node.
     * The results of this method depend on the current evaluation type of the
     * Interpreter.
     *   1. NONE:
     *      * Return the atom without doing anything.
     *   2. PARTIAL of FULL:
     *      * If the atom is a symbol, do name resolution based on the current
     *        environment.
     *      * If the atom is a type, or was resolved to a type, do type
     *        resolution.
     * @param a The atom to visit
     * @return The value obtained by visiting this node.
     */
    public CronoType visit(Atom a) {
        printASTNode(String.format("Atom Node -> %s[%s]\n", a, a.typeId()));
        traceVisit(a);
        if(eval == EvalType.NONE) {
            traceResult(a);
            return a;
        }
        
        CronoType t = a;
        if(t instanceof Symbol) {
            t = getEnv().get((Symbol)a);
            if(t == null) {
                if(eval == EvalType.FULL) {
                    except(new InterpreterException(_scope_err, a.repr()));
                }
                t = a;
            }
        }
        /* Not else-if, so that we perform a double-resolution on a symbol that
         * represents a TypeId */
        if(t instanceof CronoTypeId) {
            CronoType res = t; /*< Save symbol resolution */
            t = getEnv().getType((CronoTypeId)t);
            if(t == null) {
                if(eval == EvalType.FULL) {
                    except(new InterpreterException(_type_scope_err, a));
                }
                t = res; /*< Revert to symbol resolution */
            }
        }
        traceResult(t); /* We don't need to show the environment after atoms */
        return t;
    }
    
    /**
     * Visits a quote node.
     * Quote nodes simply return the value obtained by visiting their sub-node
     * with a forced eval-type of NONE.
     * @param q The quote node to visit.
     * @return The node below this unchanged.
     */
    public CronoType visit(Quote q) {
        printASTNode("Quote Node");
        traceVisit(q);
        
        EvalType reserve = eval;
        eval = EvalType.NONE;
        CronoType result = q.node.accept(this);
        eval = reserve;
        
        traceResult(result);
        return result;
    }
    
    public Visitor.VisitorState getState() {
        return new InterpreterState();
    }
    public void setState(Visitor.VisitorState state) {
        InterpreterState is = (InterpreterState)state;
        showEnv = is.showEnv;
        rShowEnv = is.rShowEnv;
        showClosure = is.showClosure;
        rShowClosure = is.rShowClosure;
        dynamic = is.dynamic;
        rDynamic = is.rDynamic;
        trace = is.trace;
        rTrace = is.rTrace;
        printAST = is.printAST;
        rPrintAST = is.rPrintAST;
        
        envStack = new Stack<Environment>();
        envStack.addAll(is.envStack);
        int size = envStack.size();
        for(int i = 0; i < size; ++i) {
            envStack.set(i, new Environment(envStack.get(i)));
        }
    }
    
    public Environment getEnv() {
        return envStack.peek();
    }
    public void pushEnv(Environment env) {
        envStack.push(env);
    }
    public void popEnv() {
        envStack.pop();
    }
}
