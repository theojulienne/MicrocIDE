package app.dialogs;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.json.JSONObject;

import app.Application;
import app.plugin.PluginManager;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;
import app.project.ProjectWindow;

public class PluginPreferencePane extends Composite implements IPreferencesParent {

	private ProjectWindow project;

	public PluginPreferencePane( ProjectWindow project, Composite parent, int style ) {
		super( parent, style );
		this.setLayout( new FillLayout( ) );
		this.project = project;
	}

	public Composite getComposite( ) {
		return this;
	}

	public JSONObject getPluginProjectSettings( IPlugin plugin ) {
		if ( project != null ) {
			return PluginManager.getPluginProjectSettings( project.getPath(), plugin );
		} else {
			return null;
		}
	}

	public JSONObject getPluginAppSettings( IPlugin plugin ) {
		return Application.getInstance().getPluginManager().getPluginAppSettings( plugin );
	}
	

	public void savePluginProjectSettings( IPlugin plugin, JSONObject newSettings ) {
		if ( project != null ) {
			Application.getInstance().getPluginManager().savePluginProjectSettings( project.getPath(), plugin, newSettings );
		}
	}

	public void savePluginAppSettings( IPlugin plugin, JSONObject newSettings ) {
		Application.getInstance().getPluginManager().savePluginAppSettings( plugin, newSettings );
	}

	
}
