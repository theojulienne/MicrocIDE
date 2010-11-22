package app.plugin;

import java.util.Arrays;
import java.util.List;

import plugins.documents.imageDocument.ImageDocumentPlugin;
import plugins.documents.sourceDocument.SourceDocumentPlugin;
import plugins.toolTabs.console.BuildConsolePlugin;


import app.plugin.interfaces.IPlugin;

public class PluginManager {
	public static List<IPlugin> listPlugins( ) {
		List<IPlugin> plugins = Arrays.asList( new IPlugin[] {
				new ImageDocumentPlugin( ),
				new SourceDocumentPlugin( ),
				new BuildConsolePlugin( )
			} );
		
		return plugins;
	}
}
