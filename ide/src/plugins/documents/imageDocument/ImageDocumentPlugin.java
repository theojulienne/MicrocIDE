package plugins.documents.imageDocument;

import java.io.File;


import app.plugin.base.PluginDocument;
import app.plugin.base.PluginTool;
import app.plugin.base.PluginAppPreferences;
import app.plugin.base.PluginProjectPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IDocumentParent;
import app.plugin.interfaces.parents.IPreferencesParent;
import app.plugin.interfaces.parents.IToolParent;

public class ImageDocumentPlugin implements IPlugin {

	public String getPluginName() {
		return "Image Document";
	}

	public String[] getSupportedDocumentExtensions() {
		return ImageDocument.getAssociatedExtensions();
	}

	public PluginDocument createDocument( IDocumentParent parent, File file ) {
		return new ImageDocument( parent, file );
	}

	public String getIconFilenameForExtension( String extension ) {
		return "doc_img.png";
	}

	public PluginTool createTool( IToolParent parent ) {
		return null;
	}

	public PluginProjectPreferences createProjectPreferences(
			IPreferencesParent parent) {
		return null;
	}

	public PluginAppPreferences createAppPreferences(IPreferencesParent parent) {
		return null;
	}

	public boolean hasDocument() {
		return true;
	}

	public boolean hasTool() {
		return false;
	}

	public boolean hasProjectPreferences() {
		return false;
	}

	public boolean hasAppPreferences() {
		return false;
	}

	@Override
	public boolean hasTemplatePresets( ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void applyProjectTemplateSettings( String templateName,
			IPreferencesParent parent ) {
		// TODO Auto-generated method stub
		
	}

}
