package app.project.document;

import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 * This class displays a find/replace dialog
 */
public class FindReplaceDialog extends Dialog {
  // The adapter that does the finding/replacing
  private FindReplaceDocumentAdapter frda;

  // The associated viewer
  private ITextViewer viewer;
  // The find and replace buttons
  private Button doNext;
  private Button doPrev;  
  private Button doReplace;
  private Button doReplaceFind;
  private Button doReplaceAll;

  public static int FIND_NEXT = 0;
  public static int FIND_PREV = 1;
  public static int REPLACE = 2;
  public static int REPLACE_FIND_NEXT = 3;
  public static int REPLACE_ALL = 4; // TODO
  
  private Shell shell;

  private Text findText;
  private Text replaceText;
  private Button regexp;
  private Button match;
  private Button wrap;
  
  /**
   * FindReplaceDialog constructor
   *
   * @param shell the parent shell
   * @param document the associated document
   * @param viewer the associated viewer
   */
  public FindReplaceDialog(Shell shell, IDocument document, ITextViewer viewer) {
    super( shell, SWT.DIALOG_TRIM | SWT.MODELESS );
    frda = new FindReplaceDocumentAdapter( document );
    this.viewer = viewer;
  }

  public void setVisible( boolean isVisible ) {
	  if ( !shell.isDisposed() ) {
		  shell.setVisible( isVisible );
	  }
  }
  
  public boolean isDisposed( ) {
	  return shell.isDisposed( );
  }
  
  public void close( ) {
	  if ( !shell.isDisposed() ) {
		  this.shell.close( );
	  }
  }
  
  /**
   * Opens the dialog box
   */
  public void open() {
    Shell shell = new Shell( getParent( ), getStyle( ) );
    shell.setText( "Find/Replace" );
    createContents( shell );
    shell.pack( );
    shell.open( );
    
    this.shell = shell;
    
    shell.addShellListener( new ShellListener() {
		public void shellActivated(ShellEvent e) {
		}
		public void shellClosed(ShellEvent e) {
			((Shell)e.widget).setVisible( false );
			e.doit = false;
		}
		public void shellDeactivated(ShellEvent e) {
		}
		public void shellDeiconified(ShellEvent e) {
		}
		public void shellIconified(ShellEvent e) {
		}
    } );
    
    Display display = getParent( ).getDisplay( );
    
    while ( !shell.isDisposed( ) ) {
      if ( !display.readAndDispatch( ) ) {
        display.sleep( );
      }
    }
  }

  /**
   * Performs a find/replace
   *
   * @param find the find string
   * @param replace the replace text
   * @param forward whether to search forward
   * @param matchCase whether to match case
   * @param wholeWord whether to search on whole word
   * @param regexp whether find string is a regular expression
   */
  protected boolean doFind( int code, String find,
		  String replace, boolean matchCase, boolean wrap,
		  boolean regexp ) {
      // Get the current offset
      int offset = viewer.getTextWidget( ).getCaretOffset( );
      if ( code == FIND_NEXT ) {
      	Point range = viewer.getTextWidget( ).getSelectionRange( );
      	offset = range.x  + range.y;
      } else if ( code == FIND_PREV ) {
      	offset = viewer.getTextWidget( ).getSelectionRange( ).x;
      }
      
      return doFind( code, find, replace, matchCase, wrap, regexp, offset );
  }
  
  protected boolean doFind( int code, String find,
      String replace, boolean matchCase, boolean wrap,
      boolean regexp, int offset ) {
	  
	  boolean succeeded = false;
	  
	  IRegion region = null;
      
      try {
        // Make sure we're in the document
        if ( offset >= frda.length() ) {
        	offset = frda.length() - 1;
        }
    	
        // Perform the find
        if ( code == FIND_NEXT || code == FIND_PREV ) {
        	
        	boolean forward = (code == FIND_NEXT );
        	
        	// TODO: optional: add in whole word searching
        	region = frda.find( offset, find, forward, matchCase, false, regexp );
        	
            // Update the viewer with found selection
            if (region != null) {
              viewer.setSelectedRange( region.getOffset( ), region.getLength( ) );
            }
        } else if ( code == REPLACE || code == REPLACE_FIND_NEXT ) {
      	  	// Perform the replace
        	frda.replace( replace, regexp );
        }

        // If find succeeded, flip to FIND_NEXT and enable Replace buttons
        // Otherwise, reset to FIND_FIRST and disable Replace buttons
        // We know find succeeded if region is not null AND the operation
        // wasn't REPLACE (REPLACE finds nothing new, but still returns
        // a region).
        succeeded = region != null && code != REPLACE;
        enableReplaceButtons( succeeded );
        
        // wrap around
        if ( wrap && !succeeded ) {
        	if ( code == FIND_NEXT ) {
        		// find next from beginning, this one shouldn't wrap (prevent infinite searching)
        		succeeded = doFind( FIND_NEXT, find, replace, matchCase, false, regexp, 0 );
        	} else if ( code == FIND_PREV ){
        		// find prev from end
        		succeeded = doFind( FIND_PREV, find, replace, matchCase, false, regexp, viewer.getTextWidget( ).getText().length()-1 );
        	}
        }
        
        // do the next part of the replace-find-next or replace-all
    	if ( code == REPLACE_FIND_NEXT ) {
    		succeeded = doFind( FIND_NEXT, find, replace, matchCase, wrap, regexp );
    	}
        
      } catch (BadLocationException e) {
        // Ignore
      } catch (PatternSyntaxException e) {
        // Show the error to the user
        showError(e.getMessage());
      }
      
      return succeeded;
  }
  
  private void replaceAll( ) {
	  
	  if ( findText.getText().length() == 0 ) {
		  return;
	  }
	  
	  doFind( FIND_NEXT, findText.getText(), 
			  replaceText.getText(), !match.getSelection(), 
			  wrap.getSelection(), regexp.getSelection(), 0 );
	  
	  boolean isFinding = true;
	  while ( isFinding ) {
		  isFinding = doFind( REPLACE_FIND_NEXT, findText.getText(), 
				  replaceText.getText(), !match.getSelection(), 
				  wrap.getSelection(), regexp.getSelection(), 0 );
	  }
  }
  
  public void findAction( int code ) {
	  
	  if ( code == REPLACE_ALL ) {
		  replaceAll( );
	  } else {
		  doFind( code, findText.getText(), 
				  replaceText.getText(), !match.getSelection(), 
				  wrap.getSelection(), regexp.getSelection() );
	  }
  }

  /**
   * Creates the dialog's contents
   *
   * @param shell
   */
  protected void createContents(final Shell shell) {
    shell.setLayout( new GridLayout( 1, false ) );

    // Add the text input fields
    Composite text = new Composite( shell, SWT.NONE );
    text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    text.setLayout( new GridLayout( 2, false ) );

    new Label(text, SWT.LEFT).setText("&Find:");
    findText = new Text( text, SWT.BORDER | SWT.ICON_SEARCH | SWT.SEARCH );
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    findText.setLayoutData(data);

    new Label(text, SWT.LEFT).setText("R&eplace With:");
    replaceText = new Text(text, SWT.BORDER);
    data = new GridData(GridData.FILL_HORIZONTAL);
    replaceText.setLayoutData(data);
    
    Composite options = new Composite( shell, SWT.NONE );
    options.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    options.setLayout( new GridLayout( 5, false ) );

    
    GridData checkSpacerData = new GridData( GridData.FILL_HORIZONTAL );
    checkSpacerData.minimumWidth = 60;
    new Label( options, SWT.NONE ).setLayoutData( checkSpacerData );
    
    // Add the regular expression checkbox
    regexp = new Button(options, SWT.CHECK);
    regexp.setText("Regular E&xpression");
    regexp.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    
    // Add the match case checkbox
    match = new Button(options, SWT.CHECK);
    match.setText("Ignore &Case");
    match.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    match.setSelection( true );
    
    // Add wrap checkbox
    wrap = new Button(options, SWT.CHECK);
    wrap.setText("&Wrap Around");
    wrap.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    
    new Label( options, SWT.NONE ).setLayoutData( checkSpacerData );
    
    // Add the buttons
    Composite buttons = new Composite( shell, SWT.NONE );
    buttons.setLayout( new GridLayout(6, false) );
    
    // Create the Replace All button
    doReplaceAll = new Button(buttons, SWT.PUSH);
    doReplaceAll.setText("Replace &All");
    doReplaceAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    doReplaceAll.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
        	replaceAll( );
        }
	});
    
    // Create the Replace button
    doReplace = new Button(buttons, SWT.PUSH);
    doReplace.setText("&Replace");
    doReplace.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    // Create the Replace/Find button
    doReplaceFind = new Button(buttons, SWT.PUSH);
    doReplaceFind.setText("Replace/Fin&d");
    doReplaceFind.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    doReplaceFind.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        doFind( REPLACE_FIND_NEXT, findText.getText(),
            replaceText.getText(), !match.getSelection(),
            wrap.getSelection(), regexp.getSelection());
      }
    });
    

    GridData spacerData = new GridData(GridData.FILL_HORIZONTAL);
    spacerData.widthHint = 60;
    new Label( buttons, SWT.NONE ).setLayoutData( spacerData );

    // Create the Find buttons
    
    doPrev = new Button(buttons, SWT.PUSH);
    doPrev.setText("&Previous");
    doPrev.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    doNext = new Button(buttons, SWT.PUSH);
    doNext.setText("&Next");
    doNext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    /*
    // Create the Close button
    Button close = new Button(buttons, SWT.PUSH);
    close.setText("Close");
    close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        shell.close();
      }
    });
    */

    // Reset the FIND_FIRST/FIND_NEXT when find text is modified
    findText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent event) {
        enableReplaceButtons(false);
      }
    });

    // Change to FIND_NEXT and enable replace buttons on successful find
    doNext.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        // Do the find, pulling the operation code out of the button
        doFind( FIND_NEXT, findText
            .getText(), replaceText.getText(), !match
            .getSelection(), wrap.getSelection(), regexp.getSelection());
      }
    });
    
    doPrev.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
          // Do the find, pulling the operation code out of the button
          doFind( FIND_PREV, findText
              .getText(), replaceText.getText(), !match
              .getSelection(), wrap.getSelection(), regexp.getSelection());
        }
      });

    // Replace loses "find" state, so disable buttons
    doReplace.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        doFind( REPLACE, findText.getText(), replaceText
            .getText(), !match.getSelection(), wrap
            .getSelection(), regexp.getSelection());
      }
    });

    // Set defaults
    findText.setFocus( );
    doReplace.setEnabled( false );
    doReplaceFind.setEnabled( false );
    shell.setDefaultButton( doNext );
  }
  /**
   * Enables/disables the Replace and Replace/Find buttons
   *
   * @param enable whether to enable or disable
   */
  protected void enableReplaceButtons(boolean enable) {
    doReplace.setEnabled(enable);
    doReplaceFind.setEnabled(enable);
  }

  /**
   * Shows an error
   *
   * @param message the error message
   */
  protected void showError(String message) {
    MessageDialog.openError(getParent(), "Error", message);
  }
}
