package app.plugin.interfaces;

import java.io.File;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import app.plugin.base.IDEDocument;

public interface IDocumentParent {
	void setSelection( IDEDocument ideDocument );
	Composite getComposite( );
	Shell getShell( );
	Device getDisplay( );
	
	// sets the status of the ideDocument as saved, according to this parent
	void setDocumentSaved( IDEDocument ideDocument, boolean isSaved );
	
	File getProjectPath( );
	
	void setDocumentMenuEnabled( boolean enabled );
	
	void openDocument( File documentFile );
	void updateFiles( );
	void setDocumentFile( IDEDocument ideDocument, File file );
}
