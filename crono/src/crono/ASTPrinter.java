package crono;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;
import crono.type.Nil;
import crono.type.Quote;

public class ASTPrinter extends Visitor {
    private int ident_level;
    private StringBuilder ident;
    public ASTPrinter() {
	ident_level = 0;
	ident = new StringBuilder("");
    }
    
    public void reset() { /* ... */ }
    
    public CronoType visit(Cons c) {
	System.out.printf("%sSaw LIST\n", ident.toString());
	
	ident_level++;
	ident.append(' ');
	
	for(CronoType t : c) {
	    t.accept(this);
	}
	
	ident_level--;
	ident.deleteCharAt(ident_level);
	return Nil.NIL;
    }
    
    public CronoType visit(Atom a) {
	System.out.printf("%sSaw ATOM: %s\n", ident.toString(), a.toString());
	return Nil.NIL;
    }
    
    public CronoType visit(Quote q) {
	System.out.printf("%sSaw QUOTE: %s\n", ident.toString(), q.toString());
	return Nil.NIL;
    }
}
