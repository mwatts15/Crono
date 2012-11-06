package crono.type;

public class TypeId extends Atom {
    public static final TypeId TYPEID = new TypeId(":type-id", TypeId.class);
    
    public String image;
    public Class type;
    
    public TypeId parent;
    
    public TypeId(String image, Class type) {
	this(image, type, null);
    }
    public TypeId(String image, Class type, TypeId parent) {
	this.type = type;
	this.image = image;
	this.parent = parent;
    }
    
    public boolean isType(CronoType object) {
	TypeId id = object.typeId();
	while(id != null) {
	    if(this.equals(id)) {
		return true;
	    }
	    id = id.parent;
	}
	return false;
    }
    
    public boolean equals(Object o) {
	return ((o instanceof TypeId) &&
		type.equals(((TypeId)o).type) &&
		image.equals(((TypeId)o).image));
    }
    
    public TypeId typeId() {
	return TypeId.TYPEID;
    }
    public String toString() {
	return image;
    }
}
