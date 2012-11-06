package crono;

import crono.type.CronoType;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, CronoType> symbols;
    public Environment() {
	symbols = new HashMap<String, CronoType>();
    }
}