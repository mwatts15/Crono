package crono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import crono.AbstractSyntax.CronoFunction;
import crono.AbstractSyntax.CronoType;

import static crono.CronoOptions.err;

/**
 */
public class CronoStruct implements CronoFunction {
    private final CronoStruct parent;
    private Map<String, CronoType> fields;
    private String name;

    /* Creates a new struct; should only be used when defining a new struct.
     */
    public CronoStruct(String name) {
	this(name, null);
    }
    public CronoStruct(String name, CronoStruct parent) {
	this.parent = parent;
	this.name = name;
	this.fields = new HashMap<String, CronoType>();
	if(parent != null) {
	    /* Copy all fields from parents, starting from the farthest down
	     * This ensures that any fields with overridden default values
	     * reflect the correct default value.
	     */
	    rAssimilate(parent);
	}
    }

    public CronoType put(String key, CronoType value) {
	CronoType t = fields.put(key, value);
	if(t == null) {
	    return Nil.NIL;
	}
	return t;
    }
    public CronoType get(String key) {
	CronoType t = fields.get(key);
	if(t == null) {
	    if(parent != null) {
		t = parent.get(key);
	    }
	    return Nil.NIL;
	}
	return t;
    }

    private void rAssimilate(CronoStruct s) {
	if(s.parent != null) {
	    rAssimilate(s.parent);
	}
	fields.putAll(s.fields); /*< Copy fields from the bottom up */
    }

    public CronoType run(CronoType[] args, Environment environment) {
	/* arg[0] is a field, arg[1] is an optional value */
	switch(args.length) {
	case 1:
	    if(!(args[0] instanceof Symbol)) {
		err("%s is not a field of the struct", args[0]);
	    }
	    return get(args[0].toString());
	case 2:
	    if(!(args[0] instanceof Symbol)) {
		err("%s is not a field of the struct", args[0]);
	    }
	    return put(args[0].toString(), args[1]);
	default:
	    err("too many arguments to struct");
	}
	return Nil.NIL; /*< Not needed */
    }

    public int arity() {
	/* Since the implicit struct function is variadic, we return the
	 * minimum number of args needed as a negative int. 1 for set, so -1.
	 */
	return -1;
    }
    public boolean canCurry() {
	/* Using the struct as an implicit function should be complete.
	 * Currying it on 0 arguments is useless, and currying it on two breaks
	 * getting the value.
	 */
	return false;
    }
    public boolean evalArgs() {
	return false;
    }

    /* Creates a copy, used when making an instance of the struct */
    public CronoStruct copy() {
	CronoStruct nstruct = new CronoStruct(this.name, parent);
	Set<Map.Entry<String, CronoType>> fieldset = fields.entrySet();
	CronoType t;
	for(Map.Entry<String, CronoType> entry : fieldset) {
	    t = entry.getValue();
	    if(t instanceof CronoStruct) {
		nstruct.put(entry.getKey(), ((CronoStruct)t).copy());
	    }else {
		nstruct.put(entry.getKey(), t); /*< Is some immutable type */
	    }
	}
	return nstruct;
    }

    public String toString() {
	return "struct";
    }
}
