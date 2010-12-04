package app.plugin.base;


import org.eclipse.swt.widgets.Composite;

import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

public abstract class PluginAppPreferences {
	protected Composite widgetParent;
	protected IPlugin plugin;
	public PluginAppPreferences( IPlugin plugin, IPreferencesParent parent ) {
		widgetParent = parent.getComposite( );
		this.plugin = plugin;
	}
}
