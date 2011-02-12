package plugins.documents.sourceDocument.syntax.c;

import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import plugins.documents.sourceDocument.SourceDocument;

public class CCommentBlockScanner extends RuleBasedScanner
{
    public CCommentBlockScanner( SourceDocument document ) {
    	
    	Token defaultToken = CScanner.getTokenForKey( document, "Comment" );
    	
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