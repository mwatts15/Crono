package crono;

import crono.type.CronoType;
import crono.type.Symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Environment {
    public boolean show_builtins, multiline, show_types;
    
    private Map<String, CronoType> symbols;
    private String repr;
    private boolean dirty;
    
    public Environment() {
	this(true);
    }
    
    public Environment(boolean builtins) {
	symbols = new HashMap<String, CronoType>();
	show_builtins = false;
	multiline = true;
	show_types = false;
	dirty = true; /*< dirty flag to rebuild string repr */
	
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
    
    public void put(Symbol sym, CronoType value) {
	dirty = true;
	symbols.put(sym.toString(), value);
    }
    
    public CronoType get(Symbol sym) {
	return symbols.get(sym.toString());
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
    
    public String toString() {
	if(dirty) {
	    StringBuilder result = new StringBuilder();
	    Iterator<Map.Entry<String, CronoType>> iter = iterator();
	    Map.Entry<String, CronoType> entry;
	    String sym;
	    CronoType val;
	    boolean hasnext = iter.hasNext();
	    while(hasnext) {
		entry = iter.next();
		sym = entry.getKey();
		val = entry.getValue();
		/* TODO: Check if the value is a builtin */
		result.append(sym);
		result.append(": ");
		result.append(val.toString());
		if(show_types) {
		    result.append(" [");
		    result.append(val.typeId().image);
		    result.append("]");
		}
		hasnext = iter.hasNext();
		if(hasnext) {
		    result.append(multiline ? "\n" : ", ");
		}
	    }
	    
	    repr = result.toString();
	    dirty = false;
	}
	return repr;
    }
}