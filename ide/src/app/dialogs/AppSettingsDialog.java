package app.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import app.Application;
import app.plugin.PluginManager;
import app.plugin.interfaces.IPlugin;

public class AppSettingsDialog extends Dialog {

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
		pluginManager.saveLoadedPlugins( );
	}
	
	protected void createContents( ) {
		shell.setLayout( new FillLayout( ) );
		
		Composite paddingContainer = new Composite( shell, SWT.NONE );
		paddingContainer.setLayout( new GridLayout( 1, true ) );
		
		SashForm sash = new SashForm( paddingContainer, SWT.BORDER );
		GridData data = new GridData( SWT.FILL, SWT.FILL, true, true );
		data.minimumHeight = 400;
		sash.setLayoutData( data );
		sash.setLayout( new FillLayout( ) );
		
		// TODO: remove this, make dynamic
		new Label( paddingContainer, SWT.LEFT ).setText( "Note: Enabling tool tabs takes effect on program restart." );
		
		new Label( paddingContainer, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.BOTTOM, true, false ) );
		
		Tree pluginTree = new Tree( sash, SWT.BORDER | SWT.SINGLE | SWT.CHECK );
		
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
		shell.layout( true );
	}
	
	private void setAppPane( ) {
		clearPane( );
		
		currentPane = new Composite( pluginSettings, SWT.BORDER );
		currentPane.setLayout( new FillLayout( ) );
		new Label( currentPane, SWT.CENTER ).setText( "Application Settings" );

		pluginSettings.layout( true );
		currentPane.layout( true );
		shell.layout( true );
	}
	
	
	private void setPane( IPlugin plugin ) {
		clearPane( );
		
		if ( plugin.hasAppPreferences( ) ) {	
			PluginPreferencePane newPane = new PluginPreferencePane( null, pluginSettings, SWT.BORDER );
			plugin.createAppPreferences( newPane );
			
			currentPane = newPane;
		} else {
			currentPane = new Composite( pluginSettings, SWT.BORDER );
			currentPane.setLayout( new FillLayout( ) );
			new Label( currentPane, SWT.CENTER ).setText( "This plugin has no application settings." );
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
