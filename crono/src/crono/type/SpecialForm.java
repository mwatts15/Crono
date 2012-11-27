package crono.type;

import java.util.List;
import java.util.ListIterator;
import crono.Visitor;
import crono.TypeException;
import crono.CronoFunction;
import crono.type.*;

/*
 * This captures all of the special forms.
 * It differs from function application mainly in that
 * `reduce' doesn't send the visitor to each of the args
 * whereas the `apply' does
 */
public class SpecialForm extends CronoType
{
    public static final TypeId TYPEID = new TypeId(":special_form", SpecialForm.class);

    public List<CronoType> args;
    public CronoType function;
    /* However, we use the visitor pattern to perform this
     * action, so none of that logic is stored in here.
     * See Interpreter2.java
     */
    public SpecialForm (Cons c)
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
        CronoType result = null;
        System.out.printf("function = %s\n", o.toString());
        if (o instanceof Special)
        {
            Special s = (Special) o;

            /* TODO: maybe do some sanity checking on sub-forms?
             */
            /* Unlike in a FunctionApplication, none of the arguments
             * passed to the function
             */

            result = s.run(v, args);

        }
        else
        {
            throw new TypeException(this, Special.TYPEID, o);
        }
        return result;
    }
    public String toString ()
    {
        return "(reduce " + function.toString() + " " + args.toString() + ")";
    }
    public TypeId typeId() {
        return SpecialForm.TYPEID;
    }
}
