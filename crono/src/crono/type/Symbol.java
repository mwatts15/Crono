package crono.type;

public class Symbol extends Atom {
    public static final TypeId TYPEID = new TypeId(":symbol", Symbol.class);
    
    private final String name;
    
    public Symbol(String name) {
	this.name = name;
    }
    
    public boolean equals(Object o) {
	return ((o instanceof Symbol) &&
		(this.name.equals(((Symbol)o).name)));
    }
    
    public TypeId typeId() {
	return Symbol.TYPEID;
    }
    public String toString() {
	if(isQuoted()) {
	    return "'" + this.name;
	}
	return this.name;
    }
}
