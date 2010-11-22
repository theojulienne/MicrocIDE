package app.project.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

import app.project.ProjectWindow;

public class DocumentTabFolder extends CTabFolder implements IDocumentParent {

	private HashMap<File, IDEDocument> documents;
	private HashMap<IDEDocument, DocumentTab> tabs;
	private ProjectWindow project;
	private DocumentFactory documentFactory;
	
	public DocumentTabFolder( Composite parent, int style ) {
		super( parent, style );
		
		this.documents = new HashMap<File, IDEDocument>( );
		this.tabs = new HashMap<IDEDocument, DocumentTab>( );
		
		this.setBorderVisible( true );
		
		// appears on mouse-over
		this.setUnselectedCloseVisible( true );
		
		// makes them less ugly
		this.setSimple( true );
		
		documentFactory = new DocumentFactory( this );
	}
	
	public DocumentTab[] getDocumentTabs( ) {
		Collection<DocumentTab> tabCollection = this.tabs.values();
		return tabCollection.toArray( new DocumentTab[tabCollection.size()] );
	}
	
	public void renameDocumentTab( File fromFile, File toFile ) {
		if ( documents.containsKey( fromFile ) ) {
			IDEDocument doc = documents.get( fromFile );
			doc.rename( toFile );
		}
	}
	
	public void closeTab( DocumentTab tab ) {
		// clean up references to the tab
		tabs.remove( tab.getDocument() );
		documents.remove( tab.getDocument().getFile() );
		tab.dispose( );
	}
	
	public boolean closeAllTabs( ) {
		boolean succeeded = true;
		for ( DocumentTab tab : this.getDocumentTabs() ) {
			if ( !tab.close( ) ) {
				succeeded = false;
				break;
			}
		}
		
		return succeeded;
	}
	
	public ArrayList<DocumentTab> getUnsavedTabs( ) {
		ArrayList<DocumentTab> unsaved = new ArrayList<DocumentTab>( );
		for ( DocumentTab tab : this.getDocumentTabs( ) ) {
			if ( !tab.getDocument( ).isSaved( ) ) {
				unsaved.add( tab );
			}
		}
		
		return unsaved;
	}
	
	public void setProjectWindow( ProjectWindow project ) {
		this.project = project;
	}

	public void setSelection( IDEDocument ideDocument ) {
		if ( tabs.containsKey( ideDocument ) ) {
			DocumentTab tabItem = tabs.get( ideDocument );
			this.setSelection( tabItem );
		}
	}

	public Composite getComposite( ) {
		return this;
	}

	public void setDocumentSaved( IDEDocument ideDocument, boolean isSaved ) {
		if ( tabs.containsKey( ideDocument ) ) {
			DocumentTab tabItem = tabs.get( ideDocument );
			if ( isSaved ) {
				tabItem.setSaved( );
			} else {
				tabItem.setUnsaved( );
			}
		}
	}

	public File getProjectPath() {
		return this.project.getPath( );
	}

	public void openDocument( File documentFile ) {
		
		IDEDocument document;
		if ( documents.containsKey( documentFile ) ) {
			document = documents.get( documentFile );
		} else {
			// choose what sort of document to create
			document = documentFactory.createDocument( documentFile );
			// create a tab
			createTab( document );
			// store the document with its file
			documents.put( documentFile, document );
		}
		
		document.setFocus( );
	}
	
	private void createTab( IDEDocument document ) {
		tabs.put( document, new DocumentTab( this, document ) );
	}

	public void updateFiles( ) {
		project.updateTree( );
	}

	public void setDocumentFile( IDEDocument ideDocument, File file ) {
		if ( tabs.containsKey( ideDocument ) ) {
			
			// change the new file to map to the open document
			documents.remove( ideDocument.getFile( ) );
			documents.put( file, ideDocument );
			
			// set the tab name to the new file name
			tabs.get( ideDocument ).setText( file.getName( ) );
		}
	}

	public void setDocumentMenuEnabled( boolean enabled ) {
		project.getMenuBar( ).setDocumentEnabled( enabled );
	}

}
