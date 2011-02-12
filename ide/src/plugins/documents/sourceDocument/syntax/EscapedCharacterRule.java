package plugins.documents.sourceDocument.syntax;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class EscapedCharacterRule implements IRule {
	/** Internal setting for the uninitialized column constraint */
	protected static final int UNDEFINED= -1;
	
	/** The default token to be returned on success and if nothing else has been specified. */
	protected IToken fDefaultToken;
	/** The column constraint */
	protected int fColumn= UNDEFINED;
	/** Buffer used for pattern detection */
	private StringBuffer fBuffer= new StringBuffer();
	
	private char escapeChar;
	/**
	 * Creates a rule which, with the help of an word detector, will return the token
	 * associated with the detected word. If no token has been associated, the
	 * specified default token will be returned.
	 *
	 * @param detector the word detector to be used by this rule, may not be <code>null</code>
	 * @param defaultToken the default token to be returned on success 
	 *		if nothing else is specified, may not be <code>null</code>
	 *
	 * @see #addWord
	 */
	public EscapedCharacterRule( char escapeChar, IToken token) {
		
		this.escapeChar = escapeChar;
		Assert.isNotNull(token);
		
		fDefaultToken= token;
	}
	
	/**
	 * Sets a column constraint for this rule. If set, the rule's token
	 * will only be returned if the pattern is detected starting at the 
	 * specified column. If the column is smaller then 0, the column
	 * constraint is considered removed.
	 *
	 * @param column the column in which the pattern starts
	 */
	public void setColumnConstraint(int column) {
		if (column < 0)
			column= UNDEFINED;
		fColumn= column;
	}
	
	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {

		int c = scanner.read();
		if ( c == escapeChar ) {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
				
				fBuffer.setLength(0);
				fBuffer.append((char) c);
				fBuffer.append( scanner.read( ) );
				
				return fDefaultToken;
			}
		}
		
		scanner.unread();
		return Token.UNDEFINED;
	}
	
	/**
	 * Returns the characters in the buffer to the scanner.
	 *
	 * @param scanner the scanner to be used
	 */
	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i= fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}
}
