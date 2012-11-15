package crono.type;

import crono.Visitor;

public class Quote extends CronoType {
    public final CronoType node;
    public Quote(CronoType node) {
	this.node = node;
    }
    
    public CronoType accept(Visitor v) {
	return v.visit(this);
    }
    
    public TypeId typeId() {
	return node.typeId();
    }
    
    public String toString() {
	return "'" + node.toString();
    }
}
