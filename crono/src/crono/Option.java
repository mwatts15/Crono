package crono;

public class Option {
    public final char shortopt;
    public final String longopt;
    public final boolean arg;
    
    public Option(char shortopt) {
	this(shortopt, null, false);
    }
    public Option(char shortopt, boolean arg) {
	this(shortopt, null, arg);
    }
    public Option(char shortopt, String longopt) {
	this(shortopt, longopt, false);
    }
    public Option(char shortopt, String longopt, boolean arg) {
	this.shortopt = shortopt;
	this.longopt = longopt;
	this.arg = arg;
    }
}
