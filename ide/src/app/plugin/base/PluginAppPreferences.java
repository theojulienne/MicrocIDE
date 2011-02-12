package app.plugin.base;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.json.JSONObject;

import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

public abstract class PluginAppPreferences {
	protected Composite widgetParent;
	protected IPlugin plugin;
	protected JSONObject settings;
	protected IPreferencesParent parent;
	
	public PluginAppPreferences( IPlugin plugin, IPreferencesParent parent ) {
		this.widgetParent = parent.getComposite( );
		this.parent = parent;
		this.settings = parent.getPluginAppSettings( plugin );
		this.plugin = plugin;
		createContents( );
	}
	
	
	protected void createContents() {
		new Label( widgetParent, SWT.LEFT ).setText( "No preference panel created" );
	}


	protected void saveSettings( ) {
		parent.savePluginAppSettings( plugin, this.settings );
	}
}
