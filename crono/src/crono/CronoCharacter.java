package crono;

import crono.AbstractSyntax.Atom;

public class CronoCharacter implements Atom {
    public final Character ch;
    
    public CronoCharacter(Character ch) {
	this.ch = ch;
    }
    
    public String toString() {
	return ch.toString();
    }
    
    public boolean equals(Object o) {
	if(o instanceof CronoCharacter) {
	    return (ch == ((CronoCharacter)o).ch);
	}
	if(o instanceof CronoNumber) {
	    return (((long)ch.charValue()) == ((CronoNumber)o).num);
	}
	return false;
    }
    
    public static CronoCharacter valueOf(char ch) {
	return new CronoCharacter(Character.valueOf(ch));
    }
    
    public static CronoCharacter escape(char ch) {
	switch(ch) {
	case 'b':
	    return new CronoCharacter('\b');
	case 'f':
	    return new CronoCharacter('\f');
	case 'n':
	    return new CronoCharacter('\n');
	case 'r':
	    return new CronoCharacter('\r');
	case 't':
	    return new CronoCharacter('\t');
	default:
	    return new CronoCharacter(ch);
	}
    }
}

