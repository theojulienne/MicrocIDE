package app.toolTabs;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import app.Application;

public class BuildConsoleTab extends CTabItem {

	private Font font;
	private StyledText console;
	private String consoleText;
	private ArrayList<StyleRange> ranges;
	private int latestStart, latestLength;
	private String latestLine;
	
	public BuildConsoleTab( CTabFolder parent ) {
		super( parent, SWT.BORDER );
		
		ranges = new ArrayList<StyleRange>( );
		
		this.setImage( new Image( parent.getDisplay(), "build.png" ) );
		this.setText( "Build Console" );
		
		console = new StyledText( parent, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP );
		console.setEditable( false );
		
		font = Application.getInstance().getConsoleFont( );
		console.setFont( font );
		consoleText = "";
		console.setText( consoleText );
		console.setMargins( 5, 5, 5, 5 );
		
		
		
		this.setControl( console );
	}
	
	public void addLine( String line ) {
		latestLine = line + "\n";		
		consoleText += latestLine;
		
		final Runnable updateText = new Runnable() {
			public void run() {
				console.append( latestLine );
				console.setSelection( consoleText.length() );
		    }
		};
		console.getDisplay( ).syncExec( updateText );
	}
	
	public void addColorLine( String line, Color color ) {
		
		int start = consoleText.length();
		int length = line.length( );
		
		latestLine = line + "\n";
		consoleText += latestLine;
		
		final Runnable updateText = new Runnable() {
			public void run() {
				console.append( latestLine );
		    }
		};
		
		console.getDisplay( ).syncExec( updateText );
		
		StyleRange style = new StyleRange();
		style.start = start;
		style.length = length;
		style.foreground = color;
		latestStart = start;
		latestLength = length;
		
		ranges.add( style );
		
		final Runnable updateStyle = new Runnable() {
			public void run() {
				console.replaceStyleRanges( latestStart, latestLength, (StyleRange[]) ranges.toArray( new StyleRange[ranges.size()] ) );
				console.setSelection( latestStart + latestLength + 1  );
		    }
		};
		
		console.getDisplay( ).syncExec( updateStyle );

	}
	
	public void addInfoLine( String line ) {
		Color infoCol = new Color( console.getDisplay( ), 0, 0, 230 );
		addColorLine( line, infoCol );
	}
	
	public void addErrLine( String line ) {
		Color errCol = new Color( console.getDisplay( ), 230, 0, 0 );
		addColorLine( line, errCol );
	}

}
