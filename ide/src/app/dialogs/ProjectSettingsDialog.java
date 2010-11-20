package app.dialogs;

// TODO Save and add Preset

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import files.FileIO;

import app.Application;
import app.controls.FancyListBox;

public class ProjectSettingsDialog extends Dialog {

	private Shell shell;
	private FancyListBox buildList;
	private FancyListBox deployList;
	private JSONObject presets;
	  
	public ProjectSettingsDialog( Shell parentShell, File projectPath ) {
		super( parentShell, SWT.DIALOG_TRIM | SWT.RESIZE );
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

		
		try {
			File settingsFile = new File( Application.projectSettingsFile );
			JSONObject globalSettings = new JSONObject( FileIO.readFile( settingsFile ) );
			presets = globalSettings.getJSONObject( "presets" );
			Iterator<String> presetIterator = presets.keys( );
			while ( presetIterator.hasNext( ) ) {
				presetList.add( presetIterator.next( ) );
			}
		} catch ( JSONException e ) {
			e.printStackTrace( );
		}
		
		presetList.addMouseListener( new MouseListener() {
			public void mouseUp(MouseEvent evt) {
			}
			public void mouseDown(MouseEvent evt) {
			}
			public void mouseDoubleClick(MouseEvent evt) {
				List list = (List) evt.widget;
				if ( list.getSelectionCount() > 0 ) {
					String key = list.getSelection()[0];
					try {
						JSONObject preset = presets.getJSONObject( key );
						JSONArray buildCmds = preset.getJSONArray( "Build" );
						JSONArray deployCmds = preset.getJSONArray( "Deploy" );
						
						buildList.reset( );
						for ( int i = 0; i < buildCmds.length(); i++ ) {
							buildList.add( buildCmds.getString( i ) );
						}
						
						deployList.reset( );
						for ( int i = 0; i < deployCmds.length(); i++ ) {
							deployList.add( deployCmds.getString( i ) );
							
						}
					} catch ( JSONException ex ) {
						ex.printStackTrace();
					}
					
				}
			}
		} );
		
		commandsArea.setLayout( new GridLayout( 3, false ) );
		
		sashForm.setWeights( new int[]{1, 3} );
		

		GridData textData = new GridData( SWT.FILL, SWT.FILL, true, false );
		textData.minimumWidth = 450;
		textData.horizontalSpan = 3;
		
		new Label( commandsArea, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 3, 1 )  );
		
		new Label( commandsArea, SWT.LEFT ).setText( "Build Commands:" );
		
		buildList = new FancyListBox( commandsArea, SWT.NONE );
		buildList.setLayoutData( textData );
		/*
		buildText = new Text( commandsArea, SWT.BORDER | SWT.SINGLE );
		buildText.setLayoutData( textData );
		*/

		new Label( commandsArea, SWT.LEFT ).setText( "Deploy Commands:" );
		

		deployList = new FancyListBox( commandsArea, SWT.NONE );
		deployList.setLayoutData( textData );
		/*
		deployText = new Text( commandsArea, SWT.BORDER | SWT.SINGLE );
		deployText.setLayoutData( textData );
		*/
		
		Button addPresetButton = new Button( commandsArea, SWT.PUSH );
		addPresetButton.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, false, false ) );
		addPresetButton.setText( "Add as Preset" );
		
		new Label( commandsArea, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true )  );
		
		Button saveButton = new Button( commandsArea, SWT.PUSH );
		saveButton.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, false, false ) );
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
