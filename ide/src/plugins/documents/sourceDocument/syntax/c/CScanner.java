package plugins.documents.sourceDocument.syntax.c;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;


import plugins.documents.sourceDocument.SourceDocument;
import plugins.documents.sourceDocument.syntax.CustomSingleTokenRule;
import plugins.documents.sourceDocument.syntax.CustomWordRule;

public class CScanner extends BufferedRuleBasedScanner {
	
	public static IWordDetector keywordDetector = new IWordDetector() {
       public boolean isWordStart(char c) { 
    	   return (Character.isLetter( c ) || c == '_'); 
       }
       public boolean isWordPart(char c) {   
    	   return (Character.isLetter( c ) || c == '_' || Character.isDigit( c )); 
       }
    };
	
	public static IWordDetector opDetector = new IWordDetector() {
		public boolean isWordStart(char c) { 
			return (isSymbol( c )); 
		}
		public boolean isWordPart(char c) {   
			return (isSymbol( c )); 
		}			
	};
	
	public static IWordDetector capsDetector = new IWordDetector() {
		public boolean isWordPart(char c) {
			return (Character.isUpperCase(c) || c == '_' || Character.isDigit(c));
		}
		public boolean isWordStart(char c) {
			return (Character.isUpperCase(c));
		}
    };
    
	public static IWordDetector numDetector = new IWordDetector() {
		public boolean isWordPart(char c) {
			boolean isHexChar = false;
			for ( char hexChar : "abcdefx".toCharArray() ) {
				if ( hexChar == c ) {
					isHexChar = true;
					break;
				}
			}
			
			return (Character.isDigit( c ) || c == '.' || isHexChar);
		}
		public boolean isWordStart(char c) {
			return (Character.isDigit( c ));
		}
	};
	
	public static IWordDetector groupDetector = new IWordDetector() {
		public boolean isWordPart(char c) {
			return (c == '}' || c == '{');
		}
		public boolean isWordStart(char c) {
			return (c == '}' || c == '{');
		}	    	
    };
	
	public static IWordDetector identDetector = new IWordDetector() {
		public boolean isWordStart(char c) { 
			return (Character.isLetter( c ) || c == '_'); 
		}
		public boolean isWordPart(char c) {   
			return (Character.isLetter( c ) || c == '_' || Character.isDigit( c )); 
		}
    };
	
	
	public CScanner( SourceDocument document ) {

		CustomWordRule wordRule = new CustomWordRule( keywordDetector );
		CustomWordRule operatorRule = new CustomWordRule( opDetector );
		
		Token other        = getTokenForKey( document, "Default Text" );
		Token keywordTok   = getTokenForKey( document, "Keyword" );
		Token primTok      = getTokenForKey( document, "Primitive Type" );
		Token capTok       = getTokenForKey( document, "All Caps" );
		Token tymodTok     = getTokenForKey( document, "Type Qualifier" );
	    Token commentTok   = getTokenForKey( document, "Comment" );
	    Token charTok      = getTokenForKey( document, "Character Literal" );
	    Token numTok       = getTokenForKey( document, "Number" );
	    Token groupingTok  = getTokenForKey( document, "Grouping" );
	    Token opTok        = getTokenForKey( document, "Operator" );
	    Token identTok     = getTokenForKey( document, "Identifier" );
	    
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
	    
	    for ( String op : operators ) {
	    	operatorRule.addWord( op, opTok );
	    }
	    
	    CustomSingleTokenRule capsRule = new CustomSingleTokenRule( capsDetector, capTok );
	    
	    CustomSingleTokenRule groupingRule = new CustomSingleTokenRule( groupDetector, groupingTok );
	    
		CustomSingleTokenRule numRule = new CustomSingleTokenRule( numDetector, numTok );
		
		CustomSingleTokenRule identifierRule = new CustomSingleTokenRule( identDetector, identTok );
	    
	    IRule[] rules = new IRule[] {
		    	new EndOfLineRule( "//", commentTok ),
		    	new SingleLineRule( "'", "'", charTok, '\\' ),
		    	wordRule,
		    	numRule,
		    	capsRule,
		    	operatorRule,
		    	groupingRule,
		    	identifierRule,
		    	new WhitespaceRule( new IWhitespaceDetector() {
		    		public boolean isWhitespace(char c) {
		    			return Character.isWhitespace(c);
		    		}
		    	} )
		    };
	    
	    setRules( rules );
	    setDefaultReturnToken( other );
	}
	
	public static Token getTokenForKey( SourceDocument document, String key ) {
		
		
		Color colour   = document.getSyntaxColor( key );
		boolean isBold = document.isSyntaxBold( key ); 
		
		Token token;
		if ( isBold ) {
			token = new Token( new TextAttribute( colour, null, SWT.BOLD ) );
		} else {
			token = new Token( new TextAttribute( colour ) );
		}
		
		return token;
	}
	
	private static boolean isSymbol( char c ) {
		String symbols = "~`!@#$%^&*_-+=|\\\"':;?/>.<,";
		for ( char symb : symbols.toCharArray() ) {
			if ( symb == c ) {
				return true;
			}
		}
		
		return false;
	}
}
