package plugins.documents.sourceDocument.syntax.c;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import plugins.documents.sourceDocument.SourceDocument;
import plugins.documents.sourceDocument.syntax.CustomSingleTokenRule;
import plugins.documents.sourceDocument.syntax.CustomWordRule;


public class CDirectiveScanner extends RuleBasedScanner
{
    public CDirectiveScanner( SourceDocument document ) {
    	
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
	    
	    
	    Token directiveTok = CScanner.getTokenForKey( document, "Directive" );
		Token capTok       = CScanner.getTokenForKey( document, "All Caps" );
		Token stringTok    = CScanner.getTokenForKey( document, "Directive String" );
		Token anglesTok    = CScanner.getTokenForKey( document, "Directive Angle Bracketed" );
		Token numTok       = CScanner.getTokenForKey( document, "Number" );
		
		Token directiveDefaultTok = CScanner.getTokenForKey( document, "Directive Text" );
		
		setDefaultReturnToken( directiveDefaultTok );
		
	    for ( String directive : directives ) {
	    	directiveRule.addWord( directive, directiveTok );
	    }
	    
	    CustomSingleTokenRule numRule = new CustomSingleTokenRule( CScanner.numDetector, numTok );
	    
	    CustomSingleTokenRule capsRule = new CustomSingleTokenRule( CScanner.capsDetector, capTok );
	    
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