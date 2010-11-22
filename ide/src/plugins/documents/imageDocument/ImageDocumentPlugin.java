package plugins.documents.imageDocument;

import java.io.File;


import app.plugin.base.IDEDocument;
import app.plugin.base.IDETool;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IDocumentParent;
import app.plugin.interfaces.IToolTabParent;

public class ImageDocumentPlugin implements IPlugin {

	public String getPluginName() {
		return "Image Document";
	}

	public String[] getSupportedDocumentExtensions() {
		return ImageDocument.getAssociatedExtensions();
	}

	public IDEDocument createDocument( IDocumentParent parent, File file ) {
		return new ImageDocument( parent, file );
	}

	public String getIconFilenameForExtension( String extension ) {
		return "doc_img.png";
	}

	public IDETool createTool( IToolTabParent parent ) {
		return null;
	}

}
