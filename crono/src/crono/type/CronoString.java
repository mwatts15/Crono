package crono.type;

import crono.InterpreterException;
import crono.Visitor;

public class CronoString extends CronoArray {
    public static final TypeId TYPEID = new TypeId(":string",
						   CronoString.class,
						   CronoArray.TYPEID);
    private static final String _index_oob =
	"String index out of bounds: %d > %d";
    private static final String _rank_mismatch =
	"String rank mismatch: %d != %d";
    
    protected StringBuilder data;
    protected int size;
    
    public CronoString(String str) {
	super(CronoCharacter.TYPEID);
	
	int size = str.length() - 2;
	data = new StringBuilder(size);
	
	boolean escape = false;
	char ch;
	for(int i = 1; i < (size + 1); ++i) {
	    ch = str.charAt(i);
	    if(escape) {
		data.append(CronoCharacter.escape(str.charAt(i)));
		escape = false;
	    }else {
		switch(ch) {
		case '\\':
		    escape = true;
		    break;
		default:
		    data.append(ch);
		    break;
		}
	    }
	}
	this.size = data.length();
    }
    
    public CronoString(int size) {
	super(CronoCharacter.TYPEID);
	
	data = new StringBuilder(size);
	this.size = size;
    }
    
    public int rank() {
	return 1;
    }
    public int dim(int n) {
	if(n != 1) {
	    throw new InterpreterException(_rank_mismatch, n, 1);
	}
	return size;
    }
    
    private int offset(int[] n) {
	if(n.length > 1) {
	    throw new InterpreterException(_rank_mismatch, n.length, 1);
	}
	if(n[0] >= size) {
	    throw new InterpreterException(_index_oob, n[0], size);
	}
	return n[0];
    }
    public CronoType get(int[] n) {
	return new CronoCharacter(data.charAt(offset(n)));
    }
    public CronoType put(int[] n, CronoType item) {
	data.setCharAt(offset(n), ((CronoCharacter)item).ch);
	return item;
    }
    
    public String image() {
	return data.toString();
    }
    
    public TypeId typeId() {
	return CronoString.TYPEID;
    }
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("\"");
	builder.append(data);
	builder.append("\"");
	return builder.toString();
    }
}
