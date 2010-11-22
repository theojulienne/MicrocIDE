package app.project.document;

import java.io.File;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public interface IDocumentParent {
	public void setSelection( IDEDocument ideDocument );
	public Composite getComposite( );
	public Shell getShell( );
	public Device getDisplay( );
	
	// sets the status of the ideDocument as saved, according to this parent
	public void setDocumentSaved( IDEDocument ideDocument, boolean isSaved );
	
	public File getProjectPath( );
	
	public void setDocumentMenuEnabled( boolean enabled );
	
	public void openDocument( File documentFile );
	public void updateFiles( );
	public void setDocumentFile( IDEDocument ideDocument, File file );
}
