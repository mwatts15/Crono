package crono.type;

public abstract class CronoNumber extends CronoPrimitive {
    public static final TypeId TYPEID = new TypeId(":number",
						   CronoNumber.class,
						   CronoPrimitive.TYPEID);
}
