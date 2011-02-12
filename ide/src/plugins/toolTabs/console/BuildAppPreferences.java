package plugins.toolTabs.console;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.json.JSONException;
import org.json.JSONObject;

import app.plugin.base.PluginAppPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

public class BuildAppPreferences extends PluginAppPreferences {
	
	protected Label demoFontText;
	protected Composite settingsArea;
	
	public BuildAppPreferences( IPlugin plugin, IPreferencesParent parent ) {
		super( plugin, parent );
	}
	
	protected void createContents() {
		settingsArea = new Composite( widgetParent, SWT.NONE );
		settingsArea.setLayout( new GridLayout( 1, false ) );
		
		Group fontGroup = new Group( settingsArea, SWT.BORDER );
		fontGroup.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		fontGroup.setLayout( new GridLayout( 3, false ) );
		
		JSONObject editorFont;

		FontData fontData = null;
		String fontFace = "Default";
		

		try {
			if ( !settings.has( "Console Font" )  ) {
				editorFont = new JSONObject(  );
				
				if ( parent.isMac() ) {
					editorFont.put( "Font Face", "Monaco" );
				} else {
					editorFont.put( "Font Face", "Courier" );
				}
				editorFont.put( "Font Size", 12 );
				settings.put( "Console Font", editorFont );
			} else {
				editorFont = settings.getJSONObject( "Console Font" );
			}
			
			fontFace = editorFont.getString( "Font Face" );
			int fontHeight = editorFont.getInt( "Font Size" );
			fontData = new FontData( fontFace, fontHeight, SWT.NONE );
			
		} catch ( JSONException e ) {
			e.printStackTrace( );
		}
		
		new Label( fontGroup, SWT.LEFT ).setText( "Console Font: " );
		
		GridData fontLayoutData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
		fontLayoutData.minimumWidth = 150;
		
		demoFontText = new Label( fontGroup, SWT.CENTER );
		demoFontText.setFont( new Font( widgetParent.getDisplay(), fontData ) );
		demoFontText.setText( fontFace );
		demoFontText.setLayoutData( fontLayoutData );
		demoFontText.pack( );
		
		Button changeFontButton = new Button( fontGroup, SWT.PUSH );
		changeFontButton.setText( "Change Font" );
		changeFontButton.addSelectionListener( new SelectionListener() {
			public void widgetSelected( SelectionEvent e ) {

		        try {
		        	FontDialog fd = new FontDialog( widgetParent.getShell( ), SWT.NONE );
		        	fd.setText( "Select Font" );
		        
		        	JSONObject currentFont = settings.getJSONObject( "Console Font" );
		        	String fontFace = currentFont.getString( "Font Face" );
		        	int fontHeight = currentFont.getInt( "Font Size" );
				
		        	FontData currentFontData = new FontData( fontFace, fontHeight, SWT.BOLD );
		        	fd.setFontList( new FontData[] { currentFontData } );
		        	FontData newFont = fd.open();
		        	if (newFont == null) {
		        		return;
		        	}
		        	demoFontText.setFont( new Font( widgetParent.getDisplay( ), newFont ) );
		        	demoFontText.setText( newFont.getName() );
		        	demoFontText.pack( );
		        	
		        	settings.getJSONObject( "Console Font" ).put( "Font Face", newFont.getName( ) );
		        	settings.getJSONObject( "Console Font" ).put( "Font Size", newFont.getHeight( ) );
		        	
		        	parent.savePluginAppSettings( plugin, settings );
		        	
		        } catch ( JSONException ex ) {
		        	MessageDialog.openError( widgetParent.getShell( ), "Problem loading fonts", "Unable to load font settings" );
		        }
			}
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
		} );
		
		new Label( fontGroup, SWT.LEFT ).setText( "Changes will take effect next time this extension is loaded" );
		
	}
}
