package app.project.document.imageDocument;

import java.io.File;

import app.plugin.IDEPlugin;
import app.project.document.IDEDocument;
import app.project.document.IDocumentParent;

public class ImageDocumentPlugin implements IDEPlugin {

	public String getPluginName() {
		return "Image Document";
	}

	public String[] getSupportedDocumentExtensions() {
		return ImageDocument.getAssociatedExtensions();
	}

	public IDEDocument createDocument(IDocumentParent parent, File file) {
		return new ImageDocument( parent, file );
	}

	public String getIconFilenameForExtension(String extension) {
		return "doc_img.png";
	}

}
