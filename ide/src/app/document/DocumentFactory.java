package app.document;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import plugins.documents.sourceDocument.SourceDocumentPlugin;

import app.plugin.PluginManager;
import app.plugin.base.IDEDocument;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IDocumentParent;

public class DocumentFactory {
	
	private IDocumentParent parent;

	public DocumentFactory( IDocumentParent parent ) {
		this.parent = parent;
	}
	
	public IDEDocument createDocument( File file ) {
		String[] fileParts = file.getName().split("\\.");
		String ext = fileParts[fileParts.length-1];
		
		IPlugin defaultPlugin = new SourceDocumentPlugin( );
		
		for ( IPlugin plugin : PluginManager.listPlugins() ) {
			List<String> extList = Arrays.asList( plugin.getSupportedDocumentExtensions() );
			if ( extList.contains( ext ) ) {
				IDEDocument document = plugin.createDocument( parent, file );
				if ( document != null ) { // ensure this plugin can create documents
					return document;
				}
			}
		}
		
		return defaultPlugin.createDocument( parent, file );
	}
}
