package app.plugin.interfaces;

import java.io.File;

import app.plugin.base.IDEDocument;
import app.plugin.base.IDETool;

public interface IPlugin {
	String getPluginName( );
	
	String getIconFilenameForExtension( String extension );
	String[] getSupportedDocumentExtensions( );
	IDEDocument createDocument( IDocumentParent parent, File file );
	IDETool createTool( IToolTabParent parent );
}
