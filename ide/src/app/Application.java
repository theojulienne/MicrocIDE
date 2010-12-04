package app;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.*;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	private Preferences preferences = null;
	private static Display display;
	private AppSettingsDialog prefDialog; 
	
	private ImageManager imageManager = null;
	private PluginManager pluginManager = null;

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
	}
	
	/**
	 * Returns the ImageManager for this application
	 * @return an ImageManager
	 */
	public ImageManager getImageManager( ) {
		return imageManager;
	}
	
	private Application( ) {
		boolean showPrefs = false;
		preferences = new Preferences( display );
		try {
			pluginManager = new PluginManager( );
		} catch ( JSONException e ) {
			PluginManager.savePluginJSON( "Application", new File( Application.appSettingsFileName ), PluginManager.getFreshAppSettings( ) );
			try {
				pluginManager = new PluginManager( );
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			showPrefs = true;
		}
		imageManager = new ImageManager( display, pluginManager );
		instance = this;
		
		if ( showPrefs ) {
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
	
	public Preferences getPreferences( ) {
		return this.preferences;
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
		Application.getInstance( );
		
		ProjectWindow initialProject = new ProjectWindow( );
		initialProject.open( display, null );
		
		// main loop
		while ( !initialProject.isDisposed( ) ) {
			// while main window is open
			try {
				if ( !display.readAndDispatch( ) ) {
					display.sleep( );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		display.dispose( );
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}
}
