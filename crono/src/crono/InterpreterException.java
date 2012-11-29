package crono;

public class InterpreterException extends RuntimeException {
    public InterpreterException(String message, Object... args) {
	super(String.format(message, args));
    }
}
