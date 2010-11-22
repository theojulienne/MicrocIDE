package app.project.document;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import app.plugin.IDEPlugin;
import app.plugin.PluginManager;
import app.project.document.sourceDocument.SourceDocumentPlugin;

public class DocumentFactory {
	
	private IDocumentParent parent;

	public DocumentFactory( IDocumentParent parent ) {
		this.parent = parent;
	}
	
	public IDEDocument createDocument( File file ) {
		String[] fileParts = file.getName().split("\\.");
		String ext = fileParts[fileParts.length-1];
		
		IDEPlugin defaultPlugin = new SourceDocumentPlugin( );
		
		for ( IDEPlugin plugin : PluginManager.listPlugins() ) {
			List<String> extList = Arrays.asList( plugin.getSupportedDocumentExtensions() );
			if ( extList.contains( ext ) ) {
				return plugin.createDocument( parent, file );
			}
		}
		
		return defaultPlugin.createDocument( parent, file );
	}
}
