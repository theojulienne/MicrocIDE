package plugins.documents.sourceDocument;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.json.JSONException;
import org.json.JSONObject;

import app.plugin.base.PluginAppPreferences;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.parents.IPreferencesParent;

public class SourceDocumentPreferences extends PluginAppPreferences {
	protected Label demoFontText;
	protected Composite settingsArea;
	
	public SourceDocumentPreferences( IPlugin plugin, IPreferencesParent parent ) {
		super( plugin, parent );
	}

	protected void createContents() {
		settingsArea = new Composite( widgetParent, SWT.NONE );
		settingsArea.setLayout( new GridLayout( 1, false ) );
		
		Group fontGroup = new Group( settingsArea, SWT.BORDER );
		fontGroup.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		fontGroup.setLayout( new GridLayout( 3, false ) );
		
		JSONObject editorFont;

		FontData fontData = null;
		String fontFace = "Default";
		

		try {
			if ( !settings.has( "Editor Font")  ) {
				editorFont = new JSONObject(  );
				
				if ( parent.isMac() ) {
					editorFont.put( "Font Face", "Monaco" );
				} else {
					editorFont.put( "Font Face", "Courier" );
				}
				editorFont.put( "Font Size", 12 );
				settings.put( "Editor Font", editorFont );
			} else {
				editorFont = settings.getJSONObject( "Editor Font" );
			}
			
			fontFace = editorFont.getString( "Font Face" );
			int fontHeight = editorFont.getInt( "Font Size" );
			fontData = new FontData( fontFace, fontHeight, SWT.NONE );
			
		} catch ( JSONException e ) {
			e.printStackTrace( );
		}
		
		new Label( fontGroup, SWT.LEFT ).setText( "Editor Font: " );
		
		GridData fontLayoutData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
		fontLayoutData.minimumWidth = 150;
		
		demoFontText = new Label( fontGroup, SWT.CENTER );
		demoFontText.setFont( new Font( widgetParent.getDisplay(), fontData ) );
		demoFontText.setText( fontFace );
		demoFontText.setLayoutData( fontLayoutData );
		demoFontText.pack( );
		
		Button changeFontButton = new Button( fontGroup, SWT.PUSH );
		changeFontButton.setText( "Change Font" );
		changeFontButton.addSelectionListener( new SelectionListener() {
			public void widgetSelected( SelectionEvent e ) {

		        try {
		        	FontDialog fd = new FontDialog( widgetParent.getShell( ), SWT.NONE );
		        	fd.setText( "Select Font" );
		        
		        	JSONObject currentFont = settings.getJSONObject( "Editor Font" );
		        	String fontFace = currentFont.getString( "Font Face" );
		        	int fontHeight = currentFont.getInt( "Font Size" );
				
		        	FontData currentFontData = new FontData( fontFace, fontHeight, SWT.BOLD );
		        	fd.setFontList( new FontData[] { currentFontData } );
		        	FontData newFont = fd.open();
		        	if (newFont == null) {
		        		return;
		        	}
		        	demoFontText.setFont( new Font( widgetParent.getDisplay( ), newFont ) );
		        	demoFontText.setText( newFont.getName() );
		        	demoFontText.pack( );
		        	
		        	settings.getJSONObject( "Editor Font" ).put( "Font Face", newFont.getName( ) );
		        	settings.getJSONObject( "Editor Font" ).put( "Font Size", newFont.getHeight( ) );
		        	
		        	saveSettings( );
		        	
		        } catch ( JSONException ex ) {
		        	MessageDialog.openError( widgetParent.getShell( ), "Problem loading fonts", "Unable to load font settings" );
		        }
			}
			public void widgetDefaultSelected( SelectionEvent e ) {
			}
		} );
		
		Group syntaxColourGroup = new Group( settingsArea, SWT.BORDER );
		syntaxColourGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
		syntaxColourGroup.setLayout( new FillLayout( ) );
		
		JSONObject syntaxColourSettings;
		try {
			syntaxColourSettings = settings.getJSONObject( "Syntax Highlighting" );
		} catch ( JSONException e1 ) {
			syntaxColourSettings = createDefaultSyntaxHighlighting( );
			
			try {
				settings.put( "Syntax Highlighting", syntaxColourSettings );
			} catch ( JSONException e2 ) {
				e2.printStackTrace();
			}
		}
		
		ScrolledComposite scroller = new ScrolledComposite( syntaxColourGroup, SWT.V_SCROLL | SWT.BORDER );
		
		Composite scrollArea = new Composite( scroller, SWT.NONE );
		scrollArea.setLayout( new GridLayout( 3, true ) );
		
		Iterator<String> keyIterator = syntaxColourSettings.keys();
		for ( String key = keyIterator.next(); keyIterator.hasNext( ); key = keyIterator.next() ) {
			
			
			Label demoLabel = new Label( scrollArea, SWT.LEFT );
			
			GridData demoLabelData = new GridData( SWT.FILL, SWT.CENTER, true, true );
			demoLabelData.grabExcessHorizontalSpace = true;
			demoLabelData.horizontalAlignment = SWT.FILL;
			demoLabel.setLayoutData( demoLabelData );
			demoLabel.setText( key );
			
			Button colourButton = new Button( scrollArea, SWT.PUSH );
			colourButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
			colourButton.setText( "Change" );
			colourButton.setData( demoLabel );
			
			Button boldButton   = new Button( scrollArea, SWT.CHECK );
			boldButton.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
			boldButton.setText( "Bold" );
			
			JSONObject currentSettings;
			
			try {
				currentSettings = syntaxColourSettings.getJSONObject( key );
				int red = currentSettings.getInt( "R" );
				int green = currentSettings.getInt( "G" );
				int blue = currentSettings.getInt( "B" );
				boolean isBold = currentSettings.getBoolean( "Bold" );
				
				boldButton.setSelection( isBold );
				boldButton.setData( demoLabel );
				
				int style = SWT.NONE;
				if ( isBold ) {
					style = SWT.BOLD;
				}
				
				String demoFontFace = settings.getJSONObject( "Editor Font" ).getString( "Font Face" );
				
				demoLabel.setFont( new Font( widgetParent.getDisplay(), demoFontFace, 12, style ) );
				demoLabel.setForeground( new Color( widgetParent.getDisplay(), red, green, blue ) );
			} catch ( JSONException e ) {
				e.printStackTrace( );
			}
			
			boldButton.addSelectionListener( new SelectionListener() {
				public void widgetSelected( SelectionEvent evt ) {
					
					Button boldButton = (Button)evt.widget;
					Label demoText    = (Label)boldButton.getData( );
					String key        = demoText.getText( );
					
					Font font = demoText.getFont();
					FontData fd = font.getFontData()[0];
					
					try {
						if ( boldButton.getSelection( ) ) {
							fd.setStyle( SWT.BOLD );
	
							settings.getJSONObject( "Syntax Highlighting" ).getJSONObject( key ).put( "Bold", true );
						} else {
							fd.setStyle( SWT.NONE );
							
							settings.getJSONObject( "Syntax Highlighting" ).getJSONObject( key ).put( "Bold", false );
						}
						
						saveSettings( );
					} catch ( JSONException e ) {
						e.printStackTrace( );
					}
					
					demoText.setFont( new Font( boldButton.getDisplay(), fd ) );
				}
				
				public void widgetDefaultSelected( SelectionEvent evt ) {
				}
			} );
			
			colourButton.addSelectionListener( new SelectionListener() {
				public void widgetSelected( SelectionEvent evt ) {

					Button colourButton = (Button)evt.widget;
					Label demoText      = (Label)colourButton.getData( );
					String key          = demoText.getText( );
					
					
					try {
						int r, g, b;
						ColorDialog cd = new ColorDialog( colourButton.getShell( ) );
				        cd.setText("Change Highlighting");
				        
				        RGB oldColour = demoText.getForeground( ).getRGB( );
				        
				        cd.setRGB( oldColour );
				        RGB newColour = cd.open();
						
				        demoText.setForeground( new Color( demoText.getDisplay(), newColour ) );
				        
				        r = newColour.red;
				        g = newColour.green;
				        b = newColour.blue;
				        
						settings.getJSONObject( "Syntax Highlighting" ).getJSONObject( key ).put( "R", r );
						settings.getJSONObject( "Syntax Highlighting" ).getJSONObject( key ).put( "G", g );
						settings.getJSONObject( "Syntax Highlighting" ).getJSONObject( key ).put( "B", b );
						
						saveSettings( );
					} catch ( JSONException e ) {
						e.printStackTrace( );
					}
					
				}
				
				public void widgetDefaultSelected( SelectionEvent evt ) {
				}
			} );
			
		}
		
		scroller.setContent( scrollArea );

		scrollArea.pack();
		
		
		Button resetButton = new Button( settingsArea, SWT.PUSH );
		resetButton.setText( "Set Highlighting to Defaults" );

		resetButton.addSelectionListener( new SelectionListener() {
			public void widgetSelected( SelectionEvent evt ) {
				try {
					settings.put( "Syntax Highlighting", createDefaultSyntaxHighlighting( ) );
					saveSettings( );
					
					settingsArea.dispose( );
					widgetParent.layout( );
					createContents( );
					widgetParent.layout( );
				} catch ( JSONException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		} );
		
	}

	private static JSONObject createJSONSyntaxSetting( int r, int g, int b, boolean isBold ) {
		JSONObject syntaxSetting = new JSONObject( );
		
		try {
			syntaxSetting.put( "R", r );
			syntaxSetting.put( "G", g );
			syntaxSetting.put( "B", b );
			syntaxSetting.put( "Bold", isBold );
		} catch ( JSONException e ) {
			e.printStackTrace( );
		}
		
		return syntaxSetting;
	}
	
	public static JSONObject createDefaultSyntaxHighlighting( ) {
		JSONObject newSettings = new JSONObject( );
		
		try {
			newSettings.put( "Keyword", 		createJSONSyntaxSetting(  20,  60, 255, true  ) );
			newSettings.put( "Primitive Type", 	createJSONSyntaxSetting(  84,   0, 192, true  ) );
			newSettings.put( "Type Qualifier", createJSONSyntaxSetting( 100, 100, 255, false ) );
			newSettings.put( "Comment", 		createJSONSyntaxSetting( 128, 128, 128, false ) );
			newSettings.put( "Doc Comment", 	createJSONSyntaxSetting( 128, 128, 196, false ) );
			newSettings.put( "String Literal",	createJSONSyntaxSetting(  32, 120,  32, false ) );
			newSettings.put( "Character Literal", createJSONSyntaxSetting( 64, 196, 64, false ) );
			newSettings.put( "Number", 			createJSONSyntaxSetting(   0,   0, 192, false ) );
			newSettings.put( "Operator",		createJSONSyntaxSetting( 196,   0,   0, false ) );
			newSettings.put( "All Caps",		createJSONSyntaxSetting(  84,  84,  84, true  ) );
			newSettings.put( "Grouping", 		createJSONSyntaxSetting(  64, 128,  64, false ) );			
			newSettings.put( "Directive", 		createJSONSyntaxSetting(   0,  84,   0, true  ) );
			newSettings.put( "Directive String",createJSONSyntaxSetting(  64, 128,  64, false ) );
			newSettings.put( "Directive Angle Bracketed", createJSONSyntaxSetting(  32, 128,  32, false ) );		
			newSettings.put( "Directive Text",	createJSONSyntaxSetting(  64,  84,  64, false ) );
			newSettings.put( "Default Text",	createJSONSyntaxSetting(   0,   0,   0, false ) );
			newSettings.put( "Identifier",		createJSONSyntaxSetting(   0,   0,   0, false ) );
			newSettings.put( "String Escaped",	createJSONSyntaxSetting( 127,  63,   63, true ) );
			newSettings.put( "String Formatting", createJSONSyntaxSetting( 212,  63,   63, true ) );
		} catch ( JSONException e ) {
			e.printStackTrace( );
		}
		
		return newSettings;
	}


}
