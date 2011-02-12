package app.plugin.interfaces.parents;

import java.io.File;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONObject;

import app.plugin.base.PluginDocument;
import app.plugin.interfaces.IPlugin;

public interface IDocumentParent {
	void setSelection( PluginDocument ideDocument );
	Composite getComposite( );
	Shell getShell( );
	Device getDisplay( );
	
	// sets the status of the ideDocument as saved, according to this parent
	void setDocumentSaved( PluginDocument ideDocument, boolean isSaved );
	
	File getProjectPath( );
	
	void setDocumentMenuEnabled( boolean enabled );
	
	void openDocument( File documentFile );
	void updateFiles( );
	void setDocumentFile( PluginDocument ideDocument, File file );
	
	JSONObject getPluginProjectSettings( IPlugin plugin );
	JSONObject getPluginAppSettings( IPlugin plugin );
}
