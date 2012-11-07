package crono.type;

public class CronoFloat extends CronoNumber {
    public static final TypeId TYPEID = new TypeId(":float", CronoFloat.class,
						   CronoNumber.TYPEID);
    
    public final double value;
    
    public CronoFloat(String image) {
	this(Double.valueOf(image));
    }
    public CronoFloat(double value) {
	this.value = value;
    }
    
    public TypeId typeId() {
	return CronoFloat.TYPEID;
    }
    public String toString() {
	return ((Double)value).toString();
    }
    
    public boolean equals(Object o) {
	return ((o instanceof CronoFloat) && ((CronoFloat)o).value == value);
    }
}
