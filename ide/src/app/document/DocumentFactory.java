package app.document;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import app.Application;
import app.plugin.PluginManager;
import app.plugin.base.PluginDocument;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IDocumentParent;

public class DocumentFactory {
	
	private IDocumentParent parent;

	/**
	 * Constructor for a DocumentFactory
	 * @param parent The parent class (and application interface) for the documents created by this factory instance
	 */
	public DocumentFactory( IDocumentParent parent ) {
		this.parent = parent;
	}
	
	/**
	 * Creates (opens) a new IDEDocument from a given File. Looks through loaded plug-ins to see which can provide 
	 * an IDEDocument for this file type. 
	 * @param file The file storing the data for the document to create
	 * @return An IDEDocument which displays the given File
	 */
	public PluginDocument createDocument( File file ) {
		String[] fileParts = file.getName().split("\\.");
		String ext = fileParts[fileParts.length-1];
		
		PluginManager pluginManager = Application.getInstance().getPluginManager();
		IPlugin defaultPlugin = pluginManager.getDefaultDocumentPlugin( );
		
		for ( IPlugin plugin : pluginManager.listLoadedPlugins() ) {
			if ( plugin.hasDocument() ) {
				List<String> extList = Arrays.asList( plugin.getSupportedDocumentExtensions() );
				
				if ( extList.contains( ext ) ) {
					PluginDocument document = plugin.createDocument( parent, file );
					if ( document != null ) { // ensure this plugin can create documents
						return document;
					}
				}
			}
		}
		
		return defaultPlugin.createDocument( parent, file );
	}
}
