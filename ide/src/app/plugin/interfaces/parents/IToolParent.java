package app.plugin.interfaces.parents;

import java.io.File;
import java.util.Collection;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONObject;

import app.plugin.base.PluginTool;
import app.plugin.interfaces.IMenuBar;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IToolBar;

public interface IToolParent {
	Composite getComposite( );
	Shell getShell( );
	Device getDisplay( );
	
	File getProjectPath( );
	
	void openDocument( File documentFile );
	void updateFiles( );
	
	void updateIcon( PluginTool tool );
	
	IToolBar getToolBar( );
	IMenuBar getMenuBar( );
	
	Collection<PluginTool> getTools( );
	
	JSONObject getPluginProjectSettings( IPlugin plugin );
	JSONObject getPluginAppSettings( IPlugin plugin );
}
