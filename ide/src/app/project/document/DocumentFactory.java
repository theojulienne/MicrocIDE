package app.project.document;

import java.io.File;

import app.project.document.sourceDocument.SourceDocument;

public class DocumentFactory {
	
	private IDocumentParent parent;

	public DocumentFactory( IDocumentParent parent ) {
		this.parent = parent;
	}
	
	public IDEDocument createDocument( File file ) {
		return new SourceDocument( parent, file );
	}
}
