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
	// return a unique name for each plugin
	String getPluginName( );
	
	// given a file extension, returns a filename of an image to use as icon
	String getIconFilenameForExtension( String extension );
	// lists the file extensions the createDocument method supports
	String[] getSupportedDocumentExtensions( );
	
	// returns true iff this plugin supports opening documents
	boolean hasDocument( );
	// returns true iff this plugin can create a tool pane
	boolean hasTool( );
	// returns true iff this plugin has a project preferences pane
	boolean hasProjectPreferences( );
	// returns true iff this plugin has an application preferences pane
	boolean hasAppPreferences( );
	
	// creates a document if hasDocument( ); returns true, else returns null
	PluginDocument createDocument( IDocumentParent parent, File file );
	// creates a tool pane if hasTool( ); returns true, else returns null
	PluginTool createTool( IToolParent parent );
	// creates a project preferences pane
	PluginProjectPreferences createProjectPreferences( IPreferencesParent parent );
	// creates an application preferences pane
	PluginAppPreferences createAppPreferences( IPreferencesParent parent );
}
