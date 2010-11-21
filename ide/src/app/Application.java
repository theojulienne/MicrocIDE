package app;

import org.eclipse.swt.widgets.*;

import app.dialogs.PreferencesDialog;
import app.project.ProjectWindow;

public class Application {
	
	public static String prefFile = "ide.properties";
	public static String appName = "FloobWozzle";
	public static String version = "0.1 Alpha Release";
	public static String aboutString = appName + " " + version + "\n" + "IDE for embedded development\n2010 Icy Labs http://www.icy.com.au/";
	public static String projectFileName = "project.settings";
	public static String projectSettingsFile = "project_settings.json";
	
	private static Application instance = null;
	
	private Preferences preferences;
	private Display display;
	
	
	private String name;
	private PreferencesDialog prefDialog; 

	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName( ) {
		return this.name;
	}
	
	public static boolean isMac( ) {
        if ( System.getProperty( "os.name" ).equals( "Mac OS X" ) ) {
            return true;
        }
        return false;
    }
	
	public void showPreferences( ) {
		if ( prefDialog == null ) {
			prefDialog = new PreferencesDialog( display.getActiveShell() );
		}
		
		prefDialog.open( );
	}
	
	private Application( ) {
		
		preferences = new Preferences( display );
		
		instance = this;
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
	
	public void setDisplay( Display display ) {
		this.display = display;
	}
	
	public Display getDisplay( Display display ) {
		return this.display;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Q: Why is the app called FloobWozzle?
		// A: It went a little like... 
		//    "Hey Mitch, what should I call this IDE?" 
		//    "Err... I dunno, FloobWozzle?" 
		//    "Done!"
		
		Display.setAppName( appName );
		
		Display display = new Display( );
		
		Application app = Application.getInstance( );
		app.setDisplay( display );
		app.setName( appName );
		
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
}
