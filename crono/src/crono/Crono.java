package crono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import crono.type.CronoType;

public class Crono {
    public static final Option[] options = {
	new Option('d', "dynamic"),
	new Option('D', "debug"),
	new Option('e', "show-environment"),
	new Option('h', "help"),
	new Option('p', "print-ast"),
	new Option('q', "quiet"),
	new Option('s', "static"),
	new Option('t', "show-types"),
	new Option('T', "trace")
    };
    public static final String helpstr =
	"usage Crono [-dDhs]";
    public static final String introstr =
	"Crono++ by Mark Watts, Carlo Vidal, Troy Varney (c) 2012\n";
    public static final String prompt = "> ";
    
    private static CronoType getStatement(Parser p) {
	/* This was supposed to loop the parser until it got a valid statement
	 * or hit EOF, but I can't get it to work quite right */
	CronoType statement = null;
	System.out.print(prompt);
	try {
	    statement = p.statement();
	}catch(ParseException pe) {
	    System.err.println(pe);
	    statement = null;
	}
	
	return statement;
    }
    
    public static void main(String[] args) {
	OptionParser optparse = new OptionParser(args);
	Interpreter interp = new Interpreter();
	Visitor v = interp;
	boolean interactive = (System.console() != null); /*< Java 6 feature */
	List<String> files = new LinkedList<String>();
	
	int opt = optparse.getopt(options);
	while(opt != -1) {
	    switch(opt) {
	    case 'd':
		interp.dynamic(true);
		break;
	    case 'D':
                interp.showEnv(true);
                interp.printAST(true);
                interp.trace(true);
		break;
	    case 'e':
		interp.showEnv(true);
		break;
	    case 'h':
		System.err.println(helpstr);
		return;
	    case 'p':
		interp.printAST(true);
		break;
	    case 'q':
                interp.showEnv(false);
                interp.printAST(false);
                interp.trace(false);
		break;
	    case 's':
		interp.dynamic(false);
		break;
	    case 't':
		interp.getEnv().show_types = true;
		break;
	    case 'T':
		interp.trace(true);
		break;
	    case '?':
	    default:
		System.err.printf("Invalid option: %s\n",
				  optparse.optchar);
		System.err.println(helpstr);
		return;
	    }
	    opt = optparse.getopt(options);
	}
	
	for(int i = optparse.optind(); i < args.length; ++i) {
	    files.add(args[i]);
	}
	
	Parser parser = null;
	try {
	    File package_dir = new File("./packages/");
	    CronoPackage.initLoader(new URL[]{package_dir.toURI().toURL()});
	}catch(MalformedURLException murle) {
	    System.err.printf("Crono: Could not open package directory!\n");
	}
	
	if(interactive && files.size() == 0) {
	    parser = new Parser(new InputStreamReader(System.in));
	    System.out.println(introstr);
	    
	    boolean good = false;
	    CronoType statement = getStatement(parser);
	    while(statement != null) {
		try{
		    statement = statement.accept(v);
		    System.out.printf("Result: %s\n", statement.repr());
		}catch(InterpreterException re) {
		    String message = re.getMessage();
		    if(message != null) {
			System.err.println(message);
		    }else {
			System.err.println("Unknown Interpreter Error!");
		    }
		}catch(RuntimeException re) {
		    re.printStackTrace();
		}
		statement = getStatement(parser);
	    }
	    
	    System.out.println();
	}else {
	    for(String fname : files) {
		try {
		    parser = new Parser(new FileReader(fname));
		}catch(FileNotFoundException fnfe) {
		    System.err.printf("Could not find %s:\n  %s\n", fname,
				      fnfe.toString());
		    continue;
		}
		
		try {
		    CronoType head = parser.program();
		    head.accept(v);
		} catch(ParseException pe) {
		    System.err.printf("Error parsing crono file: %s\n  %s\n",
				      fname, pe);
		}
		
		v.reset(); /*< Reset the visitor for the next file */
	    }
	}
    }
}