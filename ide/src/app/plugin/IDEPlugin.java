package app.plugin;

import java.io.File;

import app.project.document.IDEDocument;
import app.project.document.IDocumentParent;

public interface IDEPlugin {
	String getPluginName( );
	
	String getIconFilenameForExtension( String extension );
	String[] getSupportedDocumentExtensions( );
	IDEDocument createDocument( IDocumentParent parent, File file );
}
