package crono;

import crono.type.Atom;
import crono.type.Cons;
import crono.type.CronoType;
import crono.type.Function;
import crono.type.Nil;
import crono.type.Symbol;
import crono.type.TypeId;

public class Interpreter extends Visitor {
    public boolean show_env;
    public boolean show_closure;
    public boolean dprint_enable;
    public boolean dprint_ident;
    
    protected int indent_level;
    
    public Interpreter() {
	show_env = false;
	show_closure = false;
	dprint_enable = false;
	dprint_ident = true;
	indent_level = 0;
	
	Function.EvalType eval;
    }
    
    public CronoType visit(Cons c) {
	System.out.printf("Saw LIST");
	for(CronoType type : c) {
	    c.accept(this);
	}
	return Nil.NIL;
    }
    
    public CronoType visit(Atom a) {
	System.out.printf("Saw ATOM");
	if(a instanceof Symbol) {
	    
	}
	return a;
    }
}
