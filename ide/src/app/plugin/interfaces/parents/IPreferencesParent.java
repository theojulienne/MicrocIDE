package app.plugin.interfaces.parents;

import org.eclipse.swt.widgets.Composite;
import org.json.JSONObject;

import app.plugin.interfaces.IPlugin;


public interface IPreferencesParent {
	Composite getComposite( );

	JSONObject getPluginProjectSettings( IPlugin plugin );
	JSONObject getPluginAppSettings( IPlugin plugin );

	void savePluginProjectSettings( IPlugin plugin, JSONObject newSettings );
	void savePluginAppSettings( IPlugin plugin, JSONObject newSettings );
}
