package app.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.FileIO;

import plugins.documents.imageDocument.ImageDocumentPlugin;
import plugins.documents.sourceDocument.SourceDocumentPlugin;
import plugins.toolTabs.console.BuildConsolePlugin;


import app.Application;
import app.plugin.interfaces.IPlugin;

public class PluginManager {
	
	List<IPlugin> availablePlugins;
	List<IPlugin> loadedPlugins;
	JSONArray enabledPlugins;
	JSONObject appSettings;
	IPlugin defaultDocument;
	
	public PluginManager( ) throws JSONException {
		loadedPlugins = new ArrayList<IPlugin>( );
		availablePlugins = new ArrayList<IPlugin>( );
		defaultDocument = new SourceDocumentPlugin( );
		loadPlugins( );
	}
	
	public boolean isEnabled( IPlugin plugin ) {
		boolean enabled = false;
		for ( int i = 0; i < enabledPlugins.length(); i++ ) {
			try {
				if ( enabledPlugins.getString( i ).equals( plugin.getPluginName() ) ) {
					enabled = true;
					break;
				}
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
		}
		
		return enabled;
	}
	
	public IPlugin getDefaultDocumentPlugin( ) {
		return defaultDocument;
	}
	
	
	public void setEnabled( IPlugin plugin, boolean enabled ) {
		setEnabled( plugin.getPluginName(), enabled );
	}
	
	public void setEnabled( String pluginName, boolean enabled ) {
		for ( int i = 0; i < enabledPlugins.length(); i++ ) {
			try {
				if ( enabledPlugins.getString( i ).equals( pluginName ) ) {
					if ( !enabled ) {
						enabledPlugins.remove( i );
					}
					return;
				}
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
		}
		
		if ( enabled ) {
			enabledPlugins.put( pluginName );
		}
	}
	
	public void saveLoadedPlugins( ) {
		try {
			appSettings.put( "Enabled Plugins", enabledPlugins );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		PluginManager.savePluginJSON( "Application", new File( Application.appSettingsFileName ), appSettings );
	}
	
	public void loadPlugins( ) throws JSONException {
		
		File settingsFile = new File( Application.appSettingsFileName );
		appSettings = getPluginJSON( "Application", settingsFile );
		enabledPlugins = appSettings.getJSONArray( "Enabled Plugins" );
		
		IPlugin[] plugins = new IPlugin[] {
		    getDefaultDocumentPlugin( ),
			new BuildConsolePlugin( ),
			new ImageDocumentPlugin( ),
		};
		
		setEnabled( getDefaultDocumentPlugin( ), true );
		
		for ( int i = 0; i < plugins.length; i++ ) {
			if ( isEnabled( plugins[i]) ) {
				loadedPlugins.add( plugins[i] );
			}
			availablePlugins.add( plugins[i] );
		}
	}
	
	/**
	 * Lists enabled plug-ins
	 * @return a list of enabled plug-ins
	 */
	public List<IPlugin> listLoadedPlugins( ) {
		return loadedPlugins;
	}
	
	public List<IPlugin> listAllPlugins( ) {
		return availablePlugins;
	}
	
	public static void savePluginJSON( String name, File settingsFile, JSONObject newSettings ) {
		JSONObject appPrefObject = FileIO.readJSONFile( settingsFile );
		
		if ( appPrefObject != null ) {
			try {
				appPrefObject.put( name, newSettings );
			} catch (JSONException e) {
				// something didn't work
				e.printStackTrace();
			} finally {
				FileIO.writeJSONFile( appPrefObject, 3, settingsFile );
			}
		}
	}
	
	public static JSONObject getPluginJSON( String name, File settingsFile ) {
		if ( !settingsFile.exists() ) {
			try {
				settingsFile.createNewFile( );
				FileIO.writeJSONFile( new JSONObject( ), 3, settingsFile );
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		JSONObject pluginPrefObject = null;
		JSONObject prefObject = FileIO.readJSONFile( settingsFile );	
		
		if ( prefObject != null ) {
			try {
				pluginPrefObject = prefObject.getJSONObject( name );
			} catch ( JSONException e ) {
				try {
					JSONObject newObject = new JSONObject( );
					pluginPrefObject = prefObject.put( name, newObject );
				} catch ( JSONException e1 ) {
					e1.printStackTrace();
				}
			}
		}
		
		return pluginPrefObject;
	}
	
	public static JSONObject getPluginProjectSettings( File projectPath, IPlugin plugin ) {
		String name = plugin.getPluginName( );
		File settingsFile = new File( projectPath, Application.projectSettingsFileName );
		
		return getPluginJSON( name, settingsFile );
	}

	public static JSONObject getPluginAppSettings( IPlugin plugin ) {
		String name = plugin.getPluginName( );
		File settingsFile = new File( Application.appSettingsFileName );
		
		return getPluginJSON( name, settingsFile );
	}
	
	public static void savePluginProjectSettings( File projectPath, IPlugin plugin, JSONObject newSettings ) {
		File settingsPath = new File( projectPath, Application.projectSettingsFileName );
		savePluginJSON( plugin.getPluginName(), settingsPath, newSettings );
	}
	
	public static void savePluginAppSettings( IPlugin plugin, JSONObject newSettings ) {
		File settingsPath = new File( Application.appSettingsFileName );
		savePluginJSON( plugin.getPluginName(), settingsPath, newSettings );
	}

	public static JSONObject getFreshAppSettings() {
		try {
			return new JSONObject( "{\"Enabled Plugins\":[]}" );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return null;
	}
}
