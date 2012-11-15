package crono;

import crono.type.CronoType;
import crono.type.Symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Environment {
    public boolean show_builtins, multiline, show_types;

    private Map<String, CronoType> symbols;
    private String display_string;
    private boolean update_display_string;

    public Environment() {
        this(true);
    }

    public Environment(boolean builtins) {
        symbols = new HashMap<String, CronoType>();
        show_builtins = false;
        multiline = true;
        show_types = false;
        update_display_string = true; /*< update_display_string flag to rebuild display_string */

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
        update_display_string = env.update_display_string;
    }

    public void put(Symbol sym, CronoType value) {
        update_display_string = true;
        symbols.put(sym.toString(), value);
    }

    public CronoType get(Symbol sym) {
        return symbols.get(sym.toString());
    }

    public void remove(Symbol sym) {
        update_display_string = true;
        symbols.remove(sym.toString());
    }

    public boolean contains(Symbol sym) {
        return symbols.containsKey(sym.toString());
    }

    public Iterator<Map.Entry<String, CronoType>> iterator() {
        return symbols.entrySet().iterator();
    }

    public String toString() {
        if(update_display_string) {
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

            display_string = result.toString();
            update_display_string = false;
        }
        return display_string;
    }
}
