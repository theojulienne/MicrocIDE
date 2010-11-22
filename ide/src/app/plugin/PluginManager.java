package app.plugin;

import java.util.Arrays;
import java.util.List;

import app.project.document.imageDocument.ImageDocumentPlugin;
import app.project.document.sourceDocument.SourceDocumentPlugin;

public class PluginManager {
	public static List<IDEPlugin> listPlugins( ) {
		List<IDEPlugin> plugins = Arrays.asList( new IDEPlugin[] {
				new ImageDocumentPlugin( ),
				new SourceDocumentPlugin( )
			} );
		
		return plugins;
	}
}
