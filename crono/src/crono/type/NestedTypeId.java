package crono.type;

import java.util.Arrays;

public class NestedTypeId extends TypeId {
    public final TypeId[] inner;
    public NestedTypeId(String name, Class type, TypeId[] inner) {
        this(name, type, inner, null);
    }
    public NestedTypeId(String name, Class type, TypeId[] inner, TypeId par) {
        super(name, type, par);
        this.inner = inner;
    }
    
    public boolean equals(Object o) {
        if(o instanceof NestedTypeId) {
            NestedTypeId id = ((NestedTypeId)o);
            if(id.image.equals(image) && id.type.equals(type) &&
               id.inner.length == inner.length)
            {
                for(int i = 0; i < inner.length; ++i) {
                    if(!(inner[i].equals(id.inner[i]))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return String.format("%s%s", this.image, Arrays.toString(inner));
    }
}
