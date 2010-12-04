package app.plugin.base;


import org.eclipse.swt.widgets.Composite;

import app.plugin.interfaces.parents.IPreferencesParent;

public abstract class PluginProjectPreferences {
	protected Composite widgetParent;
	public PluginProjectPreferences( IPreferencesParent parent ) {
		widgetParent = parent.getComposite();
	}
}
