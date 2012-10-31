package crono;

import crono.AbstractSyntax.Atom;

public class CronoFloat implements Atom {
  public final Double num;

  public CronoFloat(Double num) {
    this.num = num;
  }

  public boolean equals(Object o) {
    if(o instanceof CronoFloat) {
      return (num == ((CronoFloat)o).num);
    }
    if(o instanceof CronoNumber) {
      return (num == ((double)((CronoNumber)o).num));
    }
    return false;
  }

  public String toString() {
    return num.toString();
  }
  public static CronoFloat valueOf(double d) {
    return new CronoFloat(Double.valueOf(d));
  }
}
