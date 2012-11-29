package crono.inf;
import java.util.*;
import crono.type.*;
import crono.*;

public class InfDatabase{

	private static ArrayList<InfTriple> elements = new ArrayList<InfTriple>();
	
	public static boolean insert(InfTriple triple){
		if (elements.contains(triple)){
			//System.out.println("Duplicate of "+triple+" detected!");
			return false;
		}
		return elements.add(triple);
	}
  
  public static void insertDefstruct(String name, Map<String, CronoStruct.Field> fields){
    InfClass nameClass = new InfClass(name);
    insert(new InfTriple(nameClass,new Rdf.Type(),new InfStruct.structDef()));
    for(Map.Entry<String,CronoStruct.Field> entry: fields.entrySet()){
      String fieldname = entry.getKey();
      insert(new InfTriple(nameClass,new InfStruct.HasA(),new InfClass(fieldname)));
    }
    return;
  }
  
  public static void insertStructInstance(Symbol name,CronoStruct value){
	InfDatabase.insert(new InfTriple(
							new InfClass(name.toString()),
							new InfStruct.IsA(),
							findName(value.toString())));
	return;
  }
  
  public static void clearEntailments(){
    for(Iterator i = elements.iterator();i.hasNext();){
      InfTriple t = (InfTriple)i.next();
      if(t.isEntailed())
        i.remove();
    }
    return;
  }
	
	public static ArrayList<InfTriple> getElements(){
		return elements;
	}
	
	public static InfClass findName(String name){
		for(Iterator i = elements.iterator();i.hasNext();){
			InfTriple t = (InfTriple)i.next();
			if(t.getSubject().toString().equals(name)) return t.getSubject();
			if(t.getPredicate().toString().equals(name)) return t.getPredicate();
			if(t.getObject().toString().equals(name)) return t.getObject();
		}
		return null;
	}
	
	public static ArrayList<InfTriple> seek(InfClass subj,InfClass pred, InfClass obj){
		return seek(subj,pred,obj,elements);		
	}
	
	public static ArrayList<InfTriple> seek(InfClass subj,InfClass pred, InfClass obj, ArrayList<InfTriple> db){
		ArrayList<InfTriple> ret = new ArrayList<InfTriple>();
		for(Iterator i = db.iterator();i.hasNext();){
			InfTriple t = (InfTriple)i.next();
				if ((subj==null || subj.equals(t.getSubject()))
				  &&(pred==null || pred.equals(t.getPredicate()))
				  &&(obj ==null || obj.equals(t.getObject())))
					ret.add(t);
		}
		return ret;
	}
	
  
  public static ArrayList<InfTriple> getStructFields(Environment env){
    ArrayList<InfTriple> ret = new ArrayList<InfTriple>();
    for(Iterator i = elements.iterator();i.hasNext();){
      InfTriple t = (InfTriple)i.next();
      if(t.getPredicate() instanceof InfStruct.HasA){
		//(defined name, hasa, fieldname)
		//->(defined name, fieldname, fieldvalue)
		  if(seek(t.getSubject(),t.getObject(),null).isEmpty()){
			CronoStruct parentStruct = (CronoStruct) env.get(new Symbol(t.getSubject().getName()));
			if(parentStruct == null) parentStruct = env.getStruct(new Symbol(t.getSubject().getName()));
			InfClass c = new InfClass(parentStruct.getField(t.getObject().getName()).get().toString());
			ret.add(new InfTriple(t.getSubject(),t.getObject(),c,true));
		  }
      }
    }
	return ret;
  }
  
  
	public static boolean entail(Environment env){
		boolean changed;
		ArrayList<InfTriple> copy;
		do{
			elements.addAll(getStructFields(env));
			copy = new ArrayList<InfTriple>(elements);
			changed = false;
			for(Iterator i = elements.iterator();i.hasNext();){
				InfTriple t = (InfTriple)i.next();
				t.getPredicate().fn(t,copy);
			}
      boolean exists = false;
      for(Iterator j = copy.iterator();j.hasNext();){
        InfTriple t = (InfTriple)j.next();
        exists = elements.contains(t);
        if(!exists){
          changed = true;
          insert(t);
        }
      }
      copy = null;
		}while(changed);	
		return false;
	}
	
	
  public static String printdb(){
		String ret = "Database:\n";
		for(Iterator i = elements.iterator();i.hasNext();){
			ret+=i.next()+"\n";
		}
		return ret;
	}
}