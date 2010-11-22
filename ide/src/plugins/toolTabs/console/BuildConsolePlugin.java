package plugins.toolTabs.console;

import java.io.File;

import app.plugin.base.IDEDocument;
import app.plugin.base.IDETool;
import app.plugin.interfaces.IDocumentParent;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IToolTabParent;

public class BuildConsolePlugin implements IPlugin {

	public String getPluginName() {
		return "Build Console";
	}

	public String getIconFilenameForExtension( String extension ) {
		return null;
	}

	public String[] getSupportedDocumentExtensions() {
		return null;
	}

	public IDEDocument createDocument( IDocumentParent parent, File file ) {
		return null;
	}

	public IDETool createTool( IToolTabParent parent ) {
		return new BuildConsole( parent );
	}

}
