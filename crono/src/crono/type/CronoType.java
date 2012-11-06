package crono.type;

import crono.Visitor;

public abstract class CronoType {
    public static final TypeId TYPEID = new TypeId(":any", CronoType.class);
    
    protected boolean quoted;
    
    public void quote(boolean quote) {
	quoted = quote;
    }
    public boolean isQuoted() {
	return quoted;
    }
    
    public abstract TypeId typeId();
    public abstract String toString();
    
    public abstract CronoType accept(Visitor v);
}
