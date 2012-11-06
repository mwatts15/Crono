package crono.type;

import java.util.HashMap;
import java.util.Map;

import crono.Visitor;

/**
 * A structure, similar to C structures.
 * CronoStruct inherits from Function so that they can be accessed as if their
 * name was a function, i.e.:
 *   (mystruct field value)
 * However their type inherits from the base CronoType. This is because they
 * are not 'really' a function, nor are they an atomic type.
 */
public class CronoStruct extends Function {
    public static final TypeId TYPEID = new TypeId(":struct",
						   CronoStruct.class,
						   CronoType.TYPEID);
    private static final String _invalid_field_type =
	"Invalid field id: %s [%s]";
    private static final String _invalid_field_name =
	"Invalid field name: (%s %s) does not exist";
    
    public final String name;
    private Map<Symbol, CronoType> fields;
    private TypeId type;
    
    public CronoStruct(String name, Map<Symbol, CronoType> fields) {
	this.name = name;
	this.fields = new HashMap<Symbol, CronoType>(fields);
	this.type = new TypeId(":struct-"+name, CronoStruct.class, TYPEID);
    }
    
    public int arity() {
	return 1;
    }
    public boolean variadic() {
	return true;
    }
    public EvalType eval() {
	return EvalType.FULL;
    }
    public CronoType run(Visitor v, CronoType[] args) {
	CronoType val;
	
	/* CronoStruct doesn't contain an AST, so we can ignore the visitor */
	switch(args.length) {
	case 1:
	    /* Being used as a get */
	    if(!(args[0] instanceof Symbol)) {
		String classname = args[0].getClass().getName();
		throw new RuntimeException(String.format(_invalid_field_type,
							 args[0].toString(),
							 classname));
	    }
	    
	    val = fields.get((Symbol)args[0]);
	    if(val == null) {
		throw new RuntimeException(String.format(_invalid_field_name,
							 name,
							 args[0].toString()));
	    }
	    return val;
	case 2:
	    /* Being used as a set */
	    if(!(args[0] instanceof Symbol)) {
		String classname = args[0].getClass().getName();
		throw new RuntimeException(String.format(_invalid_field_type,
							 args[0].toString(),
							 classname));
	    }
	    
	    val = fields.get((Symbol)args[0]);
	    if(val == null) {
		throw new RuntimeException(String.format(_invalid_field_name,
							 name,
							 args[0].toString()));
	    }
	    
	    return fields.put((Symbol)args[0], args[1]);
	default:
	    throw new RuntimeException("Wrong number of arguments to struct");
	}
    }
    
    public TypeId typeId() {
	return type;
    }
    
    public String toString() {
	return name;
    }
}
