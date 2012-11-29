package crono.type;

public class CronoInteger extends CronoNumber {
    public static final TypeId TYPEID = new TypeId(":int", CronoInteger.class,
                                                   CronoNumber.TYPEID);
    
    public final long value;
    
    public CronoInteger(String image) {
        this(Long.valueOf(image));
    }
    public CronoInteger(long value) {
        this.value = value;
    }
    
    public TypeId typeId() {
        return CronoInteger.TYPEID;
    }
    public String toString() {
        return ((Long)value).toString();
    }
    
    public boolean equals(Object o) {
        return ((o instanceof CronoInteger) &&
                ((CronoInteger)o).value == value);
    }
}