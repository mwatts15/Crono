package crono;

import crono.type.CronoType;

public class CronoException extends InterpreterException {
    public final CronoType thrown;
    public CronoException(CronoType thrown) {
        super("CronoException: %s", thrown);
        this.thrown = thrown;
    }
}