package plugins.toolTabs.console;

import java.io.File;

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
		return new BuildConsole( parent );
	}
	
	public PluginProjectPreferences createProjectPreferences(
			IPreferencesParent parent) {
		return new BuildPreferences( this, parent );
	}

	public PluginAppPreferences createAppPreferences(IPreferencesParent parent) {
		return null;
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
		return false;
	}
}
