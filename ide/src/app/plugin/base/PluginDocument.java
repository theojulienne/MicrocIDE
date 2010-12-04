package app.plugin.base;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Control;

import common.FileIO;

import app.plugin.interfaces.parents.IDocumentParent;


public abstract class PluginDocument {
	private boolean isSaved;
	
	protected String lastSavedData;
	protected File file;
	protected IDocumentParent parent;
	
	public PluginDocument( IDocumentParent parent, File file ) {
		this.parent = parent;
		this.file   = file;
		this.isSaved = true;
	}
	
	protected void setMenuEnabled( boolean enabled ) {
		this.parent.setDocumentMenuEnabled( enabled );
	}
	
	protected void checkFileValidity() {
		String currentData = FileIO.readFile( this.file );
		if ( !currentData.equals( this.lastSavedData ) ) {			
			promptFileChanged( );
		}
	}
	
	protected void setSaved( boolean isSaved ) {
		this.isSaved = isSaved;
		parent.setDocumentSaved( this, isSaved );
	}
	
	// Private Helpers
	private void promptFileChanged( ) {
		parent.setSelection( this );
		boolean canUpdate = MessageDialog.openConfirm( parent.getShell( ), "File Changed", "This file has been changed externally.\nUpdate this file to the new version?\n(This will lose unsaved changes)" );
		
		if ( canUpdate ) {
			setFile( this.file );
		}
	}
	
	// To implement generic document operations
	public boolean save( ) {
		return true;
	}
	
	public boolean saveAs( ) {
		return true;
	}
	
	public boolean close( ) {
		boolean canProceed = true;
		
		if ( !isSaved( ) ) {
			parent.setSelection( this );
			parent.getShell( ).setActive( );
			
			canProceed = MessageDialog.openConfirm( parent.getShell( ), "Unsaved File", "There are unsaved changes in file: " + file.getName() + "\nDo you want to continue? (Changes will be lost)" );
		}

		return canProceed;
	}
	
	public void undo( ) {
		
	}
	
	public void redo( ) {
		
	}
	
	public void cut( ) {
		
	}
	
	public void copy( ) {
		
	}
	
	public void paste( ) {
		
	}
	
	public void selectAll( ) {
		
	}
	
	public void find( ) {
		
	}
	
	public void findNext( ) {
		
	}
	
	public void findPrev( ) {
		
	}
	
	// To implement File operations
	public File getFile( ) {
		return this.file;
	}
	
	public void setFile( File file ) {
		this.file = file;
	}
	
	public void rename( File file ) {
		this.file = file;
		parent.setDocumentFile( this, file );
	}
	
	// returns true iff the document is saved
	public boolean isSaved( ) {
		return this.isSaved;
	}
	
	// Implement to set focus to the main widget
	public void setFocus( ) {
		parent.setSelection( this );
	}
	
	// Implement action to take when this document is selected
	public void selected( ) {
	}
	
	public Control getControl( ) {
		return null;
	}

	public Control getMainWidget( ) {
		return getControl( );
	}
	
	public static String[] getAssociatedExtensions( ) {
		return new String[] { };
	}
}
