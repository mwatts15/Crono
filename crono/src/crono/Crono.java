package crono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import crono.Interpreter;
import crono.LambdaFunction;
import crono.Parser;

public class Crono {
  public static String crono_intro =
    "Crono v2.0 by Troy Varney, Carlo Vidal, Mark Watts\n";
    
  public static void main(String[] args) throws FileNotFoundException,
         ParseException {
    // If there is a command line argument, interpret it as a stdin.
    InputStream in = System.in;
    
    CronoOptions.parseargs(args);
    if(CronoOptions.FILES.size() > 0) {
	in = new FileInputStream(CronoOptions.FILES.get(0));
    }else {
	System.out.println(crono_intro);
    }
    
    Environment env = Interpreter.getDefaultEnvironment();
    Parser parser = new Parser(in);
    parser.prog(env);
  }
}
