package app.plugin.interfaces;

import java.io.File;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import app.plugin.base.IDETool;

public interface IToolTabParent {
	Composite getComposite( );
	Shell getShell( );
	Device getDisplay( );
	
	File getProjectPath( );
	
	void openDocument( File documentFile );
	void updateFiles( );
	
	void updateIcon( IDETool tool );
	
	IToolBar getToolBar( );
	IMenuBar getMenuBar( );
}
