package crono;

import crono.AbstractSyntax.Atom;

//TODO:
//    - could be replaced with an arbitrary precision Rational class
//    - memoize numbers?
public class CronoNumber implements Atom {
  public final Long num;

  public CronoNumber(Long num) {
    this.num = num;
  }

  public boolean equals(Object o) {
    if(o instanceof CronoNumber) {
      return (num == ((CronoNumber)o).num);
    }
    if(o instanceof CronoFloat) {
      return (((double)num) == ((CronoFloat)o).num);
    }
    return false;
  }

  public String toString() {
    return num.toString();
  }

  public static CronoNumber valueOf(int i) {
    return new CronoNumber(Long.valueOf(i));
  }

  public static CronoNumber valueOf(long l) {
    return new CronoNumber(Long.valueOf(l));
  }
}
