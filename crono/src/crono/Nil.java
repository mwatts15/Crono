package crono;

import crono.AbstractSyntax.Atom;
import crono.AbstractSyntax.CronoType;
import crono.Cons;

public class Nil extends Cons implements Atom {
  public static final Nil NIL = new Nil();

  private Nil() {}

  @Override
  public CronoType car() {
    return this;
  }

  @Override
  public CronoType cdr() {
    return this;
  }

  @Override
  public String toString() {
    return "NIL";
  }
}
