package crono;

import crono.type.TypeId;

public class TypeScopeException extends InterpreterException {
    public TypeScopeException(TypeId id) {
        super("No type %s in scope", id.toString());
    }
}
