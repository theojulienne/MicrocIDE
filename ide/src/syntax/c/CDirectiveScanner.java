package syntax.c;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import syntax.CustomSingleTokenRule;
import syntax.CustomWordRule;

public class CDirectiveScanner extends RuleBasedScanner
{
    public CDirectiveScanner( ) {
    	
		CustomWordRule directiveRule = new CustomWordRule( new IWordDetector() {	       
			public boolean isWordStart(char c) { 
				return (c == '#'); 
			}
			public boolean isWordPart(char c) {   
				return (Character.isLetter(c)); 
			}
		} );
		
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
	    
	    
	    Token directiveTok = CScanner.getTokenForPreference( "directive" );
		Token capTok       = CScanner.getTokenForPreference( "allcaps" );
		Token stringTok    = CScanner.getTokenForPreference( "directivestring" );
		Token anglesTok    = CScanner.getTokenForPreference( "directiveangles" );
		Token numTok       = CScanner.getTokenForPreference( "number" );
		
		Token directiveDefaultTok = CScanner.getTokenForPreference( "directivedefault" );
		
		setDefaultReturnToken( directiveDefaultTok );
		
	    for ( String directive : directives ) {
	    	directiveRule.addWord( directive, directiveTok );
	    }
	    
	    CustomSingleTokenRule numRule = new CustomSingleTokenRule( new IWordDetector() {
			public boolean isWordPart(char c) {
				return (Character.isDigit( c ) || c == '.');
			}
			public boolean isWordStart(char c) {
				return (Character.isDigit( c ));
			}
		}, numTok );
	    
	    CustomSingleTokenRule capsRule = new CustomSingleTokenRule( new IWordDetector() {
			public boolean isWordPart(char c) {
				return (Character.isUpperCase(c) || c == '_');
			}
			public boolean isWordStart(char c) {
				return (Character.isUpperCase(c));
			}
	    }, capTok );
	    
	    IRule[] rules = new IRule[] {
	    		directiveRule,
	    		capsRule,
	    		numRule,
	    		new SingleLineRule( "<", ">", anglesTok ),
	    		new SingleLineRule( "\"", "\"", stringTok, '\\' )
	    };
	    
	    setRules( rules );
    }
}