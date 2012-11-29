package crono.inf;
import java.util.*;

public class InfTriple
{
	private InfClass subject;
	private InfClass predicate;
	private InfClass object;
  private boolean entailed;

	//constructors
	public InfTriple()
	{
		this.subject = new InfClass();
		this.predicate = new InfClass();
		this.object = new InfClass();
    this.entailed = entailed;
	}
	public InfTriple(InfClass subject,InfClass predicate,InfClass object)
	{
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
    this.entailed = entailed;
	}
  public InfTriple(InfClass subject,InfClass predicate,InfClass object,boolean entailed){
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
    this.entailed = entailed;
  }
	//accessors/mutators
	public InfClass getSubject(){
		return subject;
	}
	public InfClass getPredicate(){
		return predicate;
	}
	public InfClass getObject(){
		return object;
	}
  public boolean isEntailed(){
    return entailed;
  }
	
	//overrides
	@Override public String toString()
	{
		return "Triple:("+getSubject().getName()+"("+getSubject().getID()+"),"
                     +getPredicate().getName()+"("+getPredicate().getID()+"),"
                     +getObject().getName()+"("+getObject().getID()+"))"+(isEntailed()?" - Entailed":"");
	}	
	
	@Override public boolean equals(Object triple2)
	{
		if(triple2 instanceof InfTriple)
		{
			InfTriple that = (InfTriple)triple2;
			return 	this.getSubject().getID() == that.getSubject().getID() &&
					this.getPredicate().getID() == that.getPredicate().getID() &&
					this.getObject().getID() == that.getObject().getID();
			//return this.hashCode() == ((InfClass)triple2).hashCode();
			
		}
		return false;
	}
	
	@Override public int hashCode()
	{
		return (41*(41+getSubject().getID())) + (23*(23+getPredicate().getID())) + getObject().getID();
	}
	
}