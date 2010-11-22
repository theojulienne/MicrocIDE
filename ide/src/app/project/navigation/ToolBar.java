package app.project.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import app.Application;
import app.ImageManager;
import app.plugin.interfaces.IToolBar;
import app.project.ProjectWindow;

public class ToolBar implements IToolBar {

	private ImageManager images;


	private ProjectWindow project;
	private ProjectTreeMenu treeMenu;
	private org.eclipse.swt.widgets.ToolBar toolBarWidget;
	
	public ToolBar( ProjectWindow project, ProjectTreeMenu treeMenu, Composite parent, int style ) {
		toolBarWidget = new org.eclipse.swt.widgets.ToolBar( parent, style );
		
		this.project  = project;
		this.treeMenu = treeMenu;
		
		images = Application.getInstance( ).getImageManager( );

		images.addImage( "project settings", "project_settings.png" );
		images.addImage( "new folder", "folder_add.png" );
		images.addImage( "reload", "reload.png" );
		images.addImage( "new file", "file_add.png" );
		images.addImage( "build", "build.png" );
		images.addImage( "deploy", "deploy.png" );
		images.addImage( "stop", "stop.png" );

		ToolItem reloadButton = new ToolItem( toolBarWidget, SWT.PUSH );
		reloadButton.setImage( images.getImage( "reload" ) );
		reloadButton.setToolTipText( "Reload Project" );
		reloadButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				update( );
			}
		} );
		
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
		
		ToolItem projectSettingsButton = new ToolItem( toolBarWidget, SWT.PUSH );
		projectSettingsButton.setImage( images.getImage( "project settings") );
		projectSettingsButton.setToolTipText( "Project Settings" );
		projectSettingsButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				openProjSettings( );
			}
		} );
		
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
		
		ToolItem createDirButton = new ToolItem( toolBarWidget, SWT.PUSH );
		createDirButton.setImage( images.getImage( "new folder" ) );
		createDirButton.setToolTipText( "New Folder" );
		createDirButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createDir( );
			}
		} );
		
		ToolItem createFileButton = new ToolItem( toolBarWidget, SWT.PUSH );
		createFileButton.setImage( images.getImage( "new file") );
		createFileButton.setToolTipText( "New File" );
		createFileButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createFile( );
			}
		} );
		
		/*
		new ToolItem( toolBar, SWT.SEPARATOR );
		
		buildButton = new ToolItem( toolBar, SWT.PUSH );
		buildButton.setImage( images.getImage( "build") );
		buildButton.setToolTipText( "Build Project" );
		buildButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				Boolean building = (Boolean) e.widget.getData();
				
				if ( building != null && building) {
					stopBuild( );
				} else {
					build( );
				}
			}
		} );		
		
		deployButton = new ToolItem( toolBar, SWT.PUSH );
		deployButton.setImage( images.getImage( "deploy") );
		deployButton.setToolTipText( "Deploy Project" );
		deployButton.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {

				Boolean deploying = (Boolean) e.widget.getData();
				if ( deploying != null && deploying) {
					stopDeploy( );
				} else {
					deploy( );
				}
			}
		} );
		*/
	}
	
	public void setEnabled( boolean enabled ) {
		this.toolBarWidget.setEnabled( enabled );
	}

	private void update( ) {
		treeMenu.update( );
	}
	
	private void createDir( ) {
		treeMenu.createDir( treeMenu.getSelectedDir() );
	}
	
	private void createFile( ) {
		treeMenu.createFile( treeMenu.getSelectedDir( ) );
	}
	
	/*
	private void build( ) {
		project.build( );
	}
	
	private void deploy( ) {
		project.deploy( );
	}
	
	private void stopBuild( ) {
		project.stopBuild( );
	}
	
	private void stopDeploy( ) {
		project.stopDeploy( );
	}
	*/
	
	private void openProjSettings( ) {
		project.openProjectSettings( );
	}
	
	
	
	
	
	public ToolItem addToolItem( Image image, String toolTip,
			SelectionListener selectionListener) {
		ToolItem customItem = new ToolItem( toolBarWidget, SWT.PUSH );
		customItem.setImage( image );
		customItem.setToolTipText( toolTip );
		customItem.addSelectionListener( selectionListener );
		
		return customItem;
	}
	
	public void addSeparator( ) {
		new ToolItem( toolBarWidget, SWT.SEPARATOR );
	}

	public void setLayoutData(GridData toolBarData) {
		this.toolBarWidget.setLayoutData( toolBarData );
	}


}
