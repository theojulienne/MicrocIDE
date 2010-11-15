package app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class PreferencesDialog extends Dialog {

	private Shell shell;
	  
	public PreferencesDialog( Shell parentShell ) {
		super( parentShell, SWT.DIALOG_TRIM | SWT.MODELESS );
	}
	
	protected void createContents( ) {

		shell.setLayout( new FillLayout() );
		
		TabFolder tabs = new TabFolder( shell, SWT.NONE );
		
		TabItem colours = new TabItem( tabs, SWT.None );
		colours.setText( "Colours" );
		
		TabItem fonts = new TabItem( tabs, SWT.None );
		fonts.setText( "Fonts" );
		
	}
	
	private void createShell( ) {
	    shell = new Shell( getParent( ), getStyle( ) );
	    shell.setText( "Preferences" );
	    createContents( );
	    shell.pack( );
	}
	
	public void close( ) {
		shell.close();
	}
	
	public void open( ) {
		if ( shell == null || shell.isDisposed() ) {
			createShell( );
		}
		
	    shell.open( );
	    
	    Display display = getParent( ).getDisplay( );
	    
	    while ( !shell.isDisposed( ) ) {
	      if ( !display.readAndDispatch( ) ) {
	        display.sleep( );
	      }
	    }
	}

}
