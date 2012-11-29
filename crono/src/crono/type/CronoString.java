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
    private static final String _type_mismatch =
        "%s: type mismatch; %s != :string";
    
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
    
    public int size() {
        return data.length();
    }
    public CronoType get(int n) {
        return new CronoCharacter(data.charAt(n));
    }
    public CronoType put(int n, CronoType item) {
        data.setCharAt(n, ((CronoCharacter)item).ch);
        return item;
    }
    public CronoType append(CronoType item) {
        if(!(item instanceof CronoCharacter)) {
            throw new InterpreterException("Bad type: string expects char");
        }
        data.append(((CronoCharacter)item).ch);
        return item;
    }
    public CronoType insert(CronoArray array, int pos) {
        if(!(array instanceof CronoString)) {
            throw new InterpreterException(_type_mismatch, "insert",
                                           array.typeId());
        }
        data.insert(pos, ((CronoString)array).data);
        return this;
    }
    public CronoType concat(CronoArray array) {
        if(!(array instanceof CronoString)) {
            throw new InterpreterException(_type_mismatch, "insert",
                                           array.typeId());
        }
        
        data.append(((CronoString)array).data);
        
        return this;
    }
    
    public TypeId typeId() {
        return CronoString.TYPEID;
    }
    public String toString() {
        return data.toString();
    }
    public String repr() {
        return "\"" + data.toString() + "\"";
    }
}
