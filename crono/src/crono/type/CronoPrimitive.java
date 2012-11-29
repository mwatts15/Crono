package crono.type;

public abstract class CronoPrimitive extends Atom {
    public static final TypeId TYPEID = new TypeId(":primitive",
                                                   CronoPrimitive.class,
                                                   Atom.TYPEID);
}
