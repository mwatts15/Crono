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
import crono.type.FunctionApplication;
import crono.type.LambdaFunction;
import crono.type.Nil;
import crono.type.Quote;
import crono.type.Symbol;
import crono.type.CronoTypeId;
import crono.type.TypeId;

public class Interpreter2 extends Visitor
{
    public Interpreter2()
    {
        env_stack = new Stack<Environment>();
        reset();
    }

    public CronoType visit (CronoType o)
    {
        if (o instanceof Cons)
        {
            Cons c = (Cons) o;
            FunctionApplication fa = new FunctionApplication(c);
            return fa.apply(this);
        }
        else if (o instanceof Quote)
        {
            Quote q = (Quote) o;
            return q.node;
        }
        else if (o instanceof Symbol)
        {
            Symbol s = (Symbol) o;
            return env_stack.peek().get(s);
        }
        else
        {
            return o;
        }
    }

    public void reset()
    {
        env_stack.clear();
        pushEnv(new Environment());
    }

}
