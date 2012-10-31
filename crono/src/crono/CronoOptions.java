package crono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Options and utilities for debugging and examining parts of the Crono
 * language.
 */
class Option {
  public char shortopt;
  public String longopt;
  public boolean arg;
  public Option(char shortopt) {
    this(shortopt, null, false);
  }
  public Option(char shortopt, boolean arg) {
    this(shortopt, null, arg);
  }
  public Option(char shortopt, String longopt) {
    this(shortopt, longopt, false);
  }
  public Option(char shortopt, String longopt, boolean arg) {
    this.shortopt = shortopt;
    this.longopt = longopt;
    this.arg = arg;
  }
}
class OptionParser {
  public String optopt; /*< Used for arguments to options */
  public String optchar; /*< Used for the error case */

  private String[] args;
  private int current, subarg;

  public OptionParser(String[] args) {
    this.args = args;
    this.current = 0;
    this.subarg = -1;
    this.optopt = null;
  }
    
  public int nextarg(Option[] options) {
    if(current >= args.length) {
      return -1;
    }
    this.optopt = null;
    this.optchar = null;

    int arglen = args[current].length();
    if(subarg == -1) {
      if("--".equals(args[current])) {
	optchar = "--";
	current++; /*< Move past */
	return -1; /*< Signal the end of the option stream */
      }
      if(arglen >= 1 && args[current].charAt(0) == '-') {
	if(arglen >= 2 && args[current].charAt(1) == '-') {
	  /* Search option list for long arg */
	  String optstr = args[current].substring(2);
	  for(Option opt : options) {
	    if(optstr.equals(opt.longopt)) {
	      current++; /*< Move to the next position */
	      if(opt.arg) {
		if(current < args.length) {
		  optopt = args[current];
		  current++;
		}
	      }
	      return ((int)(opt.shortopt));
	    }
	  }
	  
	  /* Couldn't find the long option, return '?' */
	  optchar = optstr;
	  current++;
	  return ((int)'?');
	}else {
	  subarg = 1;
	}
      }else {
	return -1;
      }
    }
    
    /* Parse short options here */
    char optionchar = args[current].charAt(subarg);
    for(Option opt : options) {
      if(opt.shortopt == optionchar) {
	if(opt.arg) {
	  if(subarg == arglen - 1) {
	    /* End of the option stream, take next whole string */
	    current++; /*< Move to next position */
	    if(current < args.length) {
	      optopt = args[current]; /*< Store argument */
	    }
	  }else {
	    optopt = args[current].substring(subarg + 1);
	  }
	  subarg = -1; /*< Reset shortopt parsing to default */
	  current++; /*< Move to next option */
	}else if(subarg == arglen - 1) {
	  subarg = -1;
	  current++;
	}else {
	  subarg++;
	}
	
	return ((int)(opt.shortopt));
      }
    }
    
    /* Didn't find the option, return '?' */
    optchar = "" + optionchar;
    current++;
    return ((int)'?');
  }
  public int optind() {
    return current;
  }
}

public class CronoOptions {
  private static String HELP_STRING =
      "usage: crono [-adDeEiIlLmMpqstT] files ...";
  private static String ILLEGAL_OPTION =
      "crono: illegal option -- %s\n";
    
  // Switch controlling whether dprint prints or not.
  public static boolean DPRINT_ENABLE = true;

  // Whether or not to use indention when dprinting.
  public static boolean DPRINT_INDENT = true;

  // Whether or not to show atoms being evaluated.
  public static boolean DPRINT_SHOW_ATOM_EVAL = true;

  // Level of indention to use when using dprint.
  public static int DPRINT_I = 0;

  // Whether or not to print the environment when executing.
  public static boolean ENVIRONMENT_SHOW = true;

  // Whether or not to show CronoFunctions when printing the environment.
  public static boolean ENVIRONMENT_SHOW_BUILTIN = false;

  // Controls whether we use static or dynamic scoping.
  public static boolean ENVIRONMENT_DYNAMIC = false;

  // Whether or not to print types in the environment.
  public static boolean ENVIRONMENT_SHOW_TYPES = true;

  // Whether or not to use multiple lines when printing the environment.
  public static boolean ENVIRONMENT_MULTILINE = true;

  // Whether or not to show closures when printing lambdas.
  public static boolean LAMBDA_SHOW_CLOSURE = true;

  // Whether or not to show parser debug output.
  public static boolean PARSER_DPRINT = false;
  
  // List of files to run on the interpreter
  public static List<String> FILES = new LinkedList<String>();
  
  public static boolean parseargs(String[] args) {
    OptionParser optparse = new OptionParser(args);
    Option[] opts = {
      new Option('a', "debug-atom"),
      new Option('d', "dynamic"),
      new Option('D', "debug"),
      new Option('e', "show-environment"),
      new Option('E', "no-show-environment"),
      new Option('h', "help"),
      new Option('i', "debug-indent"),
      new Option('I', "no-debug-indent"),
      new Option('l', "lambda-show-closure"),
      new Option('L', "no-lambda-show-closure"),
      new Option('m', "env-multiline"),
      new Option('M', "no-env-multilin"),
      new Option('p', "parser-debug"),
      new Option('q', "quiet"),
      new Option('s', "static"),
      new Option('t', "env-show-types"),
      new Option('T', "debug-indent-level", true),
    };
    int ch;
    
    ch = optparse.nextarg(opts);
    while(ch != -1) {
	switch(ch) {
	case 'a':
	    DPRINT_SHOW_ATOM_EVAL = true;
	    break;
	case 'd':
	    ENVIRONMENT_DYNAMIC = true;
	    break;
	case 'D':
	    DPRINT_ENABLE = true;
	    break;
	case 'e':
	    ENVIRONMENT_SHOW = true;
	    break;
	case 'E':
	    ENVIRONMENT_SHOW = false;
	    break;
	case 'h':
	    System.err.println(HELP_STRING);
	    return false;
	case 'i':
	    DPRINT_INDENT = true;
	    break;
	case 'I':
	    DPRINT_INDENT = false;
	    break;
	case 'l':
	    LAMBDA_SHOW_CLOSURE = true;
	    break;
	case 'L':
	    LAMBDA_SHOW_CLOSURE = false;
	    break;
	case 'm':
	    ENVIRONMENT_MULTILINE = true;
	    break;
	case 'M':
	    ENVIRONMENT_MULTILINE = false;
	    break;
	case 'p':
	    PARSER_DPRINT = true;
	    break;
	case 'q':
	    DPRINT_ENABLE = false;
	    break;
	case 's':
	    ENVIRONMENT_DYNAMIC = false;
	    break;
	case 't':
	    ENVIRONMENT_SHOW_TYPES = true;
	    break;
	case 'T':
	    ENVIRONMENT_SHOW_TYPES = false;
	    break;
	case '?':
	default:
	    System.err.printf(ILLEGAL_OPTION, optparse.optchar);
	    System.err.println(HELP_STRING);
	    return false;
	}
    }
    
    /* Use the remaining arguments as files */
    for(int i = optparse.optind(); i < args.length; ++i) {
      FILES.add(args[i]);
    }
    
    return true;
  }
  
  public static void dprint(String msg, Object... args) {
    if (DPRINT_ENABLE) {
      String formatted = String.format(msg, args);
      /* Use a regex to keep the delimiter when splitting; by doing so it is
       * no longer necessary to have seperate logic for newlines.
       */
      String[] lines = formatted.split("(?<=\n)");
      StringBuilder ident = new StringBuilder((DPRINT_I + 1) * 2 + 1);
      
      /* Build initial identation */
      for(int i = 0; DPRINT_INDENT && i < DPRINT_I; i++) {
	  ident.append("  ");
      }
      
      /* First line, normal indentation */
      System.out.printf("%s%s", ident.toString(), lines[0]);
      
      /* If we are indenting, we should indent subsequent lines an extra two
       * spaces. */
      if(DPRINT_INDENT) {
	  ident.append("  ");
      }
      
      /* Got rid of Arrays.copyOfRange; using said function actually creates
       * a new array, which is overkill when you can just use a traditional for
       * loop... */
      for(int i = 1; i < lines.length; ++i) {
	  System.out.printf("%s%s", ident.toString(), lines[i]);
      }
    }
  }

  public static void err(String msg, Object... args) {
    throw new RuntimeException(String.format(msg, args));
  }
}
