package crono.type;
import java.util.List;
import crono.Visitor;
import crono.TypeException;

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
     * Applies the function to the argument list
     */
    public CronoType apply (Visitor v)
    {
        CronoType f = function.accept(v);
        if (f instanceof Function)
        {
            CronoType[] template_a = new CronoType[args.size()];
            return ((Function) f).run(v, args.toArray(template_a));
        }
        else
        {
            throw new TypeException(this, Function.TYPEID, f);
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
