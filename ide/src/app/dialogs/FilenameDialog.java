package app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FilenameDialog extends Dialog {
	private Shell shell;
	Text filenameText;
	boolean cancelled;
	private String savedText;
	
	public FilenameDialog( Shell parent ) {
		super( parent, SWT.DIALOG_TRIM | SWT.MODELESS );
	}

	protected void createContents( ) {
		shell.setLayout( new GridLayout( 1, false ) );

		// Add the text input fields
		Composite text = new Composite( shell, SWT.NONE );
		text.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		text.setLayout( new GridLayout( 2, false ) );
		
		new Label( text, SWT.LEFT ).setText( "&Filename:" );
		filenameText = new Text( text, SWT.BORDER );
		GridData data = new GridData( GridData.FILL_HORIZONTAL );
		filenameText.setLayoutData( data );

	    Composite buttons = new Composite( shell, SWT.NONE );
	    buttons.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
	    buttons.setLayout( new GridLayout( 5, false ) );
	    
	    GridData spacerData = new GridData( GridData.FILL_HORIZONTAL );
	    spacerData.minimumWidth = 60;
	    new Label( buttons, SWT.NONE ).setLayoutData( spacerData );
	    
	    // Add the regular expression checkbox
	    Button okButton = new Button( buttons, SWT.PUSH );
	    okButton.setText( "&OK" );
	    okButton.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
	    shell.setDefaultButton( okButton );
	    okButton.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
			public void widgetSelected( SelectionEvent e ) {
				ok( );
			}
	    } );
	    
	    new Label( buttons, SWT.NONE ).setLayoutData( spacerData );
	    
	    // Add the regular expression checkbox
	    Button canButton = new Button( buttons, SWT.PUSH );
	    canButton.setText( "&Cancel" );
	    canButton.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
	    canButton.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
			public void widgetSelected( SelectionEvent e ) {
				cancel( );
			}
	    } );
	    new Label( buttons, SWT.NONE ).setLayoutData( spacerData );
	}
	
	private void ok( ) {
		cancelled = false;
		savedText = filenameText.getText( );
		shell.close( );
	}
	
	private void cancel( ) {
		cancelled = true;
		shell.close( );
	}
	
	public String open( ) {
		cancelled = false;
		String text = null;
	    shell = new Shell( getParent( ), getStyle( ) );
	    shell.setText( "Project Settings" );
	    createContents( );
	    shell.pack( );
	    shell.open( );
	    
	    Display display = getParent( ).getDisplay( );
	    
	    while ( !shell.isDisposed( ) ) {
	      if ( !display.readAndDispatch( ) ) {
	        display.sleep( );
	      }
	    }
	    
	    if ( !cancelled ) {
	    	text = savedText;
	    }
	    
	    return text;
	}
	
}
