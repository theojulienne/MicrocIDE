package plugins.toolTabs.console;

import java.io.File;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import app.plugin.base.PluginDocument;
import app.plugin.base.PluginTool;
import app.plugin.base.PluginAppPreferences;
import app.plugin.base.PluginProjectPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IDocumentParent;
import app.plugin.interfaces.parents.IPreferencesParent;
import app.plugin.interfaces.parents.IToolParent;

public class BuildConsolePlugin implements IPlugin {

	public String getPluginName() {
		return "Build Console";
	}

	public String getIconFilenameForExtension( String extension ) {
		return null;
	}

	public String[] getSupportedDocumentExtensions() {
		return null;
	}

	public PluginDocument createDocument( IDocumentParent parent, File file ) {
		return null;
	}

	public PluginTool createTool( IToolParent parent ) {
		return new BuildConsole( this, parent );
	}
	
	public PluginProjectPreferences createProjectPreferences(
			IPreferencesParent parent) {
		return new BuildProjectPreferences( this, parent );
	}

	public PluginAppPreferences createAppPreferences(IPreferencesParent parent) {
		return new BuildAppPreferences( this, parent );
	}

	public boolean hasDocument() {
		return false;
	}

	public boolean hasTool() {
		return true;
	}

	public boolean hasProjectPreferences() {
		return true;
	}

	public boolean hasAppPreferences() {
		return true;
	}
	
	public boolean hasTemplatePresets( ) {
		return true;
	}

	@Override
	public void applyProjectTemplateSettings( String templateName,
			IPreferencesParent parent ) {
		
		// get both app settings and project settings for this plugin
		JSONObject appSettings = parent.getPluginAppSettings( this );
		JSONObject projectSettings = parent.getPluginProjectSettings( this );
		
		try {
			// get the presets in the app settings
			JSONObject presets = appSettings.getJSONObject( "Presets" );
			
			// iterate through the presets
			Iterator<String> presetIter = presets.keys( );
			while ( presetIter.hasNext( ) ) {
				String key = presetIter.next( );
				
				// this preset matches the selected template
				if ( key.equals( templateName ) ) {
					
					// get the preset from app settings
					JSONObject preset = presets.getJSONObject( key );
					
					// apply the settings to the project
					projectSettings.put( "Build", preset.get(  "Build" ) );
					projectSettings.put( "Deploy", preset.get( "Deploy" ) );
					projectSettings.put( "Preset Name", key );
					parent.savePluginProjectSettings( this, projectSettings );
					
					break;
				}
				
			}
			
		} catch ( JSONException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
