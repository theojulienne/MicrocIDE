package app.dialogs;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProjectSettingsDialog extends Dialog {

	private Shell shell;
	private Text buildText;
	private Text deployText;
	  
	public ProjectSettingsDialog( Shell parentShell, File projectPath ) {
		super( parentShell, SWT.DIALOG_TRIM | SWT.RESIZE );
		// TODO Auto-generated constructor stub
	}
	
	protected void createContents( ) {
		shell.setLayout( new FillLayout( ) );


		SashForm sashForm = new SashForm( shell, SWT.HORIZONTAL | SWT.BORDER );
		
		Composite presetArea = new Composite( sashForm, SWT.NONE );
		Composite commandsArea = new Composite( sashForm, SWT.NONE );
		
		presetArea.setLayout( new GridLayout( 1, false ) );

		new Label( presetArea, SWT.LEFT ).setText( "Presets:" );
		List presetList = new List( presetArea, SWT.SINGLE | SWT.BORDER );
		presetList.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

		
		// TODO: remove TEST
		presetList.add( "Penguino AVR" );
		presetList.add( "Arduino" );
		
		
		
		commandsArea.setLayout( new GridLayout( 2, false ) );
		
		sashForm.setWeights( new int[]{1, 3} );
		

		GridData textData = new GridData( SWT.FILL, SWT.FILL, true, false );
		textData.minimumWidth = 220;
		
		new Label( commandsArea, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 )  );
		
		new Label( commandsArea, SWT.LEFT ).setText( "Build Command:" );
		
		
		buildText = new Text( commandsArea, SWT.BORDER | SWT.SINGLE );
		buildText.setLayoutData( textData );
		

		new Label( commandsArea, SWT.LEFT ).setText( "Deploy Command:" );
		
		deployText = new Text( commandsArea, SWT.BORDER | SWT.SINGLE );
		deployText.setLayoutData( textData );
		
		new Label( commandsArea, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 )  );
		
		Button saveButton = new Button( commandsArea, SWT.PUSH );
		saveButton.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, false, false, 2, 1 ) );
		saveButton.setText( "Save Settings" );
		
	}
	
	
	private void createShell( ) {
	    shell = new Shell( getParent( ), getStyle( ) );
	    shell.setText( "Project Settings" );
	    createContents( );
	    shell.pack( );
	    shell.open( );
	}
	
	public void open( ) {
		if ( shell == null || shell.isDisposed() ) {
			createShell( );
		}
		
	    Display display = getParent( ).getDisplay( );
	    
	    while ( !shell.isDisposed( ) ) {
	      if ( !display.readAndDispatch( ) ) {
	        display.sleep( );
	      }
	    }
	}

}
