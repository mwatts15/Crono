package crono.type;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import crono.*;
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
public class CombinatorApplication extends CronoType
{
    public static final TypeId TYPEID = new TypeId(":function_application", CombinatorApplication.class);

    public List<CronoType> args;
    public CronoType function;
    /* However, we use the visitor pattern to perform this
     * action, so none of that logic is stored in here.
     * See Interpreter2.java
     */
    public CombinatorApplication (Cons c)
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
    public CronoType reduce (Visitor v)
    {
        /* Evaluate the function first for a
         * function value.
         * This may be extended to a "callable"
         * later
         */
        CronoType o = function.accept(v);
        System.out.printf("Reducing combinatory expression...\n");
        CronoType result = null;
        if (o instanceof LambdaFunction)
        {
            LambdaFunction f = (LambdaFunction) o;

            /* The arguments are passed to the function
             * If the function has enough arguments,
             * then we just evaluates the function. Otherwise
             * the function just retains the values for its
             * arguments-- the body of the function is NOT
             * evaluated in any capacity unless it has enough
             * arguments.
             */

            if (f.arity == args.size())
            {
                CronoType substituted_body = f.body[0];
                int i = 0;
                for (CronoType a : args)
                {
                    substituted_body = Cons.subst(substituted_body, f.arglist[i], a);
                    i++;
                }

                /* We have to keep evaluating this to reduce it to a value */
                result = substituted_body.accept(v);
            }
            else
            {
                result = new Cons(function, Cons.fromList(args));
            }

        }
        else
        {
            throw new TypeException(this, Function.TYPEID, o);
        }
        return result;
    }
    public String toString ()
    {
        return "(apply " + function.toString() + " " + args.toString() + ")";
    }
    public TypeId typeId() {
        return CombinatorApplication.TYPEID;
    }
}

