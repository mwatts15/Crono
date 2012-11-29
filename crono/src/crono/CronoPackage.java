package crono;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import crono.type.CronoType;
import crono.type.Function;
import crono.type.Symbol;
import crono.type.TypeId;

public abstract class CronoPackage {
    public class SymbolPair {
        public final Symbol sym;
        public final CronoType type;
        public SymbolPair(Symbol sym, CronoType type) {
            this.sym = sym;
            this.type = type;
        }
        public SymbolPair(String sym, CronoType type) {
            this(new Symbol(sym), type);
        }
    }
    
    public abstract Function[] functions();
    public abstract TypeId[] types();
    public abstract SymbolPair[] symbols();
    
    /* Package Loading */
    private static final String _package_not_found =
        "Package \"%s\" not found";
    private static final String _bad_package =
        "Bad package \"%s\": %s";
    private static final String _not_a_package =
        "\"%s\" is not a package";
    private static URLClassLoader loader = null;
    private static Map<String, CronoPackage> loaded =
        new HashMap<String, CronoPackage>();
    private static URL[] urls = null;
    
    public static void initLoader(URL[] urls) {
        CronoPackage.urls = urls;
        loader = new URLClassLoader(urls);
    }
    public static void addURLs(URL[] newURLs) {
        URL[] oldpath = urls;
        URL[] newpath = new URL[oldpath.length + newURLs.length];
        for(int i = 0; i < oldpath.length; ++i) {
            newpath[i] = oldpath[i];
        }
        for(int i = 0; i < newURLs.length; ++i) {
            newpath[i + oldpath.length] = newURLs[i];
        }
        initLoader(newpath);
    }
    public static URL[] getClasspath() {
        return urls;
    }
    public static CronoPackage load(String name)
    {
        CronoPackage ret = loaded.get(name);
        if(ret != null) {
            return ret;
        }
        
        if(loader == null) {
            throw new InterpreterException(_package_not_found, name);
        }
        
        /* Dynamically load the class */
        try {
            Class c = loader.loadClass(name);
            ret = ((CronoPackage)c.newInstance());
            loaded.put(name, ret);
            return ret;
        }catch(ClassNotFoundException cnfe) {
            throw new InterpreterException(_package_not_found, name);
        }catch(InstantiationException ie) {
            throw new InterpreterException(_bad_package, name,
                                           "Could not create instance");
        }catch(IllegalAccessException iae) {
            throw new InterpreterException(_bad_package, name,
                                           "Constructor is not public");
        }catch(ClassCastException cce) {
            throw new InterpreterException(_not_a_package, name);
        }
    }
}
