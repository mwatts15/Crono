package crono;

import crono.AbstractSyntax.Atom;

//TODO:
//    - could be replaced with an arbitrary precision Rational class
//    - support for floating point
//    - memoize numbers?
public class CronoNumber implements Atom {
  public final Long num;

  public CronoNumber(Long num) {
    this.num = num;
  }

  public boolean equals(Object o) {
    if (o instanceof CronoNumber) {
      CronoNumber n = (CronoNumber)o;
      return this.num.equals(n.num);
    } else {
      return false;
    }
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
