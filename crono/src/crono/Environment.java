package crono;

import crono.type.CronoType;
import crono.type.Symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Environment {
    private Map<Symbol, CronoType> symbols;
    private boolean show_builtins, multiline, show_types;
    
    private String repr;
    private boolean dirty;
    
    public Environment() {
	symbols = new HashMap<Symbol, CronoType>();
	show_builtins = false;
	multiline = true;
	show_types = false;
	dirty = true; /*< dirty flag to rebuild string repr */
    }
    
    public void put(Symbol sym, CronoType value) {
	dirty = true;
	symbols.put(sym, value);
    }
    
    public CronoType get(Symbol sym) {
	return symbols.get(sym);
    }
    
    public boolean contains(Symbol sym) {
	return symbols.containsKey(sym);
    }
    
    public Iterator<Map.Entry<Symbol, CronoType>> iterator() {
	return symbols.entrySet().iterator();
    }
    
    public String toString() {
	if(dirty) {
	    StringBuilder result = new StringBuilder();
	    Iterator<Map.Entry<Symbol, CronoType>> iter = iterator();
	    Map.Entry<Symbol, CronoType> entry;
	    Symbol sym;
	    CronoType val;
	    boolean hasnext = iter.hasNext();
	    while(hasnext) {
		entry = iter.next();
		sym = entry.getKey();
		val = entry.getValue();
		/* TODO: Check if the value is a builtin */
		result.append(sym.toString());
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