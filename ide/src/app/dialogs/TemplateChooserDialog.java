package app.dialogs;

import java.io.File;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.Application;
import app.plugin.interfaces.IPlugin;

public class TemplateChooserDialog extends Dialog {
	

	private Shell shell;

	private File projectPath;
	
	public TemplateChooserDialog( Shell parentShell, File projectPath ) {
		super( parentShell, SWT.DIALOG_TRIM | SWT.RESIZE );
		
		this.projectPath = projectPath;
	}
	
	protected void createContents( ) {
		
		FillLayout layout = new FillLayout( );
		layout.marginHeight = 10;
		layout.marginWidth  = 10;
		shell.setLayout( layout );
		
		Composite paddingContainer = new Composite( shell, SWT.NONE );
		paddingContainer.setLayout( new GridLayout( 1, true ) );
		
		GridData headerData = new GridData( SWT.FILL, SWT.FILL, true, false );
		
		Label topLabel = new Label( paddingContainer, SWT.LEFT );
		topLabel.setText( "Select a Project Template:" );
		topLabel.setLayoutData( headerData );
		
		Tree treeView = new Tree( paddingContainer, SWT.BORDER | SWT.SINGLE );
		GridData treeViewData = new GridData( SWT.FILL, SWT.FILL, true, true );
		treeView.setLayoutData( treeViewData );
		
		JSONObject appSettings = Application.getInstance( ).getPluginManager( ).getAppSettings( );
		
		try {
			JSONArray templates = appSettings.getJSONArray( "Templates" );
			
			// list all templates
			for ( int i = 0; i < templates.length( ); i++ ) {
				String templateName = templates.getString( i );
				
				TreeItem item = new TreeItem( treeView, SWT.NONE );
				item.setText( templateName );
			}
			
		} catch ( JSONException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO: generate project settings file on submission,
		// or launch settings window on blank
		
		Button button = new Button( paddingContainer, SWT.PUSH );
		button.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, false, false ) );
		button.setText( "Create Project" );
	}
	
	private void createShell( ) {
	    shell = new Shell( getParent( ), getStyle( ) );
	    shell.setText( "New Project from Template" );
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
