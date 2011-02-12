package plugins.documents.sourceDocument.syntax.c;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import plugins.documents.sourceDocument.SourceDocument;
import plugins.documents.sourceDocument.syntax.EscapedCharacterRule;

// TODO: make C strings parsed to highlight formatting and escape chars
public class CStringScanner extends RuleBasedScanner {
    public CStringScanner( SourceDocument document ) {
  	
		String[] formatting = new String[] {
			"s",
			"f",
			"lf",
			"ld",
			"p",
			"x",
			"c",
			"d"
		};
    	
    	Token defaultToken = CScanner.getTokenForKey( document, "String Literal" );
    	
    	setDefaultReturnToken( defaultToken );
    	
    	
    	Token escapeToken = CScanner.getTokenForKey( document, "String Escaped" );
    	Token formatToken = CScanner.getTokenForKey( document, "String Formatting" );
    	
    	int numRules = 1;
        IRule[] rules = new IRule[numRules];
        rules[0] = new EscapedCharacterRule( '\\', escapeToken );
        //rules[1] = new StringFormattingRule( "%", "s", formatToken );
       
        
        setRules(rules);
    }
}