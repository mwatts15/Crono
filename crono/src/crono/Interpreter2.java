package crono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import crono.type.*;

public class Interpreter2 extends Visitor
{
    public Interpreter2()
    {
        env_stack = new Stack<Environment>();
        reset();
    }
    private CronoType coerceCombinator (CronoType val)
    {
        if (val == null)
        {
            throw new InterpreterException("attempting coerce on null");
        }
        System.out.println("coercing " + val + " to combinator");
        CronoType result = null;
        if (val instanceof Symbol && ((Symbol) val).isCombinator())
        {
        /* If the value is a combinator symbol,
         * return true
         */
            result = val;
        }
        else if (val instanceof Cons)
        {
        /* If it's a cons and the head is a combinator
         * reduce it and keep doing so until we
         * reach a fixed point or the head isn't a combinator.
         */
            Cons c = (Cons) val;
            CronoType head = c.car();
            if (head instanceof Symbol)
            {
                Symbol maybe_comb = (Symbol) head;
                if (maybe_comb.isCombinator())
                {
                    System.out.println("Our head is a combinator symbol...");
                    CombinatorApplication ca = new CombinatorApplication(c);
                    CronoType r = ca.reduce(this);
                    System.out.println("c = " + c + " r = " + r );
                    /* Reduces to itself; fixed point */
                    if (r.equals(c))
                    {
                        result = r;
                    }
                    else if (r == null)
                    {
                        System.out.println("HERE at r == null");
                    }
                    else
                    {
                        /* If the result is still a combinator then we continue
                         * otherwise set result to null
                         */
                        result = coerceCombinator(r);
                    }
                }
            }
        }
        System.out.println("Result of coercion: " + result);
        return result;
    }

    public CronoType visit (CronoType o)
    {
        System.out.printf("Visiting: %s\n", o.toString());
        CronoType result = null;
        if (o instanceof Nil)
        {
            result = o;
        }
        else if (o instanceof Symbol)
        {
            Symbol s = (Symbol) o;
            result = getEnv().get(s);
        }
        else if (o instanceof Cons)
        {
            /* Here we do two steps at once, both a translation
             * from Conses to abstract syntax and evaluation. The
             * point of this is really to reduce code duplication
             * for when we do have an AST based interpreter
             *
             * You can see that some AST nodes like SpecialForm
             * and FunctionApplication have accept methods of
             * their own: these aren't for this interpreter.
             * If we do see those, they aren't intepreted at all
             */
            Cons c = (Cons) o;
            CronoType head = c.car();
            CronoType maybe_comb;
            if ((maybe_comb = coerceCombinator(head)) != null)
            {
                c = new Cons(maybe_comb, c.cdr());
                CombinatorApplication ca = new CombinatorApplication(c);
                result = ca.reduce(this);
            }
            else
            {
                head = head.accept(this);
                if (head instanceof Special)
                {
                    SpecialForm sf = new SpecialForm(c);
                    /* If we wanted only to create an AST,
                     * we would stop here.
                     * However, we create the nodes and
                     * evaluate on the fly
                     */
                    result = sf.reduce(this);
                }
                else if (head instanceof Function)
                {
                    /*
                     * We assume function application by default
                     * Note that this assumption is relied on architecturally:
                     * Specials *are* Functions, Specials must always be checked
                     * before this case if they are to be matched...specifically
                     */
                    FunctionApplication fa = new FunctionApplication(c);
                    /* If we wanted only to create an AST,
                     * we would stop here.
                     */
                    result = fa.apply(this);
                }
            }
        }
        else if (o instanceof Quote)
        {
            Quote q = (Quote) o;
            result = q.node;
        }
        else
        {
            result = o;
        }

        System.out.printf("Leaving with: %s\n", result.toString());
        return result;
    }

    public void reset()
    {
        env_stack.clear();
        pushEnv(new Environment());
    }

}
