package crono.type;

public class CronoString extends CronoArray {
    public static final TypeId TYPEID = new TypeId(":string",
						   CronoString.class,
						   CronoArray.TYPEID);
    
    protected StringBuilder data;
    protected int size;
    
    public CronoString(String str) {
	size = str.length() - 2;
	data = new StringBuilder(str.substring(1, size + 1));
    }
    
    public CronoString(int size) {
	data = new StringBuilder(size);
	this.size = size;
    }
    
    public int rank() {
	return 1;
    }
    public int dim(int n) {
	if(n != 1) {
	    throw new RuntimeException("Array index out of bounds");
	}
	return size;
    }
    public CronoType get(int[] n) {
	if(n.length != 1) {
	    throw new RuntimeException("Invalid number of indicies to get");
	}
	
	if(n[0] >= size) {
	    throw new RuntimeException("Array index out of bounds");
	}
	
	return new CronoCharacter(data.charAt(n[0]));
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
