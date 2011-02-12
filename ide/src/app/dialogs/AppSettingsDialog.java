package app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.Application;
import app.plugin.PluginManager;
import app.plugin.interfaces.IPlugin;

public class AppSettingsDialog extends Dialog {
	public static final String SINGLE_CLICK = "Single Click to Open";
	public static final String SHOW_RECENT = "Show Recent List";
	public static final String TOOLBAR = "Toolbar Placement";
	
	private static final String appMenuItemText = "Application";
	
	private Shell shell;
	private Composite currentPane = null;
	private Composite pluginSettings;
	
	public AppSettingsDialog( Shell parentShell ) {
		super( parentShell, SWT.DIALOG_TRIM | SWT.RESIZE );
		parentShell.addShellListener( new ShellListener( ) {

			public void shellClosed( ShellEvent evt ) {
				saveSettings( );
			}
			
			public void shellDeactivated(ShellEvent arg0) {
			}
			public void shellDeiconified(ShellEvent arg0) {
			}
			public void shellIconified(ShellEvent arg0) {
			}
			public void shellActivated(ShellEvent arg0) {
			}
		} );
		
	}
	
	protected void saveSettings( ) {
		PluginManager pluginManager = Application.getInstance().getPluginManager();
		pluginManager.updateLoadedPlugins( );
		saveAppSettings( );
	}
	
	protected void createContents( ) {
		shell.setLayout( new FillLayout( ) );
		
		Composite paddingContainer = new Composite( shell, SWT.NONE );
		paddingContainer.setLayout( new GridLayout( 1, true ) );
		
		SashForm sash = new SashForm( paddingContainer, SWT.NONE );
		GridData data = new GridData( SWT.FILL, SWT.FILL, true, true );
		data.heightHint = 400;
		sash.setLayoutData( data );
		sash.setLayout( new FillLayout( ) );
		
		// TODO: remove this, make dynamic
		new Label( paddingContainer, SWT.LEFT ).setText( "Note: Newly enabled extensions are available on program restart." );
		
		new Label( paddingContainer, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.BOTTOM, true, false ) );
		
		
		Composite pluginListPadding = new Composite( sash, SWT.NONE );
		pluginListPadding.setLayout( new GridLayout( 1, false ) );
		
		Tree pluginTree = new Tree( pluginListPadding, SWT.BORDER | SWT.SINGLE | SWT.CHECK );
		GridData pluginPadding = new GridData( SWT.FILL, SWT.FILL, true, true );
		pluginPadding.horizontalIndent = 5;
		pluginPadding.verticalIndent = 5;
		pluginTree.setLayoutData( pluginPadding );
		
		
		pluginSettings = new Composite( sash, SWT.NONE );
		pluginSettings.setLayout( new FillLayout( ) );
		
		sash.setWeights( new int[] {1, 4} );
		
		TreeItem appItem = new TreeItem( pluginTree, SWT.NONE );
		appItem.setText( appMenuItemText );
		appItem.setGrayed( true );
		appItem.setChecked( true );
		
		
		PluginManager pluginManager = Application.getInstance().getPluginManager();
		for ( IPlugin plugin : pluginManager.listAllPlugins() ) {
			String pluginName = plugin.getPluginName( );
			TreeItem item = new TreeItem( pluginTree, SWT.NONE );
			item.setText( pluginName );
			item.setData( plugin );
			if ( pluginManager.isEnabled( plugin ) ) {
				item.setChecked( true );
			}
			
			if ( plugin == pluginManager.getDefaultDocumentPlugin() ) {
				item.setChecked( true );
				item.setGrayed( true );
			}
	
		}
		
		pluginTree.addListener( SWT.Selection, new Listener( ) {

			PluginManager pluginManager = Application.getInstance().getPluginManager();
			
			public void handleEvent( Event evt ) {
				Tree tree = (Tree)evt.widget;
				if ( tree.getSelectionCount() > 0 ) {
					TreeItem item = tree.getSelection()[0];
					IPlugin plugin = (IPlugin)item.getData( );
					
					if ( item.getText() == appMenuItemText ) {
						item.setChecked( true );
						setAppPane( );
					} else {
						if ( plugin == pluginManager.getDefaultDocumentPlugin() ) {
							item.setChecked( true );
						}
						
						pluginManager.setEnabled( plugin, item.getChecked( ) );
						if ( item.getChecked( ) ) {
							setPane( plugin );
						} else {
							clearPane( );
						}
					}
				} else {
					clearPane( );
				}
			}
		} );
		
	}
	
	private void clearPane( ) {
		if ( currentPane != null && !currentPane.isDisposed() ) {
			for ( Control child : currentPane.getChildren() ) {
				if ( !child.isDisposed() ) {
					child.dispose( );
				}
			}
			currentPane.dispose( );
		}
		pluginSettings.layout( true );
		shell.layout( true );
	}
	
	private void saveAppSettings( ) {
		PluginManager.saveAppInstanceSettings( );
	}
	
	private void setAppPane( ) {
		clearPane( );
		
		JSONObject settings = PluginManager.getAppInstanceSettings( );
		
		currentPane = new Group( pluginSettings, SWT.BORDER );
		currentPane.setLayout( new GridLayout( 1, true ) );
		((Group)currentPane).setText( "Application Settings" );
		
		new Label( currentPane, SWT.LEFT ).setText( "File explorer:" );
		
		Button singleClickOpen = new Button( currentPane, SWT.CHECK );
		singleClickOpen.setText( "Single-click to open files" );
		
		try {
			if ( settings.getBoolean( SINGLE_CLICK ) ) {
				singleClickOpen.setSelection( true );
			}
		} catch ( JSONException e ) {
			// repair file
			try {
				settings.put( SINGLE_CLICK, false );
			} catch ( JSONException e1 ) {
				e1.printStackTrace();
			}
		}
		
		singleClickOpen.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
			public void widgetSelected( SelectionEvent evt ) {
				Button button = (Button)evt.widget;
				JSONObject settings = PluginManager.getAppInstanceSettings();
				try {
					settings.put( SINGLE_CLICK, button.getSelection() );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
			}
		} );
		
		
		Composite toolbarPlacement = new Composite( currentPane, SWT.NONE );
		toolbarPlacement.setLayout( new RowLayout( SWT.HORIZONTAL ) );
		
		new Label( toolbarPlacement, SWT.LEFT ).setText( "Toolbar Placement: " );
		
		String placement = "Left";
		try {
			if ( settings.has( TOOLBAR ) ) {
				placement = settings.getString( TOOLBAR );
			} else {
				settings.put( TOOLBAR, "Left" );
			}
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		
		SelectionListener placementSelection = new SelectionListener() {
			public void widgetSelected(SelectionEvent evt) {
				Button button = (Button)evt.widget;
				JSONObject settings = PluginManager.getAppInstanceSettings();
				try {
					settings.put( TOOLBAR, button.getText( ) );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
			}
			
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
		};
		
		Button leftButton = new Button( toolbarPlacement, SWT.RADIO );
		leftButton.setText( "Left" );
		leftButton.addSelectionListener( placementSelection );
		
		Button midButton = new Button( toolbarPlacement, SWT.RADIO );
		midButton.setText( "Middle" );
		midButton.addSelectionListener( placementSelection );
		
		Button rightButton = new Button( toolbarPlacement, SWT.RADIO );
		rightButton.setText( "Right" );
		rightButton.addSelectionListener( placementSelection );
		
		if ( placement.equals( "Right" ) ) {
			rightButton.setSelection( true );
		} else if ( placement.equals( "Middle") ) {
			midButton.setSelection( true );
		} else {
			leftButton.setSelection( true );
		}
		
		Button showRecent = new Button( currentPane, SWT.CHECK );
		showRecent.setText( "Show recently opened projects" );
		try {
			if ( settings.getBoolean( SHOW_RECENT ) ) {
				showRecent.setSelection( true );
			}
		} catch ( JSONException e ) {
			// repair
			try {
				settings.put( SHOW_RECENT, true );
				showRecent.setSelection( true );
			} catch ( JSONException e1 ) {
				e1.printStackTrace();
			} finally {
			}
		}
		
		showRecent.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
			public void widgetSelected( SelectionEvent evt ) {
				Button button = (Button)evt.widget;
				JSONObject settings = PluginManager.getAppInstanceSettings();
				try {
					settings.put( SHOW_RECENT, button.getSelection( ) );
					System.out.println( "DONE" );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
			}
		} );
		
		Button clearRecent = new Button( currentPane, SWT.PUSH );
		clearRecent.setText( "Clear recently opened projects list" );
		
		clearRecent.addSelectionListener( new SelectionListener( ) {
			public void widgetDefaultSelected( SelectionEvent evt ) {
			}
			public void widgetSelected( SelectionEvent evt ) {
				JSONObject settings = PluginManager.getAppInstanceSettings();
				try {
					settings.put( "Recent List", new JSONArray( ) );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
			}
		} );
		
		pluginSettings.layout( true );
		currentPane.layout( true );
		shell.layout( true );
	}
	
	
	private void setPane( IPlugin plugin ) {
		clearPane( );
		
		if ( plugin.hasAppPreferences( ) ) {	
			PluginPreferencePane newPane = new PluginPreferencePane( null, pluginSettings, SWT.NONE );
			plugin.createAppPreferences( newPane );
			
			currentPane = newPane;
		} else {
			currentPane = new Group( pluginSettings, SWT.BORDER );
			currentPane.setLayout( new FillLayout( ) );
			((Group)currentPane).setText( plugin.getPluginName() + " settings" );
			new Label( currentPane, SWT.CENTER ).setText( "This extension has no application settings." );
		}

		pluginSettings.layout( true );
		currentPane.layout( true );
		shell.layout( true );
	}
	
	private void createShell( ) {
	    shell = new Shell( getParent( ), getStyle( ) );
	    shell.setText( "Application Settings" );
	    createContents( );
	    shell.pack( );
	    shell.open( );
	}
	
	public void open( ) {
		
		if ( shell == null || shell.isDisposed() ) {
			createShell( );
		}
		
	    Display display = getParent( ).getDisplay( );
	    
	    while ( !shell.isDisposed( ) ) {
	      if ( !display.readAndDispatch( ) ) {
	        display.sleep( );
	      }
	    }
	}

}
