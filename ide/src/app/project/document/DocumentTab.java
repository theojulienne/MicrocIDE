package app.project.document;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;

public class DocumentTab extends CTabItem {
	private Image unsavedImage;
	private Image savedImage;
	private IDEDocument document;
	private DocumentTabFolder parent;
	
	public DocumentTab( DocumentTabFolder parent, IDEDocument document ) {
		super( parent, SWT.BORDER );
		
		this.parent = parent;
		this.unsavedImage = new Image( this.getDisplay(), "unsaved.gif" );
		this.savedImage = new Image( this.getDisplay(), "saved.gif" );
		
		setDocument( document );
	}
	
	/**
	 * Set the IDEDocument to display in this DocumentTab
	 * @param document The IDEDocument this tab displays
	 */
	public void setDocument( IDEDocument document ) {
		this.document = document;
		
		this.setText( document.getFile().getName() );
		
		if ( document.isSaved( ) ) {
			setSaved( );
		} else {
			setUnsaved( );
		}
		
		this.setControl( document.getControl( ) );
		
	}
	
	/**
	 * Get the IDEDocument this tab displays
	 * @return the displayed IDEDocument
	 */
	public IDEDocument getDocument( ) {
		return this.document;
	}
	
	/**
	 * Close this tab
	 * @return true iff the tab closed successfully
	 */
	public boolean close( ) {
		boolean closed = false;
		if ( document.close( ) ) {
			closed = true;
			parent.closeTab( this );
		}
		
		return closed;
	}
	
	/**
	 * Set this tab to be in a "saved" mode
	 */
	public void setSaved( ) {
		this.setImage( savedImage );
	}
	
	/**
	 * Set this tab to be in an "unsaved" mode
	 */
	public void setUnsaved( ) {
		this.setImage( unsavedImage );
	}
}
