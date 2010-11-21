package syntax.c;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class CPartitionScanner extends RuleBasedPartitionScanner {
	public final static String C_MULTILINE_COMMENT = "__c_multiline_comment";
	public final static String C_DOCUMENTATION_COMMENT = "__c_doc_comment";
	
	public static final String[] PARTITION_TYPES = { C_MULTILINE_COMMENT, C_DOCUMENTATION_COMMENT };
	
	public CPartitionScanner( ) {
		
		Token multiCommentTok = new Token( C_MULTILINE_COMMENT );
		Token docCommentTok   = new Token( C_DOCUMENTATION_COMMENT );
		
		IPredicateRule[] rules = new IPredicateRule[2];
		rules[0] = new MultiLineRule( "/**", "*/", docCommentTok, (char)ICharacterScanner.EOF, true );
		rules[1] = new MultiLineRule( "/*", "*/", multiCommentTok, (char)ICharacterScanner.EOF, true );
		
		setPredicateRules( rules );
	}
}
