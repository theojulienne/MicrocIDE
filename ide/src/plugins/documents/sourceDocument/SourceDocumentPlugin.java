package plugins.documents.sourceDocument;

import java.io.File;
import java.util.HashMap;

import app.plugin.base.PluginDocument;
import app.plugin.base.PluginTool;
import app.plugin.base.PluginAppPreferences;
import app.plugin.base.PluginProjectPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IDocumentParent;
import app.plugin.interfaces.parents.IPreferencesParent;
import app.plugin.interfaces.parents.IToolParent;

public class SourceDocumentPlugin implements IPlugin {
	private HashMap<String, String> icons;
	
	public SourceDocumentPlugin( ) {
		icons = new HashMap<String, String>( );
		icons.put( "txt", "doc_txt.png" );
		icons.put( "c"  , "doc_c.png"   );
		icons.put( "cpp", "doc_cpp.png" );
		icons.put( "h"  , "doc_h.png"   );
		icons.put( "xml", "doc_xml.png" );
	}
	
	public String getPluginName() {
		return "Source Editor";
	}

	public String[] getSupportedDocumentExtensions() {
		return SourceDocument.getAssociatedExtensions();
	}

	public PluginDocument createDocument( IDocumentParent parent, File file ) {
		return new SourceDocument( this, parent, file );
	}

	public String getIconFilenameForExtension( String extension ) {
		if ( icons.containsKey( extension) ) {
			return icons.get( extension );
		} else {
			return null;
		}
	}

	public PluginTool createTool( IToolParent parent ) {
		return null;
	}

	public PluginProjectPreferences createProjectPreferences(
			IPreferencesParent parent) {
		return null;
	}

	public PluginAppPreferences createAppPreferences( IPreferencesParent parent ) {
		return new SourceDocumentPreferences( this, parent );
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
		return true;
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
