package app;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.*;

import app.dialogs.PreferencesDialog;
import app.project.ProjectWindow;

public class Application {
	
	private static final String prefFile = "ide.properties";

	public static String projectFileName = "project.settings";
	
	private static Application instance = null;
	
	private PreferenceStore preferences;
	private Display display;
	
	private Font sourceFont; // Fonts tend not to garbage collect, keep one for all tabs
	private Font consoleFont;
	
	private String name;
	private PreferencesDialog prefDialog; 
	
	
	// TODO: move to Preferences.java
	public Color getColorPreference( String preference ) {
		int r = preferences.getInt( preference+".r" );
		int g = preferences.getInt( preference+".g" );
		int b = preferences.getInt( preference+".b" );
		
		return new Color( display, r, g, b );
	}
	
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
		
		File prefHandle = new File( prefFile );

		
		// TODO: move to Preferences.java
		preferences = new PreferenceStore( prefFile );


		try {
			prefHandle.createNewFile( );
			preferences.load( );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		preferences.setDefault( "syntax.keyword.r", 20 );
		preferences.setDefault( "syntax.keyword.g", 60 );
		preferences.setDefault( "syntax.keyword.b", 255 );

		preferences.setDefault( "syntax.keyword.font", SWT.BOLD );
		
		preferences.setDefault( "syntax.primtype.r", 84 );
		preferences.setDefault( "syntax.primtype.g", 0 );
		preferences.setDefault( "syntax.primtype.b", 192 );
		
		preferences.setDefault( "syntax.primtype.font", SWT.BOLD );
		
		preferences.setDefault( "syntax.typemod.r", 100 );
		preferences.setDefault( "syntax.typemod.g", 100 );
		preferences.setDefault( "syntax.typemod.b", 255 );
		
		preferences.setDefault( "syntax.directive.r", 0 );
		preferences.setDefault( "syntax.directive.g", 84 );
		preferences.setDefault( "syntax.directive.b", 0 );
		
		preferences.setDefault( "syntax.directive.font", SWT.BOLD );
		
		preferences.setDefault( "syntax.comment.r", 128 );
		preferences.setDefault( "syntax.comment.g", 128 );
		preferences.setDefault( "syntax.comment.b", 128 );
		
		preferences.setDefault( "syntax.string.r", 32 );
		preferences.setDefault( "syntax.string.g", 120 );
		preferences.setDefault( "syntax.string.b", 32 );
		
		preferences.setDefault( "syntax.char.r", 64 );
		preferences.setDefault( "syntax.char.g", 196 );
		preferences.setDefault( "syntax.char.b", 64 );
		
		preferences.setDefault( "syntax.number.r", 0 );
		preferences.setDefault( "syntax.number.g", 0 );
		preferences.setDefault( "syntax.number.b", 192 );
		
		preferences.setDefault( "syntax.operator.r", 196 );
		preferences.setDefault( "syntax.operator.g", 0 );
		preferences.setDefault( "syntax.operator.b", 0 );
		
		preferences.setDefault( "syntax.allcaps.r", 84 );
		preferences.setDefault( "syntax.allcaps.g", 84 );
		preferences.setDefault( "syntax.allcaps.b", 84 );

		preferences.setDefault( "syntax.allcaps.font", SWT.BOLD );
		
		preferences.setDefault( "syntax.grouping.r", 64 );
		preferences.setDefault( "syntax.grouping.g", 128 );
		preferences.setDefault( "syntax.grouping.b", 64 );
		
		preferences.setDefault( "syntax.default.r", 0 );
		preferences.setDefault( "syntax.default.g", 0 );
		preferences.setDefault( "syntax.default.b", 0 );


		if ( isMac( ) ) {
			preferences.setDefault( "source.font.face", "Monaco" );
			preferences.setDefault( "source.font.size", 12 );

			preferences.setDefault( "console.font.face", "Monaco" );
			preferences.setDefault( "console.font.size", 12 );
		} else {
			preferences.setDefault( "source.font.face", "monospace" );
			preferences.setDefault( "source.font.size", 12 );
			
			preferences.setDefault( "console.font.face", "monospace" );
			preferences.setDefault( "console.font.size", 12 );
		}
		

		String fontFace = preferences.getString( "source.font.face" );
		int fontSize = preferences.getInt( "source.font.size" );
		sourceFont = new Font( display, fontFace, fontSize, SWT.NONE );
		
		String cfontFace = preferences.getString( "source.font.face" );
		int cfontSize = preferences.getInt( "source.font.size" );
		consoleFont = new Font( display, cfontFace, cfontSize, SWT.NONE );
		
		try {
			preferences.save( );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		instance = this;
	}
	
	public Font getSourceFont( ) {
		return sourceFont;
	}
	
	public Font getConsoleFont( ) {
		return consoleFont;
	}
	
	public static Application getInstance( ) {
		if ( instance == null ) {
			instance = new Application( );
		}
		return instance;
	}
	
	public PreferenceStore getPreferenceStore( ) {
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
		String appName = "FloobWozzle";
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
