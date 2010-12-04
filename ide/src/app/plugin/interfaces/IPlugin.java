package app.plugin.interfaces;

import java.io.File;

import app.plugin.base.PluginDocument;
import app.plugin.base.PluginTool;
import app.plugin.base.PluginAppPreferences;
import app.plugin.base.PluginProjectPreferences;
import app.plugin.interfaces.parents.IDocumentParent;
import app.plugin.interfaces.parents.IPreferencesParent;
import app.plugin.interfaces.parents.IToolParent;

public interface IPlugin {
	String getPluginName( );
	
	String getIconFilenameForExtension( String extension );
	String[] getSupportedDocumentExtensions( );
	
	boolean hasDocument( );
	boolean hasTool( );
	boolean hasProjectPreferences( );
	boolean hasAppPreferences( );
	
	PluginDocument createDocument( IDocumentParent parent, File file );
	PluginTool createTool( IToolParent parent );
	PluginProjectPreferences createProjectPreferences( IPreferencesParent parent );
	PluginAppPreferences createAppPreferences( IPreferencesParent parent );
}
