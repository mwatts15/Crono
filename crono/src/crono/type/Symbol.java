package crono.type;

public class Symbol extends Atom {
    public static final TypeId TYPEID = new TypeId(":symbol", Symbol.class);
    
    private final String name;
    
    public Symbol(String name) {
	this.name = name;
    }
    
    public TypeId typeId() {
	return Symbol.TYPEID;
    }
    public String toString() {
	return this.name;
    }
}
