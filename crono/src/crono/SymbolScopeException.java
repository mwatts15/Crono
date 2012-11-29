package crono;

import crono.type.Symbol;

public class SymbolScopeException extends InterpreterException {
    public SymbolScopeException(Symbol sym) {
        super("No object %s in scope", sym);
    }
}
