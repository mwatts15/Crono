package crono;

import java.util.HashMap;
import java.util.Map;

import crono.AbstractSyntax.Atom;

/**
 * Symbol class
 *
 * All symbols are unique and case insensitive.
 */
public class Symbol implements Atom {
  private final String name;

  private static final Map<String, Symbol> symbols = new HashMap<String, Symbol>();

  private Symbol(String s) {
    this.name = s.toLowerCase();
  }

  public String toString() {
    return this.name;
  }

  public boolean equals(Object o) {
    if (o instanceof Symbol) {
      return ((Symbol)o).name.equals(this.name);
    } else {
      return false;
    }
  }

  public static Symbol valueOf(String s) {
    s = s.toLowerCase();
    if (symbols.containsKey(s)) {
      return symbols.get(s);
    } else {
      Symbol result = new Symbol(s);
      symbols.put(s, result);
      return result;
    }
  }
}

