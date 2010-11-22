package plugins.documents.sourceDocument;

import java.io.File;
import java.util.HashMap;

import app.plugin.base.IDEDocument;
import app.plugin.base.IDETool;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IDocumentParent;
import app.plugin.interfaces.IToolTabParent;

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

	public IDEDocument createDocument(IDocumentParent parent, File file) {
		return new SourceDocument( parent, file );
	}

	public String getIconFilenameForExtension( String extension ) {
		if ( icons.containsKey( extension) ) {
			return icons.get( extension );
		} else {
			return null;
		}
	}

	public IDETool createTool(IToolTabParent parent) {
		return null;
	}

}
