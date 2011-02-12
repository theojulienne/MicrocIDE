package app;

import java.util.HashMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import app.plugin.PluginManager;
import app.plugin.interfaces.IPlugin;

public class ImageManager {
	private HashMap<String, Image> imageIndex;
	private HashMap<String, Image> fileIconIndex;
	private Display display;

	public ImageManager( Display display, PluginManager plugins ) {
		this.display = display;
		this.imageIndex = new HashMap<String, Image>( );
		this.fileIconIndex = new HashMap<String, Image>( );
		
		addImage( "folder", "folder.png" );
		addImage( "defaultIcon", "doc_other.png" );
		
		addIconForExtension( "settings", "doc_settings.png" );
		addIconForExtension( "bin", "doc_bin.png" );
		addIconForExtension( "hex", "doc_bin.png" );
		
		for ( IPlugin plugin : plugins.listLoadedPlugins() ) {
			if ( plugin.getSupportedDocumentExtensions() != null ) {
				for ( String ext : plugin.getSupportedDocumentExtensions() ) {
					String filename = plugin.getIconFilenameForExtension( ext );
					if ( filename != null ) {
						addIconForExtension( ext, filename );
					}
				}
			}
		}
	}
	
	public boolean addIconForExtension( String ext, Image image ) {
		boolean added = false;
		if ( !fileIconIndex.containsKey( ext ) ) {
			fileIconIndex.put( ext, image );
			added = true;
		}
		
		return added;
	}
	
	public boolean addIconForExtension( String ext, String filename ) {
		boolean added = false;
		if ( !fileIconIndex.containsKey( ext ) ) {
			fileIconIndex.put( ext, new Image( display, filename ) );
			added = true;
		}
		
		return added;		
	}
	
	public Image getIconForExtension( String ext ) {
		if ( fileIconIndex.containsKey( ext ) ) {
			return fileIconIndex.get( ext );
		}
		
		return null;
	}
	
	public boolean addImage( String key, String filename ) {
		boolean added = false;
		if ( !imageIndex.containsKey( key ) ) {
			imageIndex.put( key, new Image( display, filename ) );
			added = true;
		}
		
		return added;
	}
	
	public boolean addImage( String key, Image image ) {
		boolean added = false;
		if ( !imageIndex.containsKey( key ) ) {
			imageIndex.put( key, image );
			added = true;
		}
		
		return added;		
	}
	
	public Image getImage( String key ) {
		if ( imageIndex.containsKey( key ) ) {
			return imageIndex.get( key );
		}
		
		return null;
	}
}
