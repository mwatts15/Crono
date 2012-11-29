package crono;

import crono.type.CronoType;
import crono.type.Function;

public class TooManyArgsException extends InterpreterException {
    protected static String argsTypeString(CronoType[] args) {
        StringBuilder builder = new StringBuilder("[");
        if(args.length > 0) {
            builder.append(args[0].typeId().toString());
        }
        for(int i = 1; i < args.length; ++i) {
            builder.append(", ");
            builder.append(args[i].typeId().toString());
        }
        builder.append("]");
        return builder.toString();
    }
    
    public TooManyArgsException(Function f, int required, int given) {
        super("Too many arguments to %s: %d/%d recieved", f, required, given);
    }
    public TooManyArgsException(Function f, int r, int g, CronoType[] args) {
        super("Too many arguments to %s: %d/%d recieved; args = %s", f,r,g,
              argsTypeString(args));
    }
}
