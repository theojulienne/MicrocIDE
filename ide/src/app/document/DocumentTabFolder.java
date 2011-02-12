package app.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.widgets.Composite;
import org.json.JSONObject;

import app.plugin.PluginManager;
import app.plugin.base.PluginDocument;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IDocumentParent;
import app.project.ProjectWindow;

public class DocumentTabFolder extends CTabFolder implements IDocumentParent {

	private HashMap<File, PluginDocument> documents;
	private HashMap<PluginDocument, DocumentTab> tabs;
	private ProjectWindow project;
	private DocumentFactory documentFactory;
	
	public DocumentTabFolder( Composite parent, int style ) {
		super( parent, style );
		
		this.documents = new HashMap<File, PluginDocument>( );
		this.tabs = new HashMap<PluginDocument, DocumentTab>( );
		
		this.setBorderVisible( true );
		
		// appears on mouse-over
		this.setUnselectedCloseVisible( true );
		
		// makes them less ugly
		this.setSimple( true );
		
		documentFactory = new DocumentFactory( this );
		
		addListeners( );
		
		this.setSelection( 0 );
	}
	
	private void addListeners( ) {
		this.addCTabFolder2Listener( new CTabFolder2Listener( ) {
			public void close(CTabFolderEvent event) {
				DocumentTab tab = (DocumentTab)event.item;
				if ( !tab.close( ) ) {
					event.doit = false;
				}
			}
			public void maximize(CTabFolderEvent event) {
			}
			public void minimize(CTabFolderEvent event) {
			}
			public void restore(CTabFolderEvent event) {
			}
			public void showList(CTabFolderEvent event) {
			}
			
		} );
	}
	
	public DocumentTab[] getDocumentTabs( ) {
		Collection<DocumentTab> tabCollection = this.tabs.values();
		return tabCollection.toArray( new DocumentTab[tabCollection.size()] );
	}
	
	public void renameDocumentTab( File fromFile, File toFile ) {
		if ( documents.containsKey( fromFile ) ) {
			PluginDocument doc = documents.get( fromFile );
			doc.rename( toFile );
		}
	}
	
	public void closeTab( DocumentTab tab ) {
		// clean up references to the tab
		tabs.remove( tab.getDocument() );
		documents.remove( tab.getDocument().getFile() );
		tab.dispose( );
		
		if ( this.getItemCount() == 0 ) {
			project.getMenuBar( ).setTabEnabled( false );
		}
	}
	
	public boolean closeAllTabs( ) {
		boolean succeeded = true;
		for ( DocumentTab tab : this.getDocumentTabs() ) {
			if ( !tab.close( ) ) {
				succeeded = false;
				break;
			}
		}
		
		if ( succeeded ) {
			project.getMenuBar( ).setTabEnabled( false );
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

	public void setSelection( PluginDocument ideDocument ) {
		if ( tabs.containsKey( ideDocument ) ) {
			DocumentTab tabItem = tabs.get( ideDocument );
			this.setSelection( tabItem );
		}
	}

	public Composite getComposite( ) {
		return this;
	}

	public void setDocumentSaved( PluginDocument ideDocument, boolean isSaved ) {
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
		
		PluginDocument document;
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
	
	private void createTab( PluginDocument document ) {
		tabs.put( document, new DocumentTab( this, document ) );
		project.getMenuBar( ).setTabEnabled( true );
	}

	public void updateFiles( ) {
		project.updateTree( );
	}

	public void setDocumentFile( PluginDocument ideDocument, File file ) {
		if ( tabs.containsKey( ideDocument ) ) {
			
			// change the new file to map to the open document
			documents.remove( ideDocument.getFile( ) );
			documents.put( file, ideDocument );
			
			// set the tab name to the new file name
			tabs.get( ideDocument ).setText( file.getName( ) );
		}
	}

	public void setDocumentMenuEnabled( boolean enabled ) {
		project.setDocumentEnabled( enabled );
	}

	public JSONObject getPluginProjectSettings( IPlugin plugin ) {
		return PluginManager.getPluginProjectSettings( project.getPath(), plugin );
	}

	public JSONObject getPluginAppSettings(IPlugin plugin) {
		return PluginManager.getPluginAppSettings( plugin );
	}
	
}
