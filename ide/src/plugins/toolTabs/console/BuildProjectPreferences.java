package plugins.toolTabs.console;

import app.plugin.base.PluginProjectPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.controls.FancyListBox;

public class BuildProjectPreferences extends PluginProjectPreferences {
	public static final String ENV_KEY = "Environment";

	private FancyListBox buildList;
	private FancyListBox deployList;
	private JSONObject presets;
	private JSONObject buildPrefs;
	private IPreferencesParent parent;
	private IPlugin plugin;
	
	private Combo presetList;
	

	private Text envValue;
	private Combo envKey;
	private JSONObject settings;
	
	public BuildProjectPreferences( IPlugin plugin, IPreferencesParent parent ) {
		super( parent );
		buildPrefs = parent.getPluginProjectSettings( plugin );
		this.parent = parent;
		this.plugin = plugin;

		settings = parent.getPluginProjectSettings( plugin );
		
		createContents( );
	}
	
	private void populateKeys( ) {
		envKey.setItems( new String[] {} );
		
		JSONObject envSettings;
		String key = ENV_KEY;
		try {
			if ( settings.has( key ) ) {
				envSettings = settings.getJSONObject( key );
			} else {
				settings.put( key, new JSONObject( ) );
				for ( String sysKey : System.getenv().keySet() ) {
					settings.getJSONObject( key ).put( sysKey, System.getenv( sysKey ) );
				}
				
				envSettings = settings.getJSONObject( key );
				System.out.println( envSettings.getString( "PATH" ) );
			}
			
			Iterator<String> keys = envSettings.keys( );
			while ( keys.hasNext() ) {
				envKey.add( keys.next( ) );
			}
			
		} catch ( JSONException e ) {
			e.printStackTrace( );
		}
	}
	
	protected void createContents( ) {
		
		TabFolder tabs = new TabFolder( parent.getComposite(), SWT.NONE );
		TabItem buildCommandsTab = new TabItem( tabs, SWT.NONE );
		buildCommandsTab.setText( "Commands" );
		
		TabItem environmentTab = new TabItem( tabs, SWT.NONE );
		environmentTab.setText( "Environment" );
		
		Composite environmentGroup = new Composite( tabs, SWT.NONE );
		environmentTab.setControl( environmentGroup );
		
		environmentGroup.setLayoutData( new FillLayout( ) );
		
		GridLayout layout = new GridLayout( 1, false );
		environmentGroup.setLayout( layout );
		
		new Label( environmentGroup, SWT.LEFT ).setText( "Environment Variables:" );
		envKey = new Combo( environmentGroup, SWT.SINGLE );
		GridData rowData = new GridData( SWT.FILL, SWT.CENTER, true, false );
		envKey.setLayoutData( rowData );
		
		populateKeys( );
		
		
		
		
		envKey.addSelectionListener( new SelectionListener() {
			public void widgetSelected( SelectionEvent evt ) {
				JSONObject envSettings;
				String key = ENV_KEY;
				try {
					if ( settings.has( key ) ) {
						envSettings = settings.getJSONObject( key );
						if ( !envSettings.has( envKey.getText( ) ) ) {
							envSettings.put( envKey.getText(), "" );
						}
					} else {
						envSettings = settings.put( key, new JSONObject( ) );
						envSettings.put( envKey.getText(), "" );
					}
					
					
					envValue.setText( envSettings.getString( envKey.getText( ) ) );
				} catch ( JSONException e ) {
					e.printStackTrace( );
				}
			}
				
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
		} );
		
		envValue = new Text( environmentGroup, SWT.BORDER );
		envValue.setLayoutData( rowData );
		
		Composite buttonContainer = new Composite( environmentGroup, SWT.NONE );
		buttonContainer.setLayout( new GridLayout( 1, false ) );
		
		Button saveVarsButton = new Button( buttonContainer, SWT.PUSH );
		saveVarsButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, true ) );
		saveVarsButton.setText( "Save Variable" );
		saveVarsButton.addSelectionListener( new SelectionListener( ) {
			public void widgetSelected( SelectionEvent evt ) {
				JSONObject envSettings;
				String key = "Environment";
				try {
					if ( settings.has( key ) ) {
						envSettings = settings.getJSONObject( key );
					} else {
						envSettings = settings.put( key, new JSONObject( ) );
					}
					

					envSettings.put( envKey.getText(), envValue.getText() );
					
				} catch ( JSONException e ) {
					e.printStackTrace( );
				}
				
				saveVariables( );
				
				String envKeyText = envKey.getText();
				String envValText = envValue.getText();
				populateKeys( );
				envKey.setText( envKeyText );
				envValue.setText( envValText );
				
			}
			
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
		} );
		
		
		
		// Commands
		
		
		Composite container = new Composite( tabs, SWT.NONE );
		container.setLayout( new GridLayout( 1, false ) );
		buildCommandsTab.setControl( container );
		
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
				presetList.setText( "No Presets" );
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
		saveButton.setText( "Apply and Close" );
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
	
	public void saveVariables( ) {
		parent.savePluginProjectSettings( plugin, settings );
	}

}
