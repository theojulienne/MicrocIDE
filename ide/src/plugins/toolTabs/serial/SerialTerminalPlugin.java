package plugins.toolTabs.serial;

import java.io.File;

import app.plugin.base.PluginAppPreferences;
import app.plugin.base.PluginDocument;
import app.plugin.base.PluginProjectPreferences;
import app.plugin.base.PluginTool;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IDocumentParent;
import app.plugin.interfaces.parents.IPreferencesParent;
import app.plugin.interfaces.parents.IToolParent;

public class SerialTerminalPlugin implements IPlugin {

	public String getPluginName() {
		return "Serial Terminal";
	}

	public String getIconFilenameForExtension(String extension) {
		return null;
	}

	public String[] getSupportedDocumentExtensions() {
		return null;
	}

	public boolean hasDocument() {
		return false;
	}

	public boolean hasTool() {
		return true;
	}

	public boolean hasProjectPreferences() {
		return false;
	}

	public boolean hasAppPreferences() {
		return false;
	}

	public PluginDocument createDocument( IDocumentParent parent, File file ) {
		return null;
	}

	public PluginTool createTool( IToolParent parent ) {
		return new SerialTerminal( this, parent );
	}

	public PluginProjectPreferences createProjectPreferences(
			IPreferencesParent parent) {
		return null;
	}

	public PluginAppPreferences createAppPreferences(IPreferencesParent parent) {
		return null;
	}

	public boolean hasTemplatePresets( ) {
		// TODO Auto-generated method stub
		return false;
	}

	public void applyProjectTemplateSettings( String templateName,
			IPreferencesParent parent ) {
		// TODO Auto-generated method stub
		
	}

}
