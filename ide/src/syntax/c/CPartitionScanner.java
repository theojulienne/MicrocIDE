package syntax.c;

import java.util.ArrayList;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class CPartitionScanner extends RuleBasedPartitionScanner {
	public final static String C_COMPILER_DIRECTIVE = "__c_compiler_directive"; 
	public final static String C_MULTILINE_COMMENT = "__c_multiline_comment";
	public final static String C_DOCUMENTATION_COMMENT = "__c_doc_comment";
	
	public static final String[] PARTITION_TYPES = { C_MULTILINE_COMMENT, C_DOCUMENTATION_COMMENT, C_COMPILER_DIRECTIVE };
	
	public CPartitionScanner( ) {
		
		Token multiCommentTok = new Token( C_MULTILINE_COMMENT );
		Token docCommentTok   = new Token( C_DOCUMENTATION_COMMENT );
		Token directiveTok    = new Token( C_COMPILER_DIRECTIVE );
		
	    String[] directives = new String[] {
		    	"#define",
		    	"#include",
		    	"#ifdef",
		    	"#ifndef",
		    	"#else",
		    	"#undef",
		    	"#if",
		    	"#endif",
		    	"#line",
		    	"#error",
		    	"#pragma"
		    };
	    
		ArrayList<IPredicateRule> rules = new ArrayList<IPredicateRule>( );
		rules.add( new MultiLineRule( "/**", "*/", docCommentTok, (char)ICharacterScanner.EOF, true )  );
		rules.add( new MultiLineRule( "/*", "*/", multiCommentTok, (char)ICharacterScanner.EOF, true ) );
		
	    for ( String directive : directives ) {
	    	rules.add( new EndOfLineRule( directive, directiveTok ) );
	    }
		
		setPredicateRules( rules.toArray( new IPredicateRule[rules.size()] ) );
	}
}
