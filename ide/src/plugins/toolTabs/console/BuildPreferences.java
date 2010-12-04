package plugins.toolTabs.console;

import app.plugin.base.PluginProjectPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.controls.FancyListBox;

public class BuildPreferences extends PluginProjectPreferences {


	private FancyListBox buildList;
	private FancyListBox deployList;
	private JSONObject presets;
	private JSONObject buildPrefs;
	private IPreferencesParent parent;
	private IPlugin plugin;
	
	private Combo presetList;
	
	public BuildPreferences( IPlugin plugin, IPreferencesParent parent ) {
		super( parent );
		buildPrefs = parent.getPluginProjectSettings( plugin );
		this.parent = parent;
		this.plugin = plugin;
		createContents( );
	}
	
	protected void createContents( ) {
		Composite container = new Composite( parent.getComposite(), SWT.BORDER );
		container.setLayout( new GridLayout( 1, false ) );
		
		Composite presetArea = new Composite( container, SWT.NONE );
		presetArea.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, true ) );
		presetArea.setLayout( new GridLayout( 1, false ) );
		
		new Label( presetArea, SWT.LEFT ).setText( "Presets:" );
		presetList = new Combo( presetArea, SWT.SINGLE | SWT.BORDER );
		GridData presetData = new GridData( SWT.FILL, SWT.TOP, true, false );
		presetData.minimumWidth = 250;
		presetList.setLayoutData( presetData );

		Composite commandsArea = new Composite( container, SWT.NONE );
		commandsArea.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
		commandsArea.setLayout( new GridLayout( 3, false ) );
		
		JSONObject globalSettings = parent.getPluginAppSettings( plugin );
		if ( globalSettings != null ) {
			try {
				presets = globalSettings.getJSONObject( "Presets" );
				Iterator<String> presetIterator = presets.keys( );
				while ( presetIterator.hasNext( ) ) {
					presetList.add( presetIterator.next( ) );
				}
			} catch ( JSONException e ) {
				e.printStackTrace( );
			}
		}
		
		presetList.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
			public void widgetSelected( SelectionEvent evt ) {
				Combo list = (Combo)evt.widget;
				if ( buildList.getItems().length > 0 || deployList.getItems().length > 0 ) {
					if ( !MessageDialog.openConfirm( parent.getComposite( ).getShell( ), "Apply Preset", "This will replace all commands with the preset. Continue?" ) ) {
						return;
					}
				}
				
				if ( list.getSelectionIndex() >= 0 ) {
					int index = list.getSelectionIndex();
					try {
						JSONObject preset = presets.getJSONObject( list.getItem( index ) );
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
		

		GridData textData = new GridData( SWT.FILL, SWT.FILL, true, false );
		textData.minimumWidth = 450;
		textData.horizontalSpan = 3;
		
		new Label( commandsArea, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 3, 1 )  );
		
		new Label( commandsArea, SWT.LEFT ).setText( "Build Commands:" );
		
		buildList = new FancyListBox( commandsArea, SWT.NONE );
		buildList.setLayoutData( textData );
		buildList.setFocus( );

		new Label( commandsArea, SWT.LEFT ).setText( "Deploy Commands:" );
		

		deployList = new FancyListBox( commandsArea, SWT.NONE );
		deployList.setLayoutData( textData );

		new Label( commandsArea, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );
		
		Button saveButton = new Button( commandsArea, SWT.PUSH );
		saveButton.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, false, false ) );
		saveButton.setText( "Apply and Save" );
		saveButton.addSelectionListener( new SelectionListener() {
			public void widgetSelected( SelectionEvent evt ) {
				saveSettings( );
			}
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
		} );
		
		loadSettings( );
	}
	
	private void loadSettings( ) {
		boolean requiresNewFile = false;
		
		if ( buildPrefs == null ) {
			requiresNewFile = true;
		} else {
			try {
				JSONArray buildCmds = buildPrefs.getJSONArray( "Build" );
				JSONArray deployCmds = buildPrefs.getJSONArray( "Deploy" );
				String presetName = buildPrefs.getString( "Preset Name" );
				
				presetList.setText( presetName );
				
				// populate lists
				for ( int i = 0; i < buildCmds.length(); i++ ) {
					buildList.add( buildCmds.getString( i ) );
				}
				
				for ( int i = 0; i < deployCmds.length(); i++ ) {
					deployList.add( deployCmds.getString( i ) );
				}
			} catch ( JSONException e ) {
				requiresNewFile = true;
			}
		}
		
		if ( requiresNewFile ) {
			// Nothing, just displays as blank info
		}
	}
	
	private void saveSettings( ) {
		try {
			JSONObject newSettings = new JSONObject( );
			JSONArray buildCmds = new JSONArray( buildList.getItems() );
			JSONArray deployCmds = new JSONArray( deployList.getItems() );
			
			newSettings.put( "Build", buildCmds );
			newSettings.put( "Deploy", deployCmds );
			newSettings.put( "Preset Name", presetList.getText() );
			parent.savePluginProjectSettings( plugin, newSettings );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
