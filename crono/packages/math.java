import crono.CronoPackage;
import crono.CronoPackage.SymbolPair;
import crono.InterpreterException;
import crono.Visitor;
import crono.type.CronoFloat;
import crono.type.CronoInteger;
import crono.type.CronoNumber;
import crono.type.CronoType;
import crono.type.Function;
import crono.type.TypeId;

public class math extends CronoPackage {
    public static final String _unsupported_type =
	"%s: Unsupported type %s\n";
    
    private class AbsoluteValueFunction extends Function {
	private static final String _name = "abs";
	public AbsoluteValueFunction() {
	    super(new TypeId[]{CronoNumber.TYPEID}, CronoNumber.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    if(args[0] instanceof CronoInteger) {
		long lval = Math.abs(((CronoInteger)args[0]).value);
		return new CronoInteger(lval);
	    }else if(args[0] instanceof CronoFloat) {
		double dval = Math.abs(((CronoFloat)args[0]).value);
		return new CronoFloat(dval);
	    }
	    throw new InterpreterException(_unsupported_type, _name,
					   args[0].typeId());
	}
	public String toString() {
	    return _name;
	}
    }
    private class AcosFunction extends Function {
	private static final String _name = "acos";
	public AcosFunction() {
	    super(new TypeId[]{CronoNumber.TYPEID}, CronoFloat.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    double val = 0.0;
	    if(args[0] instanceof CronoInteger) {
		val = ((CronoInteger)args[0]).value;
	    }else if(args[0] instanceof CronoFloat) {
		val = ((CronoFloat)args[0]).value;
	    }else {
		throw new InterpreterException(_unsupported_type, _name,
					       args[0].typeId());
	    }
	    return new CronoFloat(Math.acos(val));
	}
	public String toString() {
	    return _name;
	}
    }
    private class AsinFunction extends Function {
	private static final String _name = "asin";
	public AsinFunction() {
	    super(new TypeId[]{CronoNumber.TYPEID}, CronoFloat.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    double val = 0.0;
	    if(args[0] instanceof CronoInteger) {
		val = ((CronoInteger)args[0]).value;
	    }else if(args[0] instanceof CronoFloat) {
		val = ((CronoFloat)args[0]).value;
	    }else {
		throw new InterpreterException(_unsupported_type, _name,
					       args[0].typeId());
	    }
	    return new CronoFloat(Math.asin(val));
	}
	public String toString() {
	    return _name;
	}
    }
    private class AtanFunction extends Function {
	private static final String _name = "atan";
	public AtanFunction() {
	    super(new TypeId[]{CronoNumber.TYPEID}, CronoFloat.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    double val = 0.0;
	    if(args[0] instanceof CronoInteger) {
		val = ((CronoInteger)args[0]).value;
	    }else if(args[0] instanceof CronoFloat) {
		val = ((CronoFloat)args[0]).value;
	    }else {
		throw new InterpreterException(_unsupported_type, _name,
					       args[0].typeId());
	    }
	    return new CronoFloat(Math.atan(val));
	}
	public String toString() {
	    return _name;
	}
    }
    private class CubeRootFunction extends Function {
	private static final String _name = "cbrt";
	public CubeRootFunction() {
	    super(new TypeId[]{CronoNumber.TYPEID}, CronoFloat.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    double val = 0.0;
	    if(args[0] instanceof CronoInteger) {
		val = ((CronoInteger)args[0]).value;
	    }else if(args[0] instanceof CronoFloat) {
		val = ((CronoFloat)args[0]).value;
	    }else {
		throw new InterpreterException(_unsupported_type, _name,
					       args[0].typeId());
	    }
	    return new CronoFloat(Math.cbrt(val));
	}
	public String toString() {
	    return _name;
	}
    }
    private class CeilingFunction extends Function {
	private static final String _name = "ceil";
	public CeilingFunction() {
	    super(new TypeId[]{CronoFloat.TYPEID}, CronoFloat.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    double val = ((CronoFloat)args[0]).value;
	    return new CronoFloat(Math.ceil(val));
	}
	public String toString() {
	    return _name;
	}
    }
    private class CosineFunction extends Function {
	private static final String _name = "cos";
	public CosineFunction() {
	    super(new TypeId[]{CronoNumber.TYPEID}, CronoFloat.TYPEID, 1);
	}
	public CronoType run(Visitor v, CronoType[] args) {
	    double val = 0.0;
	    if(args[0] instanceof CronoInteger) {
		val = ((CronoInteger)args[0]).value;
	    }else if(args[0] instanceof CronoFloat) {
		val = ((CronoFloat)args[0]).value;
	    }else {
		throw new InterpreterException(_unsupported_type, _name,
					       args[0].typeId());
	    }
	    return new CronoFloat(Math.cos(val));
	}
	public String toString() {
	    return _name;
	}
    }
    
    private Function[] _funcs;
    private SymbolPair[] _syms;
    public math() {
	_funcs = new Function[] {
	    new AbsoluteValueFunction(),
	    new AcosFunction(),
	    new AsinFunction(),
	    new AtanFunction(),
	    new CubeRootFunction(),
	    new CeilingFunction(),
	    new CosineFunction(),
	};
	_syms = new SymbolPair[] {
	    new SymbolPair("m_pi", new CronoFloat(Math.PI)),
	    new SymbolPair("m_e", new CronoFloat(Math.E)),
	};
    }
    public Function[] functions() {
	return _funcs;
    }
    public TypeId[] types() {
	return null;
    }
    public SymbolPair[] symbols() {
	return _syms;
    }
}
