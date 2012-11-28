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

    public CronoType visit (CronoType o)
    {
        System.out.printf("Visiting: %s\n", o.toString());
        CronoType result = null;
        if (o instanceof Nil)
        {
            result = o;
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
            if (head instanceof Symbol)
            {
                Symbol s = (Symbol) head;
                /* If the head position is occupied by a
                 * "special" then we pass the whole cons to
                 * a SpecialApplication.
                 *
                 * We don't evaluate the symbol, just look it
                 * up, since we don't want to visit the head
                 * twice
                 */
                if (s.isCombinator())
                {
                    CombinatorApplication ca = new CombinatorApplication(c);
                    result = ca.reduce(this);
                }
                else
                {
                    CronoType t = getEnv().get(s);
                    if (t instanceof Special)
                    {
                        SpecialForm sf = new SpecialForm(c);
                        /* If we wanted only to create an AST,
                         * we would stop here.
                         * However, we create the nodes and
                         * evaluate on the fly
                         */
                        result = sf.reduce(this);
                    }
                }
            }

            if (result == null)
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
        else if (o instanceof Quote)
        {
            Quote q = (Quote) o;
            result = q.node;
        }
        else if (o instanceof Symbol)
        {
            Symbol s = (Symbol) o;
            result = getEnv().get(s);
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
