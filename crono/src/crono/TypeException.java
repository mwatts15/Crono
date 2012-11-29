package crono;
import crono.type.CronoType;
import crono.type.TypeId;
import crono.InterpreterException;

public class TypeException extends InterpreterException {
    public TypeException(CronoType context, TypeId expected, CronoType received) {
        super(String.format("Expected %s, got %s(%s) in %s", expected, received, received.TYPEID, context));
    }
}
