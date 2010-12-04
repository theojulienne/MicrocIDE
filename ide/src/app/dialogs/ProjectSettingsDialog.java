package app.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import app.Application;
import app.plugin.interfaces.IPlugin;
import app.project.ProjectWindow;

public class ProjectSettingsDialog extends Dialog {

	private Shell shell;
	private ProjectWindow project;
	
	public ProjectSettingsDialog( Shell parentShell, ProjectWindow project ) {
		super( parentShell, SWT.DIALOG_TRIM | SWT.RESIZE );
		
		this.project = project;
	}
	
	protected void createContents( ) {
		shell.setLayout( new FillLayout( ) );
		
		Composite paddingContainer = new Composite( shell, SWT.NONE );
		paddingContainer.setLayout( new GridLayout( 1, true ) );
		
		CTabFolder pluginTabs = new CTabFolder( paddingContainer, SWT.TOP );
		pluginTabs.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
		pluginTabs.setBorderVisible( true );
		pluginTabs.setSimple( true );
		pluginTabs.setMaximizeVisible( false );
		pluginTabs.setMinimizeVisible( false );
		
		new Label( paddingContainer, SWT.NONE ).setLayoutData( new GridData( SWT.FILL, SWT.BOTTOM, true, false ) );
		
		for ( IPlugin plugin : Application.getInstance().getPluginManager().listLoadedPlugins() ) {
			// create tabs for all project preferences
			if ( plugin.hasProjectPreferences() ) {
				PluginPreferencePane pane = new PluginPreferencePane( project, pluginTabs, SWT.NONE );
				plugin.createProjectPreferences( pane );
				
				CTabItem item = new CTabItem( pluginTabs, SWT.NONE );
				item.setText( plugin.getPluginName() );
				item.setControl( pane.getComposite( ) );
			}
		}
		
		if ( pluginTabs.getItemCount() > 0 ) {
			pluginTabs.setSelection( 0 );
		}
	}
	
	private void createShell( ) {
	    shell = new Shell( getParent( ), getStyle( ) );
	    shell.setText( "Project Settings" );
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
