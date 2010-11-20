package syntax.c;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.graphics.Color;

import syntax.CustomSingleTokenRule;
import syntax.CustomWordRule;

import app.Application;

public class CScanner extends BufferedRuleBasedScanner {
	public CScanner( ) {
		CustomWordRule wordRule = new CustomWordRule( new IWordDetector() {
	       public boolean isWordStart(char c) { 
	    	   return (Character.isLetter( c ) || c == '_'); 
	       }
	       public boolean isWordPart(char c) {   
	    	   return (Character.isLetterOrDigit( c ) || c == '_'); 
	       }
	    } );
		
		CustomWordRule directiveRule = new CustomWordRule( new IWordDetector() {	       
			public boolean isWordStart(char c) { 
				return (c == '#'); 
			}
			public boolean isWordPart(char c) {   
				return (Character.isLetter(c)); 
			}
		} );
		
		CustomWordRule operatorRule = new CustomWordRule( new IWordDetector() {
			public boolean isWordStart(char c) { 
				return (isSymbol( c )); 
			}
			public boolean isWordPart(char c) {   
				return (isSymbol( c )); 
			}			
		} );
		
		Token other        = getTokenForPreference( "default" );
		Token keywordTok   = getTokenForPreference( "keyword" );
		Token primTok      = getTokenForPreference( "primtype" );
		Token capTok       = getTokenForPreference( "allcaps" );
		Token directiveTok = getTokenForPreference( "directive" );
		Token tymodTok     = getTokenForPreference( "typemod" );
	    Token commentTok   = getTokenForPreference( "comment" );
	    Token stringTok    = getTokenForPreference( "string" );
	    Token charTok      = getTokenForPreference( "char" );
	    Token numTok       = getTokenForPreference( "number" );
	    Token groupingTok  = getTokenForPreference( "grouping" );
	    Token opTok        = getTokenForPreference( "operator" );
	    
	    Token multiCommentTok = getTokenForPreference( "comment" );
	    
	    //add tokens for each reserved word
	    String[] keywords = new String[]{
	    	"auto",
	    	"break",
	    	"case",
	    	"continue",
	    	"default",
	    	"do",
	    	"else",
	    	"enum",
	    	"extern",
	    	"for",
	    	"goto",
	    	"if",
	    	"return",
	    	"sizeof",
	    	"struct",
	    	"switch",
	    	"typedef",
	    	"union",
	    	"while"
	    };
	    
	    String[] tyMods = new String[] {
	    	"static",
	    	"const",
	    	"voltatile",
	    	"unsigned",
	    	"signed",
	    	"register",
	    };
	    
	    String[] primTypes = new String[] {
	    		
	    	"void",
	    	"double",
	    	"float",
	    	"char",
	    	"int",
	    	"long",
	    	"short",
	    	
	    	"int8_t",
	    	"uint8_t",
	    	"int16_t",
	    	"uint16_t",
	    	"int32_t",
	    	"uint32_t",
	    	"int64_t",
	    	"uint64_t"
	    };
	    
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
	    
	    String[] operators = new String[] {
	    	"+",
	    	"-",
	    	"*",
	    	"/",
	    	"%",
	    	
	    	"!",
	    	"&&",
	    	"||",
	    	
	    	">",
	    	"<",
	    	">=",
	    	"<=",
	    	"==",
	    	"!=",

	    	"++",
	    	"--",
	    	
	    	"=",
	    	"+=",
	    	"-=",
	    	"*=",
	    	"/=",
	    	"%=",

	    	"|",
	    	"&",
	    	"^",
	    	"~",
	    	">>",
	    	"<<",
	    	
	    	"|=",
	    	"&=",
	    	"^=",
	    	"~=",
	    	">>=",
	    	"<<=",
	    	
	    	"?",
	    	":",
	    	
	    };
	    
	    for ( String keyword : keywords ) {
	    	wordRule.addWord( keyword, keywordTok );
	    }
	    
	    for ( String tyMod : tyMods ) {
	    	wordRule.addWord( tyMod, tymodTok );
	    }
	    
	    for ( String primType : primTypes ) {
	    	wordRule.addWord( primType, primTok );
	    }
	    
	    for ( String directive : directives ) {
	    	directiveRule.addWord( directive, directiveTok );
	    }
	    
	    for ( String op : operators ) {
	    	operatorRule.addWord( op, opTok );
	    }
	    
	    CustomSingleTokenRule capsRule = new CustomSingleTokenRule( new IWordDetector() {
			public boolean isWordPart(char c) {
				return (Character.isUpperCase(c) || c == '_');
			}
			public boolean isWordStart(char c) {
				return (Character.isUpperCase(c));
			}
	    }, capTok );
	    
	    CustomSingleTokenRule groupingRule = new CustomSingleTokenRule( new IWordDetector() {
			public boolean isWordPart(char c) {
				return (c == '}' || c == '{');
			}
			public boolean isWordStart(char c) {
				return (c == '}' || c == '{');
			}	    	
	    }, groupingTok );
	    
		CustomSingleTokenRule numRule = new CustomSingleTokenRule( new IWordDetector() {
			public boolean isWordPart(char c) {
				return (Character.isDigit( c ) || c == '.');
			}
			public boolean isWordStart(char c) {
				return (Character.isDigit( c ));
			}
		}, numTok );
	    
	    IRule[] rules = new IRule[] {
		    	new EndOfLineRule( "//", commentTok ),
		    	new MultiLineRule( "/*", "*/", multiCommentTok, (char)ICharacterScanner.EOF, true ),
		    	new SingleLineRule( "\"", "\"", stringTok, '\\' ),
		    	new SingleLineRule( "'", "'", charTok, '\\' ),
		    	numRule,
		    	capsRule,
		    	wordRule,
		    	directiveRule,
		    	operatorRule,
		    	groupingRule,
		    	new WhitespaceRule( new IWhitespaceDetector() {
		    		public boolean isWhitespace(char c) {
		    			return Character.isWhitespace(c);
		    		}
		    	} )
		    };
	    
	    setRules( rules );
	    setDefaultReturnToken( other );
	}
	
	Token getTokenForPreference( String preference ) {

		Application app = Application.getInstance( );
		
		IPreferenceStore preferences = app.getPreferenceStore();
		
		Color colour = app.getColorPreference( "syntax." + preference );
		
		Token token;
		if ( preferences.contains( "syntax." + preference + ".font" ) ) {
			token = new Token( new TextAttribute( colour, null, 
					app.getPreferenceStore().getInt( "syntax." + preference + ".font" ) ) );
		} else {
			token = new Token( new TextAttribute( colour ) );
		}
		
		return token;
	}
	
	private boolean isSymbol( char c ) {
		String symbols = "~`!@#$%^&*_-+=|\\\"':;?/>.<,";
		for ( char symb : symbols.toCharArray() ) {
			if ( symb == c ) {
				return true;
			}
		}
		
		return false;
	}
}
