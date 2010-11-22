package app.project.document.sourceDocument;

import java.io.File;
import java.util.HashMap;

import app.plugin.IDEPlugin;
import app.project.document.IDEDocument;
import app.project.document.IDocumentParent;

public class SourceDocumentPlugin implements IDEPlugin {
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

}
