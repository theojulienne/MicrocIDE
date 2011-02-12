package app;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.*;
import org.json.JSONException;

import app.dialogs.AppSettingsDialog;
import app.plugin.PluginManager;
import app.project.ProjectWindow;

public class Application {
	
	public static String idePreferenceFile = "ide.properties";
	public static String appName = "MicrocIDE";
	public static String version = "0.1 Alpha Release";
	public static String aboutString = appName + " " + version + "\n" + "IDE for embedded development\n2010 Icy Labs http://www.icy.com.au/";
	
	public static String projectSettingsFileName = "project.settings";
	public static String appSettingsFileName     = "app.settings";
	
	private static Application instance = null;
	
	private static Display display;
	private AppSettingsDialog prefDialog; 
	
	private ImageManager imageManager = null;
	private PluginManager pluginManager = null;
	
	private ArrayList<ProjectWindow> projectWindows;

	/**
	 * Is this application running on a Mac?
	 * @return true if os.name == "Mac OS X"
	 */
	public static boolean isMac( ) {
        if ( System.getProperty( "os.name" ).equals( "Mac OS X" ) ) {
            return true;
        }
        return false;
    }
	
	/**
	 * Creates and shows a preferences dialog for the application
	 */
	public void showPreferences( ) {
		if ( prefDialog == null ) {
			prefDialog = new AppSettingsDialog( display.getActiveShell() );
		}
		
		prefDialog.open( );
		

		for ( ProjectWindow projectWindow : projectWindows ) {
			projectWindow.updateTree( );
			projectWindow.updateSettings( );
		}
	}
	
	/**
	 * Returns the ImageManager for this application
	 * @return an ImageManager
	 */
	public ImageManager getImageManager( ) {
		return imageManager;
	}
	
	private Application( ) {
		projectWindows = new ArrayList<ProjectWindow>( );
		
		boolean showPrefsWarning = false;
		try {
			pluginManager = new PluginManager( );
		} catch ( JSONException e ) {
			PluginManager.savePluginJSON( "Application", new File( Application.appSettingsFileName ), null );
			try {
				pluginManager = new PluginManager( );
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			showPrefsWarning = true;
		}
		imageManager = new ImageManager( display, pluginManager );
		instance = this;
		
		if ( showPrefsWarning ) {
			MessageDialog.openInformation( display.getActiveShell(), "Application Settings",
					"The application's settings and loaded extensions need to be reviewed." );
		}
	}
	
	public static Application getInstance( ) {
		if ( instance == null ) {
			instance = new Application( );
		}
		return instance;
	}
	
	public static void setDisplay( Display display ) {
		Application.display = display;
	}
	
	public static Display getDisplay( ) {
		return display;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Display.setAppName( appName );
		
		Display display = new Display( );

		Application.setDisplay( display );
		
		// init
		Application.getInstance( ).run( );
		
		display.dispose( );
	}

	public ProjectWindow openNewProjectWindow( ) {
		ProjectWindow newWindow = new ProjectWindow( );
		projectWindows.add( newWindow );
		newWindow.open( display, null );
		
		return newWindow;
	}
	
	private void run() {
		ProjectWindow masterWindow = openNewProjectWindow( );

		while ( masterWindow != null ) {
			// while a window is open
			try {
				if ( !display.readAndDispatch( ) ) {
					display.sleep( );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// reallocate master status to an un-disposed window
			if ( masterWindow.isDisposed( ) ) {
				masterWindow = null;
				for ( ProjectWindow window : projectWindows ) {
					if ( !window.isDisposed( ) ) {
						masterWindow = window;
						break;
					}
				}
			}
		}
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}
}
