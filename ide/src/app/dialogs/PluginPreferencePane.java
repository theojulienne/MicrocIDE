package app.dialogs;

import java.io.File;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.json.JSONObject;

import app.Application;
import app.plugin.PluginManager;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

public class PluginPreferencePane extends Composite implements IPreferencesParent {

	private File projectPath;

	public PluginPreferencePane( File projectPath, Composite parent, int style ) {
		super( parent, style );
		this.setLayout( new FillLayout( ) );
		this.projectPath = projectPath;
	}

	public Composite getComposite( ) {
		return this;
	}

	public JSONObject getPluginProjectSettings( IPlugin plugin ) {
		if ( projectPath != null ) {
			return PluginManager.getPluginProjectSettings( projectPath, plugin );
		} else {
			return null;
		}
	}

	public JSONObject getPluginAppSettings( IPlugin plugin ) {
		return PluginManager.getPluginAppSettings( plugin );
	}
	

	public void savePluginProjectSettings( IPlugin plugin, JSONObject newSettings ) {
		if ( projectPath != null ) {
			PluginManager.savePluginProjectSettings( projectPath, plugin, newSettings );
		}
	}

	public void savePluginAppSettings( IPlugin plugin, JSONObject newSettings ) {
		PluginManager.savePluginAppSettings( plugin, newSettings );
	}
	
	public boolean isMac( ) {
		return Application.isMac( );
	}

	
}
