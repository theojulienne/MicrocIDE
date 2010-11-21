package syntax.c;

import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class CDocCommentScanner extends RuleBasedScanner
{
    public CDocCommentScanner( ) {
    	
    	Token defaultToken = CScanner.getTokenForPreference( "doccomment" );
    	
    	setDefaultReturnToken( defaultToken );
    	
    	/*
        TextAttribute textAttribute = new TextAttribute( ... );
        IToken string = new Token(textAttribute);
    
        IRule[] rules = new IRule[3];
    
        // Add rule for double quotes
        rules[0] = new SingleLineRule("\"", "\"", string, '\\');
        // Add a rule for single quotes
        rules[1] = new SingleLineRule("'", "'", string, '\\');
        // Add generic whitespace rule.
        rules[2] = new WhitespaceRule(new XMLWhitespaceDetector());
    
        setRules(rules);
        */
    }
}