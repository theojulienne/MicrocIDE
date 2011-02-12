package plugins.documents.sourceDocument.syntax.c;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

public class CAutoIndentStrategy implements IAutoEditStrategy {
	public void customizeDocumentCommand( IDocument document, DocumentCommand command ) {
		autoInsertPair( document, command, "{", "}" );
		autoInsertPair( document, command, "(", ")" );
		autoInsertPair( document, command, "[", "]" );
		autoInsertPair( document, command, "\"", "\"" );
		autoInsertPair( document, command, "'", "'" );
		
		if ( command.text.equals( "\n" ) ) {
			if ( !attemptBlockSplit( document, command ) ) {
				// just adding a new line, borrow previous line indentation
				String indent = getCurrentLineIndent( document, command );
				
				if ( indent != null ) {
					command.text = "\n" + indent;
					command.caretOffset = command.offset + command.text.length( );
					command.shiftsCaret = false;
				}
			}
		}
    }
	
	private String getCurrentLineIndent( IDocument document, DocumentCommand command ) {
		int line;
		String indent = null;
		try {
			line = document.getLineOfOffset( command.offset );
			indent = getIndentOfLine( document, line );
		} catch (BadLocationException e) {
			// ignore, leave as null
		}
		
		return indent;
	}
	
	// attempts autoindent on a newline between { and }
	private boolean attemptBlockSplit( IDocument document, DocumentCommand command ) {
		char preChar;
		char postChar;
		
		try {
			preChar = document.getChar(command.offset-1);
			postChar = document.getChar(command.offset);
		} catch (BadLocationException e) {
			return false; // bail out, didn't work
		}
		
		if ( preChar == '{' ) {
			// going after a bracket means we want to be indented 1 more
			String indent = getCurrentLineIndent( document, command );
			if ( indent == null ) {
				return false;
			}

			String beforeCaret = "\n" + indent + "\t";
			String afterCaret = "";
			
			// if we've got a } after us, we also want to indent that properly
			if ( postChar == '}' ) {
				afterCaret = "\n" + indent;
			}

			command.text = beforeCaret + afterCaret;
			command.caretOffset = command.offset + beforeCaret.length( );
			command.shiftsCaret = false;
			
			return true;
		}
		
		return false;
	}
	
	private void autoInsertPair( IDocument document, DocumentCommand command, String start, String end ) {
		// check if we already have the ending string so we can skip over it
		try {
			String nextText = document.get( command.offset, end.length( ) );
			//System.out.println( "command text: " + command.text );
			//System.out.println( "next text: " + nextText );
			if ( command.text.equals(end) && nextText.equals(end) ) {
				command.text = "";
				command.caretOffset = command.offset + end.length( );
				command.shiftsCaret = false;
				return; // bail!
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// otherwise see if we can autocomplete the ending tag
		if ( command.text.equals( start ) ) {
			command.text = start + end;
			command.caretOffset = command.offset + start.length( );
			command.shiftsCaret = false;
		}
	}
	
	public static  int findEndOfWhiteSpace( IDocument document, int offset, int end ) 
		throws BadLocationException {

		while ( offset < end ) {
			char c= document.getChar(offset);
			if ( c != ' ' & c !=  '\t' ) {
				return offset;
			}
			offset++;
		}
		return end;
   }

    public static String getIndentOfLine( IDocument document, int line )
	    throws BadLocationException {
    	
		if ( line > -1 ) {
		    int start = document.getLineOffset(line);
		    int end = start + document.getLineLength(line) - 1;
		    int whiteend = findEndOfWhiteSpace(document, start, end);
		    return document.get(start, whiteend - start);
		} else {
		    return ";";
		}
	}
}
