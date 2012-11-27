package crono;
import java.util.List;
import crono.type.Function.EvalType;
import crono.type.*;
/* These are like functions but have evaluation
 * schemes that differ from normal operators.
 * They are separated mainly to emphasize that
 * distinction
 */
public enum CronoSpecial {
    LAMBDA(new Special(new TypeId[]{Cons.TYPEID, CronoType.TYPEID},
            Function.TYPEID, 2, true, EvalType.NONE)
    {
        public CronoType run(Visitor v, List<CronoType> args) {
                List<CronoType> list = ((Cons)args.remove(0)).toList();

                Symbol[] arglist = new Symbol[list.size()];
                return new LambdaFunction(list.toArray(arglist), args.remove(0),
                    v.getEnv());
            }
        public String toString() {
            return "\\";
        }
    });
    //DEFINE(new Function(new TypeId[]{Symbol.TYPEID, CronoType.TYPEID},
			//CronoType.TYPEID, 2, EvalType.NONE)
    //{
	//private static final String _bad_type =
		//"DEFINE: expected :symbol, got %s";

	//public CronoType run(Visitor v, CronoType[]args) {
		//if(!(args[0] instanceof Symbol)) {
		//throw new InterpreterException(_bad_type, args[0].typeId());
		//}
		//CronoType value = args[1].accept(v);
		//v.getEnv().put(((Symbol)args[0]), value);
		//return value;
	//}
	//public String toString() {
		//return "define";
	//}
    //}),
    //UNDEFINE(new Function(new TypeId[]{Symbol.TYPEID}, Nil.TYPEID, 1,
			  //EvalType.NONE)
    //{
	//private static final String _bad_type =
		//"UNDEF: expected :symbol, got %s";

	//public CronoType run(Visitor v, CronoType[] args) {
		//StringBuilder builder = new StringBuilder();
		//boolean errors = false;
		//for(CronoType ct : args) {
		//if(!(ct instanceof Symbol)) {
			//builder.append(String.format(_bad_type, ct.typeId()));
			//builder.append('\n');
			//errors = true;
			//continue;
		//}
		//v.getEnv().remove((Symbol)ct);
		//}

		//if(errors) {
		//[> Remove last newline <]
		//builder.deleteCharAt(builder.length() - 1);
		//throw new InterpreterException(builder.toString());
		//}
		//return Nil.NIL;
	//}
	//public String toString() {
		//return "undef";
	//}
    //}),
    //LET(new Function(new TypeId[]{Cons.TYPEID, CronoType.TYPEID},
			 //CronoType.TYPEID, 2, true, EvalType.NONE)
    //{
	//private static final String _subst_list_type =
		//"LET: substitution list must be :cons, got %s";
	//private static final String _subst_not_cons =
		//"LET: expected :cons in substitution list, got %s";
	//private static final String _subst_not_sym =
		//"LET: argument names numst be :symbol, got %s";

	//public CronoType run(Visitor v, CronoType[] args) {
		//if(!(args[0] instanceof Cons)) {
		//throw new InterpreterException(_subst_list_type,
						   //args[0].typeId());
		//}

		//List<Symbol> symlist = new LinkedList<Symbol>();
		//List<CronoType> arglist = new LinkedList<CronoType>();
		//for(CronoType ct : ((Cons)args[0])) {
		//if(!(ct instanceof Cons)) {
			//throw new InterpreterException(_subst_not_cons,
						   //ct.typeId());
		//}

		//CronoType car = ((Cons)ct).car();
		//CronoType cdr = ((Cons)ct).cdr();
		//if(!(car instanceof Symbol)) {
			//throw new InterpreterException(_subst_not_sym,
						   //car.typeId());
		//}

		//cdr = cdr.accept(v);
		//symlist.add((Symbol)car);
		//arglist.add(cdr);
		//}

		//Symbol[] lsyms = new Symbol[symlist.size()];
		//lsyms = symlist.toArray(lsyms);

		//List<CronoType> bodylist = new LinkedList<CronoType>();
		//for(int i = 1; i < args.length; ++i) {
		//bodylist.add(args[i]);
		//}
		//CronoType body = Cons.fromList(bodylist);
		//CronoType[] largs = new CronoType[arglist.size()];
		//largs = arglist.toArray(largs);

		//LambdaFunction lambda = new LambdaFunction(lsyms,body,v.getEnv());
		//return lambda.run(v, largs); [>< -. - <]
	//}
	//public String toString() {
		//return "let";
	//}
    //}),
    //IF(new Function(new TypeId[]{CronoType.TYPEID, CronoType.TYPEID,
				 //CronoType.TYPEID},
		//CronoType.TYPEID, 3, EvalType.NONE)
    //{
	//public CronoType run(Visitor v, CronoType[] args) {
		//CronoType check = args[0].accept(v);
		//if(check != Nil.NIL) {
		//return args[1].accept(v);
		//}
		//return args[2].accept(v);
	//}
        //public String toString() {
		//return "if";
	//}
    //}),
    public final Special special;
    private CronoSpecial(Special spec) {
        this.special = spec;
    }
}
