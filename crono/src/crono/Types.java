package crono;

import java.util.HashMap;
import java.util.Map;

import crono.type.TypeId;

public class Types {
    private Map<String, TypeId> types;
    
    public Types() {
	types = new HashMap<String, TypeId>();
	/* Put the default types here */
	this.put(crono.type.Cons.TYPEID);
	this.put(crono.type.CronoArray.TYPEID);
	this.put(crono.type.CronoCharacter.TYPEID);
	this.put(crono.type.CronoFloat.TYPEID);
	this.put(crono.type.CronoInteger.TYPEID);
	this.put(crono.type.CronoNumber.TYPEID);
	this.put(crono.type.CronoPrimitive.TYPEID);
	this.put(crono.type.CronoString.TYPEID);
	this.put(crono.type.CronoStruct.TYPEID);
	this.put(crono.type.CronoType.TYPEID);
	this.put(crono.type.CronoVector.TYPEID);
	this.put(crono.type.Function.TYPEID);
	this.put(crono.type.Nil.TYPEID);
	this.put(crono.type.Symbol.TYPEID);
	this.put(crono.type.TypeId.TYPEID);
    }
    
    public TypeId put(TypeId id) {
	TypeId old = types.put(id.image, id);
	return old;
    }
    
    public TypeId get(String image) {
	return types.get(image);
    }
    public TypeId get(TypeId partial) {
	TypeId id = types.get(partial.image);
	if(id == null) {
	    return partial;
	}
	return id;
    }
    
}