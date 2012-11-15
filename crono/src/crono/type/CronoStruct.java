package crono.type;

import java.util.HashMap;
import java.util.Map;

import crono.InterpreterException;
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
    private static final String _inv_field_name =
	"Invalid field name: (%s %s) does not exist";
    private static final String _field_type_mismatch =
	"Field %s expects %s, got %s";
    private static final TypeId[] _args = {Symbol.TYPEID, CronoType.TYPEID};
    
    public class Field {
	public final Symbol sym;
	public final TypeId type;
	private CronoType data;
	
	public Field(Symbol sym, TypeId type, CronoType data) {
	    this.sym = sym;
	    this.type = type;
	    this.data = data;
	}
	public CronoType put(CronoType data) {
	    if(!(type.isType(data))) {
		throw new InterpreterException(_field_type_mismatch, sym,
					       type, data.typeId());
	    }
	    this.data = data;
	    return data;
	}
	public CronoType get() {
	    return data;
	}
    }
    
    public final String name;
    public final CronoStruct parent;
    private Map<Symbol, Field> fields;
    private TypeId type;
    
    public CronoStruct(String name, Map<Symbol, Field> fields) {
	this(name, fields, null);
    }
    public CronoStruct(String name, Map<Symbol, Field> fields,
		       CronoStruct parent)
    {
	super(_args, CronoType.TYPEID, 1, true, Function.EvalType.NONE);
	this.name = name;
	this.parent = parent;
	this.fields = new HashMap<Symbol, Field>();
	TypeId parid = TYPEID;
	if(parent != null) {
	    /* Since all structs do this we only need to add the parent's
	     * fields */
	    fields.putAll(parent.fields);
	    parid = parent.typeId();
	}
	fields.putAll(fields);
	
	this.type = new TypeId(":struct-"+name, CronoStruct.class, parid);
    }
    
    public CronoType run(Visitor v, CronoType[] args) {
	/* CronoStruct doesn't contain an AST, so we can ignore the visitor */
	switch(args.length) {
	case 1:
	    /* Being used as a get */
	    CronoType val = fields.get((Symbol)args[0]).get();
	    if(val == null) {
		throw new InterpreterException(_inv_field_name, name,
					       args[0].toString());
	    }
	    return val;
	case 2:
	    /* Being used as a set */
	    Field field = fields.get((Symbol)args[0]);
	    if(field == null) {
		throw new InterpreterException(_inv_field_name, name,
					       args[0].toString());
	    }
	    
	    return field.put(args[1]);
	default:
	    throw new InterpreterException("Too many arguments to struct");
	}
    }
    
    public TypeId typeId() {
	return type;
    }
    
    public String toString() {
	return name;
    }
}
