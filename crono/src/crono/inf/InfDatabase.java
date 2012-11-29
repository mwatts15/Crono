package crono.inf;
import java.util.*;
import crono.type.*;
import crono.*;

public class InfDatabase{

	private static ArrayList<InfTriple> elements = new ArrayList<InfTriple>();
	
	public static boolean insert(InfTriple triple){
		if (elements.contains(triple)){
			System.out.println("Duplicate of "+triple+" detected!");
			return false;
		}
		return elements.add(triple);
	}
  
  public static void insertDefstruct(String name, Map<String, CronoStruct.Field> fields){
    InfClass nameClass = new InfClass(name);
    insert(new InfTriple(nameClass,new Rdf.Type(),new InfStruct.structDef()));
    for(Map.Entry<String,CronoStruct.Field> entry: fields.entrySet()){
      //CronoStruct.Field field = entry.getValue();
      String fieldname = entry.getKey();
      insert(new InfTriple(nameClass,new InfStruct.HasA(),new InfClass(fieldname)));
    }
    System.out.println("Inserted defstruct: "+printdb());
    return;
  }
  
  public static void clearEntailments(){
    //System.out.println("DESTROY");
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
	
	public static ArrayList<InfTriple> seek(InfClass iclass,int what){
		ArrayList<InfTriple> ret = new ArrayList<InfTriple>();
		for(Iterator i = elements.iterator();i.hasNext();){
			InfTriple t = (InfTriple)i.next();
			switch(what){
				default: case 0:
					if(iclass.equals(t.getSubject())) ret.add(t);
				break;
				case 1:
					if(iclass.equals(t.getPredicate())) ret.add(t);
				break;
				case 2:
					if(iclass.equals(t.getPredicate())) ret.add(t);
				break;}
		}
		return ret;
	}
	
  /*
  public static ArrayList<InfTriple> getStructFields(Environment env){
    ArrayList<InfTriple> ret = new ArrayList<InfTriple>();
    for(Iterator i = elements.iterator();i.hasNext()){
      InfTriple t = (InfTriple)i.next();
      if(t.getPredicate() instanceof InfStruct.HasA)
        insert(new InfTriple())
      }
    }
  }
  */
  
	public static boolean entail(Environment env){
		boolean changed;
		ArrayList<InfTriple> copy;
		do{
			//System.out.println("Entailing...");
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