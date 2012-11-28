package crono;

import crono.type.CronoStruct;
import crono.type.CronoType;
import crono.type.CronoTypeId;
import crono.type.Function;
import crono.type.LambdaFunction;
import crono.type.Symbol;
import crono.type.TypeId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Environment {
    public boolean show_builtins, multiline, show_types;
    
    private Map<String, CronoType> symbols;
    private Map<String, CronoStruct> structs;
    private Map<String, CronoTypeId> types;
    private String repr;
    private boolean dirty;
    
    public Environment() {
	this(true);
    }
    
    public Environment(boolean builtins) {
	symbols = new HashMap<String, CronoType>();
	structs = new HashMap<String, CronoStruct>();
	types = new HashMap<String, CronoTypeId>();
	show_builtins = false;
	multiline = false;
	show_types = false;
	dirty = true; /*< dirty flag to rebuild string repr */
	
	this.put(crono.type.CronoType.TYPEID);
	this.put(crono.type.Atom.TYPEID);
	this.put(crono.type.Cons.TYPEID);	
	this.put(crono.type.CronoPrimitive.TYPEID);
	this.put(crono.type.CronoArray.TYPEID);
	this.put(crono.type.CronoCharacter.TYPEID);
	this.put(crono.type.CronoFloat.TYPEID);
	this.put(crono.type.CronoInteger.TYPEID);
	this.put(crono.type.CronoNumber.TYPEID);
	this.put(crono.type.CronoString.TYPEID);
	this.put(crono.type.CronoStruct.TYPEID);
	this.put(crono.type.CronoVector.TYPEID);
	this.put(crono.type.Function.TYPEID);
	this.put(crono.type.Nil.TYPEID);
	this.put(crono.type.Symbol.TYPEID);
	this.put(crono.type.CronoTypeId.TYPEID);
	
	if(builtins) {
	    for(CronoFunction cf : CronoFunction.values()) {
		put(new Symbol(cf.function.toString()), cf.function);
	    }
	}
    }
    
    public Environment(Environment env) {
	symbols = new HashMap<String, CronoType>(env.symbols);
	show_builtins = env.show_builtins;
	multiline = env.multiline;
	show_types = env.show_types;
	dirty = env.dirty;
    }
    
    public void put(CronoStruct struct) {
	dirty = true;
	this.structs.put(struct.name, struct);
    }
    public void put(TypeId type) {
	put(new CronoTypeId(type));
    }
    public void put(CronoTypeId type) {
	dirty = true;
	this.types.put(type.type.image, type);
    }
    
    public void put(Symbol sym, CronoType value) {
	dirty = true;
	symbols.put(sym.toString(), value);
    }
    
    public CronoType get(Symbol sym) {
	return symbols.get(sym.toString());
    }
    
    public CronoStruct getStruct(Symbol sym) {
	return structs.get(sym.toString());
    }
    public CronoTypeId getType(CronoTypeId id) {
	return getType(id.type.image);
    }
    public CronoTypeId getType(String str) {
	return types.get(str);
    }
    
    public void remove(Symbol sym) {
	dirty = true;
	symbols.remove(sym.toString());
    }
    
    public boolean contains(Symbol sym) {
	return symbols.containsKey(sym.toString());
    }
    
    public Iterator<Map.Entry<String, CronoType>> iterator() {
	return symbols.entrySet().iterator();
    }
    
    private boolean isBuiltin(CronoType item) {
	return (item instanceof Function && !(item instanceof LambdaFunction));
    }
    
    public String toString() {
	if(dirty) {
	    StringBuilder result = new StringBuilder();
	    Iterator<Map.Entry<String, CronoType>> iter = iterator();
	    Map.Entry<String, CronoType> entry;
	    String sym;
	    CronoType val;
	    boolean empty = true;
	    while(iter.hasNext()) {
		entry = iter.next();
		sym = entry.getKey();
		val = entry.getValue();
		if(show_builtins || !isBuiltin(val)) {
		    result.append("(let ((");
		    result.append(sym);
		    result.append(" ");
		    result.append(val.repr());
		    if(show_types) {
			result.append(" ");
			result.append(val.typeId());
		    }
		    result.append(")");
		    empty = false;
		    break;
		}
	    }
	    while(iter.hasNext()) {
		entry = iter.next();
		sym = entry.getKey();
		val = entry.getValue();
		if(show_builtins || !isBuiltin(val)) {
		    result.append(" (");
		    result.append(sym);
		    result.append(" ");
		    result.append(val.repr());
		    if(show_types) {
			result.append(" ");
			result.append(val.typeId());
		    }
		    result.append(")");
		}
	    }

	    if(empty) {
		repr = "empty";
	    }else {
		result.append(")");
		repr = result.toString();
	    }
	    dirty = false;
	}
	return repr;
    }
}