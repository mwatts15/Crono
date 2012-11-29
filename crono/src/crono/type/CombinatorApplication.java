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
public class CombinatorApplication
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
        System.out.println("Reducing combinatory expression " + c);
        function = c.car();
        // Unchecked cdr assumed to be cons
        args = ((Cons) c.cdr()).toList();
    }

    //public CronoType accept (Visitor v)
    //{
        //return v.visit((Atom)this);
    //}

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
        System.out.printf("Reducing combinatory expression...\n");
        CronoType o = function.accept(v);
        CronoType result = null;
        if (o instanceof Function)
        {
            System.out.println("Simple combinator...");
            LambdaFunction f = (LambdaFunction) o;

            /* The arguments are passed to the function
             * If the function has enough arguments,
             * then we just evaluates the function. Otherwise
             * the function just retains the values for its
             * arguments-- the body of the function is NOT
             * evaluated in any capacity unless it has enough
             * arguments.
             */

            System.out.println(args);
            if (args.size() == 0)
            {
                result = function;
            }
            else if (f.arity == args.size())
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
            else if (f.arity < args.size())
            {
                /* we take as many as we need and leave the rest alone */
                Cons usable_args = Cons.fromList(args.subList(0, f.arity));
                Cons rest_of_args = Cons.fromList(args.subList(f.arity, args.size()));
                /* We can use either the combinator or function application
                /* We can use either the combinator or function application
                 * path for this
                 */
                CronoType reduced = (new Cons(function, usable_args)).accept(v);
                Cons new_expr = Nil.NIL;
                if (reduced instanceof Cons)
                {
                    new_expr = ((Cons) reduced).append(rest_of_args);
                }
                else
                {
                    new_expr = new Cons(reduced, rest_of_args);
                }
                result = new_expr.accept(v);
            }
            else
            {
                System.out.println("Not enough arguments to combinator...");
                result = new Cons(function, Cons.fromList(args));
            }

        }
        else if (o instanceof Cons)
        {
            /* We have to append this to args */
            System.out.println("Reducing parethesized combinator...");
            Cons reduced = ((Cons) o).append(Cons.fromList(args));
            result = reduced.accept(v);
        }
        else
        {
            System.out.println(v.getEnv());
            throw new InterpreterException("not a combinator in reduction of %s", function);
        }
        System.out.println("Reduced expression: " + result);
        return result;
    }
    public String toString ()
    {
        return "(comb_apply " + function.toString() + " " + args.toString() + ")";
    }
    public TypeId typeId() {
        return CombinatorApplication.TYPEID;
    }
}

