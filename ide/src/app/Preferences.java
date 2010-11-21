package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class Preferences {

	private PreferenceStore preferences;
	private Display display;
	
	private Font sourceFont; // Fonts tend not to garbage collect, keep one for all tabs
	private Font consoleFont;
	
	public Color getColor( String preference ) {
		int r = preferences.getInt( preference+".r" );
		int g = preferences.getInt( preference+".g" );
		int b = preferences.getInt( preference+".b" );
		
		return new Color( display, r, g, b );
	}
	
	public void setValue( String key, int value ) {
		preferences.setValue( key, value );
	}
	
	public void setValue( String key, String value ) {
		preferences.setValue( key, value );
	}
	
	public void setFont( String key, FontData fd ) {
		preferences.setValue( key + ".font.face", fd.getName() );
		preferences.setValue( key + ".font.size", fd.getHeight() );
		preferences.setValue( key + ".font.style", fd.getStyle() );
	}
	
	public void setList( String key, String delim, Collection<String> list ) {
		StringBuffer buffer = new StringBuffer( );
        Iterator<String> iter = list.iterator( );
        while ( iter.hasNext( ) ) {
            buffer.append( iter.next( ) );
            if ( iter.hasNext( ) ) {
                buffer.append( delim );
            }
        }
        
        preferences.setValue( key, buffer.toString( ) );
	}
	
	public ArrayList<String> getList( String key, String delim ) {
		String listString = preferences.getString( key );
		if ( listString == "" ) { // treat empty string as empty list
			return new ArrayList<String>();
		}
		
		String[] list = listString.split( delim );
		ArrayList<String> arrayList = new ArrayList<String>( Arrays.asList( list ) );
		return arrayList;
	}
	
	public ArrayList<String> getRecentList( ) {
		return getList( "recentList", "," );
	}
	
	public boolean contains( String key ) {
		return preferences.contains( key );
	}
	
	public void setRecentList( Collection<String> list ) {
		setList( "recentList", ",", list );
	}
	
	public void save( ) {
		String fontFace = preferences.getString( "source.font.face" );
		int fontSize = preferences.getInt( "source.font.size" );
		sourceFont = new Font( display, fontFace, fontSize, SWT.NONE );
		
		String cfontFace = preferences.getString( "source.font.face" );
		int cfontSize = preferences.getInt( "source.font.size" );
		consoleFont = new Font( display, cfontFace, cfontSize, SWT.NONE );
		
		try {
			preferences.save( );
		} catch (IOException e) {
			MessageDialog.openError( display.getActiveShell(), "Preferences Error", "Unable to save preferences file." );
		}
	}
	
	
	public String getString( String key ) {
		return preferences.getString( key );
	}
	
	public int getInt( String key ) {
		return preferences.getInt( key );
	}
	
	public Font getSourceFont( ) {
		return sourceFont;
	}
	
	public Font getConsoleFont( ) {
		return consoleFont;
	}
	
	public Preferences( Display display ) {
		this.display = display;

		File prefHandle = new File( Application.idePreferenceFile );

		preferences = new PreferenceStore( Application.idePreferenceFile );


		try {
			prefHandle.createNewFile( );
			preferences.load( );
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError( display.getActiveShell(), "Preferences Error", "Unable to load preferences file." );
		}
		
		preferences.setDefault( "recentList", "" );
		
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


		if ( Application.isMac( ) ) {
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

		save( );
	}
}
