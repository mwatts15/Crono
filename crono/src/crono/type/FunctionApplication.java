package crono.type;

import java.util.List;
import java.util.ListIterator;
import crono.Visitor;
import crono.TypeException;
import crono.CronoFunction;
import crono.type.*;

/*
 * This captures the process of function application.
 * It is a function or identifier together with some
 * arguments to the function
 *
 * Should this be CronoType? I made it so because it needs to be printed
 * on the AST, but maybe there should be a Node class or something that
 * deals with that
 */
public class FunctionApplication extends CronoType
{
    public static final TypeId TYPEID = new TypeId(":function_application", FunctionApplication.class);

    public List<CronoType> args;
    public CronoType function;
    /* However, we use the visitor pattern to perform this
     * action, so none of that logic is stored in here.
     * See Interpreter2.java
     */
    public FunctionApplication (Cons c)
    {
        function = c.car();
        // Unchecked cdr assumed to be cons
        args = ((Cons) c.cdr()).toList();
    }

    public CronoType accept (Visitor v)
    {
        return v.visit(this);
    }

    /*
     * Applies this.function to the this.args
     */
    public CronoType apply (Visitor v)
    {
        /* Evaluate the function first for a
         * function value.
         * This may be extended to a "callable"
         * later
         */
        CronoType o = function.accept(v);
        System.out.printf("function = %s\n", o.toString());
        if (o instanceof Function)
        {
            Function f = (Function) o;

            /* The arguments are passed to the function
             * If the function has enough arguments,
             * then we just evaluates the function. Otherwise
             * the function just retains the values for its
             * arguments-- the body of the function is NOT
             * evaluated in any capacity unless it has enough
             * arguments.
             */
            //if (f.arity != args.size())
            //{
                //if (f instanceof CronoFunction)
                //{
                    /* Wrap all CronoFunctions in
                     * LambdaFunction if they don't have the right number of args
                     * so as not to duplicate partial application logic
                     */
                    //CronoFunction cf = (CronoFunction) f;
                    //Cons body = Nil.NIL;
                    //Symbol[] new_args = new Symbol[cf.arity];
                    //for (int i = cf.arity - 1; i >= 0; i--)
                    //{
                        //Symbol sym = new Symbol(String.format("arg%d", i));
                        //body.cons(sym);
                        //new_args[i] = sym;
                    //}

                    //f = new LambdaFunction(new_args, (CronoType) body, v.getEnv());
                //}
            //}
            /* All of the arguments must be evaluated before getting
             * passed to the function
             */
            ListIterator<CronoType> it = args.listIterator();
            while (it.hasNext())
            {
                CronoType a = it.next();
                it.set(a.accept(v));
            }
            CronoType result = f.run(v, args);

            /* If we get a function back, that could mean
             * either that the function was curried or that
             * it's just a function that returns a function
             *
             * This case is purely for documentation.
             */
            if (result instanceof Function)
            {
                return result;
            }
            /* If the result of the application is another
             * function application, then we passed more arguments
             * than the function takes.
             */
            else if (result instanceof FunctionApplication)
            {
                FunctionApplication fa = (FunctionApplication) result;
                /* We will try evaluating it again.
                 * This follows with the idea that in the lambda calculus
                 * (f a b c) is equiv to (((f a) b) c)
                 *
                 * If the first application (f a) returns a function, then
                 * we can continue evaluating. Whatever result we get is
                 * equally the result of the entire expression so it gets
                 * returned.
                 *
                 * Why does this trace back through visitor?
                 * i.e. fa.accept rather than fa.apply?
                 * leaving it accept for now
                 */
                 return fa.accept(v);
            }
            else
            {
                return result;
            }
        }
        else
        {
            throw new TypeException(this, Function.TYPEID, o);
        }
    }
    public String toString ()
    {
        return "(apply " + function.toString() + " " + args.toString() + ")";
    }
    public TypeId typeId() {
        return FunctionApplication.TYPEID;
    }
}
